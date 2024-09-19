package minigames.server;

import io.vertx.core.buffer.*;
import io.vertx.core.eventbus.Message;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.http.*;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Arrays;
import java.util.List;

import minigames.server.api.leaderboard.LeaderboardService;
import minigames.server.api.leaderboard.LeaderboardServiceImpl;
import minigames.server.database.repositories.LeaderboardRepositoryImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import minigames.commands.CommandPackage;
import minigames.rendering.GameMetadata;
import minigames.rendering.RenderingPackage;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.auth.User;
import minigames.server.database.DatabaseTables;
import minigames.server.database.DatabaseUtils;
import minigames.server.database.DatabaseSeeder;
import minigames.server.database.repositories.UserRepositoryImpl;
import minigames.server.database.repositories.LeaderboardRepositoryImpl;
import minigames.server.database.repositories.ProfileRepositoryImpl;
import minigames.server.database.repositories.LeaderboardRepositoryImpl;
import minigames.server.api.achievement.Achievement;
import minigames.server.api.auth.AuthProvider;
import minigames.server.api.leaderboard.LeaderboardService;
import minigames.server.api.leaderboard.LeaderboardServiceImpl;
import minigames.server.api.profile.ProfileService;
import minigames.server.api.profile.ProfileServiceImpl;
import minigames.server.api.user.UserService;
import minigames.server.api.user.UserServiceImpl;

public class MinigameNetworkServer {

  /** A logger for logging output */
  private static final Logger logger = LogManager.getLogger(MinigameNetworkServer.class);

  private final Vertx vertx;
  private final HttpServer server;
  private final Router router;

  private DatabaseSeeder databaseSeeder;
  private final AuthProvider authProvider;
  private final JWTAuth jwtAuth;

  private final UserService userService;
  private final ProfileService profileService;
  private final LeaderboardService leaderboardService;

  private final UserRepositoryImpl userRepository;
  private final ProfileRepositoryImpl profileRepository;
  private final LeaderboardRepositoryImpl leaderboardRepository;

  private final ScheduledExecutorService executorService;

  public MinigameNetworkServer(Vertx vertx) {
    this.vertx = vertx;
    this.server = vertx.createHttpServer();
    this.router = Router.router(vertx);

    // Init JWT auth
    JWTAuthOptions jwtAuthOptions = new JWTAuthOptions()
        .addPubSecKey(new PubSecKeyOptions()
            .setAlgorithm("HS256")
            .setBuffer("need-to-move-this-secret"));
    this.jwtAuth = JWTAuth.create(vertx, jwtAuthOptions);

    this.userRepository = new UserRepositoryImpl();
    this.profileRepository = new ProfileRepositoryImpl();
    this.leaderboardRepository = new LeaderboardRepositoryImpl();

    this.databaseSeeder = new DatabaseSeeder(userRepository, profileRepository, leaderboardRepository);
    this.userService = new UserServiceImpl(userRepository, jwtAuth);
    this.profileService = new ProfileServiceImpl(profileRepository);
    this.leaderboardService = new LeaderboardServiceImpl(leaderboardRepository);
    this.authProvider = new AuthProvider(userRepository);

    this.executorService = Executors.newSingleThreadScheduledExecutor(); // run background process

    if (!DatabaseUtils.connectToDatabase()) {
      logger.info("Database failed to launch");
    } else {
      logger.info("Database launched");
      DatabaseTables.createTables();
      databaseSeeder.seedDatabase();
    }
  }

  /** Starts the server on the given port */
  public void start(int port) {
    router.route()
        .handler(CorsHandler.create().allowedMethod(HttpMethod.POST))
        .handler(BodyHandler.create());

    // added webSocketHandler
    server.webSocketHandler((ctx) -> {
      ctx.binaryMessageHandler(bin -> {
        handleWebSocketRequest(ctx, bin);
      });

    });

    // * Force CORS to respond that everything is ok
    // * router.options().handler((ctx) -> {
    // * ctx.response().putHeader("Acces-Control-Allow-Origin", "*");
    // * ctx.response().end("");
    // * });
    // */

    // A basic ping route to check if there is contact
    router.get("/ping").handler((ctx) -> {
      ctx.response().end("pong");
    });

    // Basic authentication route
    router.post("/login").handler((ctx) -> {
      logger.info("Login route hit");
      JsonObject body = ctx.body().asJsonObject();

      authProvider.authenticate(body, res -> {
        if (res.succeeded()) {
          User user = res.result();
          String token = jwtAuth.generateToken(
              new JsonObject().put("username", user.principal().getString("username")),
              new JWTOptions().setExpiresInMinutes(60));
          ctx.response()
              .setStatusCode(200)
              .putHeader("Content-Type", "application/json")
              .end(new JsonObject().put("token", token).encode());
          logger.info("Authentication successful");
        } else {
          logger.error("Authentication failed", res.cause());
          logger.error("Authentication failed", res.cause());
          ctx.response()
              .setStatusCode(401)
              .putHeader("Content-Type", "application/json")
              .end(new JsonObject().put("error", "Authentication failed").encode());
        }
      });
    });

    // Gets the list of game servers for this client type
    router.get("/gameServers/:clientType").respond((ctx) -> {
      String clientStr = ctx.pathParam("clientType");
      ClientType ct = ClientType.valueOf(clientStr);
      List<GameServer> servers = Main.gameRegistry.getGamesForPlatform(ct);

      /**
       * Vertx/Jackson should turn this into a JSON list, because we're just outputing
       * a simple List<record>
       */
      return Future.succeededFuture(servers.stream().map((gs) -> gs.getDetails()).toList());
    });

    // Gets the list of game servers for this client type
    router.get("/games/:gameServer").respond((ctx) -> {
      String serverName = ctx.pathParam("gameServer");
      GameServer gs = Main.gameRegistry.getGameServer(serverName);
      GameMetadata[] games = gs.getGamesInProgress();

      /**
       * Vertx/Jackson should turn this into a JSON list, because we're just outputing
       * a simple List<record>
       */
      return Future.succeededFuture(Arrays.asList(games));
    });

    // Starts a new game on the server
    router.post("/newGame/:gameServer").respond((ctx) -> {
      String serverName = ctx.pathParam("gameServer");
      GameServer gs = Main.gameRegistry.getGameServer(serverName);

      String playerName = ctx.body().asString();

      /*
       * executeBlocking moves this onto a background thread
       */
      Future<RenderingPackage> resp = vertx.executeBlocking((promise) -> gs.newGame(playerName).onSuccess((r) -> {
        logger.info("package {}", r);
        promise.complete(r);
      }));
      return resp;
    });

    // Joins a game on the server
    router.post("/joinGame/:gameServer/:game").respond((ctx) -> {
      logger.info("A New player has joined");

      String serverName = ctx.pathParam("gameServer");
      String gameName = ctx.pathParam("game");
      GameServer gs = Main.gameRegistry.getGameServer(serverName);

      String playerName = ctx.body().asString();
      /*
       * executeBlocking moves this onto a background thread
       */
      Future<RenderingPackage> resp = vertx
          .executeBlocking((promise) -> gs.joinGame(gameName, playerName).onSuccess((r) -> {
            logger.info("package {}", r);
            promise.complete(r);
          }));
      return resp;
    });

    // Sends a command package to a game on the server
    router.post("/command").respond((ctx) -> {
      JsonObject data = ctx.body().asJsonObject();
      CommandPackage cp = CommandPackage.fromJson(data);

      GameServer gs = Main.gameRegistry.getGameServer(cp.gameServer());

      /*
       * executeBlocking moves this onto a background thread
       */
      Future<RenderingPackage> resp = vertx.executeBlocking((promise) -> gs.callGame(cp).onSuccess((r) -> {
        logger.info("package {}", r);
        promise.complete(r);
      }));
      return resp;
    });

    router.post("/api/login").handler(this::loginUser);
    router.post("/api/register").handler(this::registerUser);

    router.post("/api/send-email").handler((ctx) -> {
      JsonObject emailDetails = ctx.body().asJsonObject();
      logger.info("/send-email route hit. Email details: {}", emailDetails);
      vertx.eventBus().<JsonObject>request("email.send", emailDetails, ar -> {
        if (ar.succeeded()) {
          Message<JsonObject> reply = ar.result();
          logger.info("Email sent successfully");
          ctx.response()
            .setStatusCode(200)
            .putHeader("Content-Type", "application/json")
            .end(new JsonObject().put("message", "Email sent successfully").encode());
        } else {
          logger.error("Failed to send email", ar.cause());
          ctx.response()
            .setStatusCode(500)
            .putHeader("Content-Type", "application/json")
            .end(new JsonObject().put("message", "Failed to send email").encode());
        }
        });
    });

    router.get("/api/get-username/:email").handler((ctx) -> {
      logger.info("/get-username route hit.");
      String email = ctx.pathParam("email");

      userService.getUsernameFromEmail(email)
        .onSuccess(username -> {
          logger.info("Username found: {}", username);
          ctx.response()
            .setStatusCode(200)
            .putHeader("Content-Type", "application/json")
            .end(username);
        })
        .onFailure(err -> {
          logger.error("Failed to retrieve username", err);
          ctx.response()
            .setStatusCode(404)
            .putHeader("Content-Type", "application/json")
            .end("Username not found");
        });
    });

    router.get("/api/user").handler(this::getCurrentUser);
    router.get("/api/user/:username").handler(this::getUserData);
    router.post("/api/user/update/:username").handler(this::updateUser);
    router.get("/api/users").handler(this::getAllUsers);

    router.get("/api/profile/:username").handler(this::getProfile);
    router.post("/api/profile/:username").handler(this::updateProfile);
    router.put("/api/profile/:username").handler(this::createProfile);
    router.post("/api/profile/:username/playtime").handler(this::incrementPlayTime);

    router.get("/api/achievements").handler(this::getAllAchievements);
    router.get("/api/achievements/:achievementId").handler(this::getAchievement);
    router.get("/api/users/:username/achievements").handler(this::getUserAchievements);
    router.post("/api/users/:username/achievements/:achievementId").handler(this::unlockAchievement);
    router.post("/api/users/:username/achievements/name/:achievementName").handler(this::unlockAchievementByName);
    router.post("/api/achievements").handler(this::addAchievement);

    router.post("/api/leaderboard/scores").handler(this::addLeaderboardScore);
    router.get("/api/leaderboard/top/:gameType/:limit").handler(this::getTopLeaderboardScores);
    router.get("/api/leaderboard/:gameType").handler(this::getAllLeaderboardScores);

    server.requestHandler(router).listen(port, (http) -> {
      if (http.succeeded()) {
        logger.info("Server started on {}", port);
      } else {
        logger.error("Server failed to start");
      }
    });
  }

  /**
   * Get game server who runs the commond "gameState" and constantly push
   * notifications to the client
   * 
   * @param ctx
   * @param bin
   */
  private void handleWebSocketRequest(ServerWebSocket ctx, Buffer bin) {
    var comms = CommandPackage.fromJson(new JsonObject(bin));
    GameServer gs = Main.gameRegistry.getGameServer(comms.gameServer());

    executorService.scheduleAtFixedRate(
        new Runnable() {
          @Override
          public void run() {
            gs.callGame(comms).onSuccess((rp) -> {
              ctx.write(JsonObject.mapFrom(rp).toBuffer());
            });
          }
        }, 0, 10, TimeUnit.MILLISECONDS);
  }

  private void registerUser(RoutingContext ctx) {
    JsonObject body = ctx.body().asJsonObject();
    String username = body.getString("username");
    String password = body.getString("password");
    String email = body.getString("email");

    if (username == null || password == null || email == null) {
      handleBadRequest(ctx, "Missing required fields: username, password, and email");
      return;
    }

    userService.registerUser(username, password, email)
        .onSuccess(token -> {
          logger.info("User registered: {}", username);
          JsonObject responseBody = new JsonObject()
              .put("username", username)
              .put("token", token);

          ctx.response()
              .setStatusCode(201)
              .putHeader("Content-Type", "application/json")
              .end(responseBody.encode());
        })
        .onFailure(err -> handleDetailedError(ctx, err, "Failed to register user"));
  }

  private void loginUser(RoutingContext ctx) {
    JsonObject body = ctx.body().asJsonObject();
    String username = body.getString("username");
    String password = body.getString("password");

    if (username == null || password == null) {
      handleBadRequest(ctx, "Missing required fields: username and password");
      return;
    }

    userService.loginUser(username, password)
        .onSuccess(token -> {
          ctx.response()
              .setStatusCode(200)
              .putHeader("Content-Type", "application/json")
              .end(new JsonObject().put("token", token).encode());
        })
        .onFailure(err -> handleDetailedError(ctx, err, "Authentication failed"));
  }

  private void getCurrentUser(RoutingContext ctx) {
    User user = ctx.user();
    if (user == null) {
      handleUnauthorized(ctx, "User not authenticated");
      return;
    }
    ctx.response()
        .putHeader("Content-Type", "application/json")
        .end(user.principal().encode());
  }

  private void getUserData(RoutingContext ctx) {
    String username = ctx.pathParam("username");
    userService.getUserData(username)
        .onSuccess(userData -> {
          ctx.response()
              .setStatusCode(200)
              .putHeader("Content-Type", "application/json")
              .end(userData.encode());
        })
        .onFailure(err -> {
          if (err.getMessage().contains("User not found")) {
            handleNotFound(ctx, "User not found: " + username);
          } else {
            handleDetailedError(ctx, err, "Failed to retrieve user data");
          }
        });
  }

  private void updateUser(RoutingContext ctx) {
    String username = ctx.pathParam("username");
    JsonObject body = ctx.body().asJsonObject();
    logger.info("Updating user {}", username);
    logger.info("Updated details: {}", body);

    userService.updateUser(username, body)
        .compose(v -> userService.getUserData(username))
        .onSuccess(updatedUser -> {
          ctx.response()
              .setStatusCode(200)
              .putHeader("Content-Type", "application/json")
              .end(updatedUser.encode());
        })
        .onFailure(err -> {
          if (err.getMessage().contains("User not found")) {
            handleNotFound(ctx, "User not found: " + username);
          } else {
            handleDetailedError(ctx, err, "Failed to update user");
          }
        });
  }

  private void getAllUsers(RoutingContext ctx) {
    userService.getAllUsers()
        .onSuccess(users -> {
          ctx.response()
              .setStatusCode(200)
              .putHeader("Content-Type", "application/json")
              .end(new JsonArray(users).encode());
        })
        .onFailure(err -> handleDetailedError(ctx, err, "Failed to retrieve users"));
  }

  private void handleUnauthorized(RoutingContext ctx, String message) {
    ctx.response()
        .setStatusCode(401)
        .putHeader("Content-Type", "application/json")
        .end(new JsonObject()
            .put("error", "Unauthorized")
            .put("message", message)
            .encode());
  }

  private void getProfile(RoutingContext ctx) {
    String username = ctx.pathParam("username");
    profileService.getProfile(username)
        .onSuccess(profile -> {
          ctx.response()
              .setStatusCode(200)
              .putHeader("Content-Type", "application/json")
              .end(profile.encode());
        })
        .onFailure(err -> {
          ctx.response()
              .setStatusCode(404)
              .putHeader("Content-Type", "application/json")
              .end(new JsonObject().put("error", "User profile not found").encode());
        });
  }

  private void updateProfile(RoutingContext ctx) {
    String username = ctx.pathParam("username");
    JsonObject profileContent = ctx.body().asJsonObject();
    logger.info("Updating profile for user {}", username);
    logger.info("Updated details: {}", profileContent);
    profileService.updateProfile(username, profileContent)
        .onSuccess(v -> {
          ctx.response()
              .setStatusCode(200)
              .putHeader("Content-Type", "application/json")
              .end(new JsonObject().put("message", "Profile updated successfully").encode());
        })
        .onFailure(err -> {
          ctx.response()
              .setStatusCode(404)
              .putHeader("Content-Type", "application/json")
              .end(new JsonObject().put("error", "User not found or update failed").encode());
        });
  }

  private void createProfile(RoutingContext ctx) {
    String username = ctx.pathParam("username");
    JsonObject profileContent = ctx.body().asJsonObject();
    logger.info("Creating profile for user {}", username);
    logger.info("Profile details: {}", profileContent);
    profileService.createProfile(username, profileContent)
        .onSuccess(v -> {
          ctx.response()
              .setStatusCode(201)
              .putHeader("Content-Type", "application/json")
              .end(new JsonObject().put("message", "Profile created successfully").encode());
        })
        .onFailure(err -> {
          ctx.response()
              .setStatusCode(400)
              .putHeader("Content-Type", "application/json")
              .end(new JsonObject().put("error", "Failed to create profile").encode());
        });
  }

  private void incrementPlayTime(RoutingContext ctx) {
    String username = ctx.pathParam("username");
    JsonObject body = ctx.getBodyAsJson();

    if (body == null || !body.containsKey("minutes")) {
      ctx.response()
          .setStatusCode(400)
          .putHeader("Content-Type", "application/json")
          .end(new JsonObject().put("error", "Invalid request body").encode());
      return;
    }

    long minutes;
    try {
      // Handle both number and string representations of minutes
      Object minutesObj = body.getValue("minutes");
      if (minutesObj instanceof Number) {
        minutes = ((Number) minutesObj).longValue();
      } else if (minutesObj instanceof String) {
        minutes = Long.parseLong((String) minutesObj);
      } else {
        throw new IllegalArgumentException("Invalid minutes format");
      }
    } catch (IllegalArgumentException e) {
      ctx.response()
          .setStatusCode(400)
          .putHeader("Content-Type", "application/json")
          .end(new JsonObject().put("error", "Invalid minutes format").encode());
      return;
    }

    logger.info("Incrementing playtime for user {}", username);
    logger.info("Adding playtime: {}", minutes);

    profileService.incrementPlayTime(username, minutes)
        .onSuccess(v -> {
          ctx.response()
              .setStatusCode(200)
              .putHeader("Content-Type", "application/json")
              .end(new JsonObject().put("message", "Playtime incremented successfully").encode());
        })
        .onFailure(err -> {
          ctx.response()
              .setStatusCode(404)
              .putHeader("Content-Type", "application/json")
              .end(new JsonObject().put("error", "User not found or update failed").encode());
        });
  }

  private void getAllAchievements(RoutingContext ctx) {
    userService.getAllAchievements()
        .onSuccess(achievements -> {
          ctx.response()
              .setStatusCode(200)
              .putHeader("Content-Type", "application/json")
              .end(achievements.encode());
        })
        .onFailure(err -> handleError(ctx, err));
  }

  private void getAchievement(RoutingContext ctx) {
    String achievementId = ctx.pathParam("achievementId");
    userService.getAchievement(achievementId)
        .onSuccess(achievement -> {
          if (achievement != null) {
            ctx.response()
                .setStatusCode(200)
                .putHeader("Content-Type", "application/json")
                .end(achievement.encode());
          } else {
            ctx.response()
                .setStatusCode(404)
                .putHeader("Content-Type", "application/json")
                .end(new JsonObject().put("error", "Achievement not found").encode());
          }
        })
        .onFailure(err -> handleError(ctx, err));
  }

  private void getUserAchievements(RoutingContext ctx) {
    String username = ctx.pathParam("username");
    userService.getUserAchievements(username)
        .onSuccess(achievements -> {
          ctx.response()
              .setStatusCode(200)
              .putHeader("Content-Type", "application/json")
              .end(achievements.encode());
        })
        .onFailure(err -> handleError(ctx, err));
  }

  private void unlockAchievement(RoutingContext ctx) {
    String username = ctx.pathParam("username");
    String achievementId = ctx.pathParam("achievementId");
    userService.unlockAchievement(username, achievementId)
        .onSuccess(v -> {
          ctx.response()
              .setStatusCode(200)
              .putHeader("Content-Type", "application/json")
              .end(new JsonObject().put("message", "Achievement unlocked successfully").encode());
        })
        .onFailure(err -> handleError(ctx, err));
  }

  private void addAchievement(RoutingContext ctx) {
    JsonObject body = ctx.body().asJsonObject();
    Achievement achievement = new Achievement(
        null,
        body.getString("game"),
        body.getString("name"),
        body.getString("description"),
        body.getInteger("points"),
        null,
        body.getString("image"));
    userService.addAchievement(achievement)
        .onSuccess(v -> {
          ctx.response()
              .setStatusCode(201)
              .putHeader("Content-Type", "application/json")
              .end(new JsonObject().put("message", "Achievement added successfully").encode());
        })
        .onFailure(err -> handleError(ctx, err));
  }

  private void handleError(RoutingContext ctx, Throwable err) {
    logger.error("Error occurred: ", err);
    ctx.response()
        .setStatusCode(500)
        .putHeader("Content-Type", "application/json")
        .end(new JsonObject().put("error", err.getMessage()).encode());
  }

  private void handleDetailedError(RoutingContext ctx, Throwable err, String message) {
    logger.error(message, err);
    int statusCode = (err instanceof IllegalArgumentException) ? 400 : 500;
    String errorDetails = (err.getMessage() != null) ? err.getMessage() : "No additional details available";
    ctx.response()
        .setStatusCode(statusCode)
        .putHeader("Content-Type", "application/json")
        .end(new JsonObject()
            .put("error", message)
            .put("details", errorDetails)
            .encode());
  }

  private void handleNotFound(RoutingContext ctx, String message) {
    ctx.response()
        .setStatusCode(404)
        .putHeader("Content-Type", "application/json")
        .end(new JsonObject()
            .put("error", "Not Found")
            .put("message", message)
            .encode());
  }

  private void handleBadRequest(RoutingContext ctx, String message) {
    ctx.response()
        .setStatusCode(400)
        .putHeader("Content-Type", "application/json")
        .end(new JsonObject()
            .put("error", "Bad Request")
            .put("message", message)
            .encode());
  }

  private void unlockAchievementByName(RoutingContext ctx) {
    String username = ctx.pathParam("username");
    String achievementName = ctx.pathParam("achievementName");
    logger.info("Attempting to unlock achievement '{}' for user '{}'", achievementName, username);

    userService.unlockAchievementByName(username, achievementName)
        .onSuccess(v -> {
          logger.info("Successfully unlocked achievement '{}' for user '{}'", achievementName, username);
          ctx.response()
              .setStatusCode(200)
              .putHeader("Content-Type", "application/json")
              .end(new JsonObject().put("message", "Achievement unlocked successfully").encode());
        })
        .onFailure(err -> {
          logger.error("Failed to unlock achievement '{}' for user '{}': {}", achievementName, username,
              err.getMessage());
          handleDetailedError(ctx, err, "Failed to unlock achievement");
        });
  }

  private void addLeaderboardScore(RoutingContext ctx) {
    JsonObject body = ctx.body().asJsonObject();
    String name = body.getString("name");
    int score = body.getInteger("score");
    String gameType = body.getString("gameType");

    leaderboardService.addScore(name, score, gameType).onSuccess(v -> {
      ctx.response()
              .setStatusCode(200)
              .putHeader("Content-Type", "application/json")
              .end(new JsonObject().put("message", "Score added successfully").encodePrettily());
    }).onFailure(err -> {
      ctx.response()
              .setStatusCode(500)
              .putHeader("Content-Type", "application/json")
              .end(new JsonObject().put("error", err.getMessage()).encodePrettily());
    });
  }

  private void getTopLeaderboardScores(RoutingContext ctx) {
    String gameType = ctx.pathParam("gameType");
    int limit = Integer.parseInt(ctx.pathParam("limit"));

    leaderboardService.getTopScores(gameType, limit).onSuccess(scores -> {
      ctx.response()
              .setStatusCode(200)
              .putHeader("Content-Type", "application/json")
              .end(scores.encodePrettily());
    }).onFailure(err -> {
      ctx.response()
              .setStatusCode(500)
              .putHeader("Content-Type", "application/json")
              .end(new JsonObject().put("error", err.getMessage()).encodePrettily());
    });
  }

  private void getAllLeaderboardScores(RoutingContext ctx) {
    String gameType = ctx.pathParam("gameType");

    leaderboardService.getAllScores(gameType).onSuccess(scores -> {
      ctx.response()
              .setStatusCode(200)
              .putHeader("Content-Type", "application/json")
              .end(scores.encodePrettily());
    }).onFailure(err -> {
      ctx.response()
              .setStatusCode(500)
              .putHeader("Content-Type", "application/json")
              .end(new JsonObject().put("error", err.getMessage()).encodePrettily());
    });
  }


}