package minigames.client;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import javax.swing.JLabel;

import io.vertx.core.http.WebSocket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.http.HttpClient;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.HttpResponse;
import minigames.commands.CommandPackage;
import minigames.rendering.GameMetadata;
import minigames.rendering.GameServerDetails;
import minigames.rendering.NativeCommands.LoadClient;
import minigames.rendering.NativeCommands.QuitToMenu;
import minigames.rendering.NativeCommands.ShowMenuError;
import minigames.rendering.RenderingPackage;

/**
 * The central cub of the client.
 * 
 * GameClients will be given a reference to this.
 * From this, they can get the main window, to set up their UI
 * They can get the Animator, to register for ticks
 * They gan get a reference to Vertx, for starting any other verticles they
 * might want (though most won't)
 */
public class MinigameNetworkClient {

    /** A logger for logging output */
    private static final Logger logger = LogManager.getLogger(MinigameNetworkClient.class);

    /**
     * Host to connect to. Updated from Main.
     */
    public static String host = "localhost";

    /**
     * Port to connect to. Updated from Main.
     */
    public static int port = 8080;

    Vertx vertx;
    WebClient webClient;
    MinigameNetworkClientWindow mainWindow;
    Animator animator;
    HttpClient client;

    Optional<GameClient> gameClient;

    String storedToken;

    public MinigameNetworkClient(Vertx vertx) {
        this.vertx = vertx;
        this.webClient = WebClient.create(vertx);
        this.gameClient = Optional.empty();
        this.client = vertx.createHttpClient();

        animator = new Animator();
        vertx.setPeriodic(16, (id) -> animator.tick());

        mainWindow = new MinigameNetworkClientWindow(this);
        mainWindow.show();
    }

    /** Get a reference to the Vertx instance */
    public Vertx getVertx() {
        return this.vertx;
    }

    /** Get a reference to the main window */
    public MinigameNetworkClientWindow getMainWindow() {
        return this.mainWindow;
    }

    /** Get a reference to the animator */
    public Animator getAnimator() {
        return this.animator;
    }

    /** Sends a ping to the server and logs the response */
    public Future<String> ping() {
        return webClient.get(port, host, "/ping")
                .send()
                .onSuccess((resp) -> {
                    logger.info(resp.bodyAsString());
                })
                .onFailure((resp) -> {
                    logger.error("Failed: {} ", resp.getMessage());
                }).map((resp) -> resp.bodyAsString());
    }

    /** Get the list of GameServers that are supported for this client type */
    public Future<List<GameServerDetails>> getGameServers() {
        return webClient.get(port, host, "/gameServers/Swing")
                .send()
                .onSuccess((resp) -> {
                    logger.info(resp.bodyAsString());
                })
                .map((resp) -> resp.bodyAsJsonArray()
                        .stream()
                        .map((j) -> ((JsonObject) j).mapTo(GameServerDetails.class))
                        .toList())
                .onFailure((resp) -> {
                    logger.error("Failed: {} ", resp.getMessage());
                });
    }

    /**
     * Authenticate user via username and password (currently plain text basic auth)
     */
    public Future<HttpResponse<Buffer>> login(String username, String password) {
        return webClient.post(port, host, "/login")
                .sendJsonObject(
                        new JsonObject()
                                .put("username", username)
                                .put("password", password))
                .onSuccess((resp) -> {
                    logger.info("Logged in: {} ", resp.bodyAsString());
                    String token = resp.bodyAsJsonObject().getString("token");
                    setStoredToken(token);
                    mainWindow.updateNavigationBar();
                })
                .onFailure((resp) -> {
                    logger.error("Failed: {} ", resp.getMessage());
                });
    }

    public Future<HttpResponse<Buffer>> register(JsonObject body) {
        return webClient.post(port, host, "/api/register")
                .sendJsonObject(body)
                .onSuccess((resp) -> {
                    String token = resp.bodyAsJsonObject().getString("token");
                    setStoredToken(token);
                    mainWindow.updateNavigationBar();
                    System.out.println("got to here in registration");
                })
                .onFailure((resp) -> {
                    logger.error("Failed: {} ", resp.getMessage());
                });
    }

    /**
     * Get the metadata for all games currently running for a particular gameServer
     */
    public Future<List<GameMetadata>> getGameMetadata(String gameServer) {
        return webClient.get(port, host, "/games/" + gameServer)
                .send()
                .onSuccess((resp) -> {
                    logger.info(resp.bodyAsString());
                })
                .map((resp) -> resp.bodyAsJsonArray()
                        .stream()
                        .map((j) -> ((JsonObject) j).mapTo(GameMetadata.class))
                        .toList())
                .onFailure((resp) -> {
                    logger.error("Failed: {} ", resp.getMessage());
                });
    }

    /** Creates a new game on the server, running any commands that come back */
    public Future<RenderingPackage> newGame(String gameServer, String playerName) {
        return webClient.post(port, host, "/newGame/" + gameServer)
                .sendBuffer(Buffer.buffer(playerName))
                .onSuccess((resp) -> {
                    logger.info(resp.bodyAsString());
                })
                .map((resp) -> {
                    JsonObject rpj = resp.bodyAsJsonObject();
                    return RenderingPackage.fromJson(rpj);
                })
                .onSuccess((rp) -> {
                    runRenderingPackage(rp);
                    if (Main.clientRegistry.isForWebSockets(gameServer)) {
                        handleWebSocketRequest(rp, playerName);
                    }
                })
                .onFailure((resp) -> {
                    logger.error("Failed: {} ", resp.getMessage());
                });
    }

    /** Joins a game on the server, running any commands that come back */
    public Future<RenderingPackage> joinGame(String gameServer, String game, String playerName) {
        logger.info("Joining game {} on server {}", game, gameServer);
        logger.info("Player name: {}", playerName);
        return webClient.post(port, host, "/joinGame/" + gameServer + "/" + game)
                .sendBuffer(Buffer.buffer(playerName))
                .onSuccess((resp) -> {
                    logger.info(resp.bodyAsString());
                })
                .map((resp) -> {
                    JsonObject rpj = resp.bodyAsJsonObject();
                    return RenderingPackage.fromJson(rpj);
                })
                .onSuccess((rp) -> {
                    runRenderingPackage(rp);
                    if (Main.clientRegistry.isForWebSockets(gameServer)) { // if the game wants to use websocket
                        handleWebSocketRequest(rp, playerName);
                    }
                })
                .onFailure((resp) -> {
                    logger.error("Failed: {} ", resp.getMessage());
                });
    }

    /** Sends a CommandPackage to the server, running any commands that come back */
    public Future<RenderingPackage> send(CommandPackage cp) {
        return webClient.post(port, host, "/command")
                .sendJson(cp)
                .onSuccess((resp) -> {
                    logger.info(resp.bodyAsString());
                })
                .map((resp) -> {
                    JsonObject rpj = resp.bodyAsJsonObject();
                    return RenderingPackage.fromJson(rpj);
                })
                .onSuccess((rp) -> runRenderingPackage(rp))
                .onFailure((resp) -> {
                    logger.error("Failed: {} ", resp.getMessage());
                });
    }

    /**
     * Runs the sequence that opens the main menu - starting with a title card
     * before communicating with
     * the server to get a list of available games.
     */
    public void runMainMenuSequence() {
        mainWindow.showStarfieldMessage("Minigame Network");

        ping().flatMap((s) -> getGameServers()).map((list) -> {
            logger.info("Got servers {}", list);
            return list;
        }).map((l) -> {
            mainWindow.showGameServers(l);
            // TODO: Add our button to main window
            return l;
        });
    }

        /** Executes the QuitToMenu command */
    public void execute(QuitToMenu qtm) {
        gameClient.ifPresent((gc) -> gc.closeGame());
        gameClient = Optional.empty();

        runMainMenuSequence();
    }

    /** Executes a LoadClient command */
    private void execute(GameMetadata metadata, LoadClient lc) {
        logger.info("Loading client {} ", lc);
        mainWindow.clearAll();

        // Retrieve the GameClient
        GameClient gc = Main.clientRegistry.getGameClient(lc.clientName());

        // Check if the GameClient is null
        if (gc == null) {
            logger.error("GameClient {} not found in clientRegistry", lc.clientName());
            return; // Exit early if no client is found
        }

        // Proceed if GameClient is found
        gameClient = Optional.of(gc);
        gc.load(this, metadata, lc.player());
    }


    /** Executes a ShowMenuError command */
    private void execute(ShowMenuError sme) {
        // We can only show an error if there's no game client
        // (otherwise we might mess with its display)
        if (gameClient.isEmpty()) {
            mainWindow.clearSouth();
            JLabel l = new JLabel(sme.message());
            mainWindow.addSouth(l);
            mainWindow.pack();
        }
    }

    /**
     * Interprets and runs a rendering command.
     * If this is one of the (3) known native command, it runs it.
     * If not, it passes it directly on to the current GameClient to interpret.
     */
    private void interpretCommand(GameMetadata metadata, JsonObject json) {
        logger.info("Interpreting command {}", json);

        // Try the native commands first
        boolean handled = false;

        Optional<LoadClient> olc = LoadClient.tryParsing(json);
        if (olc.isPresent()) {
            handled = true;
            execute(metadata, olc.get());
        }

        Optional<ShowMenuError> osme = ShowMenuError.tryParsing(json);
        if (osme.isPresent()) {
            handled = true;
            execute(osme.get());
        }

        Optional<QuitToMenu> qtm = QuitToMenu.tryParsing(json);
        if (qtm.isPresent()) {
            handled = true;
            execute(qtm.get());
        }

        if (!handled) {
            gameClient.ifPresent((gc) -> gc.execute(metadata, json));
        }
    }

    /** Interprets and executes all the commands in a rendering package */
    private void runRenderingPackage(RenderingPackage rp) {
        logger.info("Running rendering package");
        GameMetadata gm = rp.metadata();

        for (JsonObject json : rp.renderingCommands()) {
            interpretCommand(gm, json);
        }
    }

    public Future<Object> awardAchievement(String username, String achievementName) {
        return webClient.post(port, host, "/api/users/" + username + "/achievements/name/" + achievementName)
                // .putHeader("Authorization", "Bearer " + getStoredToken())
                .send()
                .compose(response -> {
                    if (response.statusCode() == 200) {
                        logger.info("Achievement unlocked successfully: {}", achievementName);
                        return Future.succeededFuture();
                    } else {
                        return Future.failedFuture("Failed to unlock achievement: " + response.statusMessage());
                    }
                })
                .recover(err -> {
                    logger.error("Error unlocking achievement: {}", err.getMessage());
                    return Future.failedFuture(err);
                });
    }

    public Future<Void> addAchievement(JsonObject achievement) {
        if (achievement == null) {
            return Future.failedFuture("Achievement object cannot be null");
        }
        
        return webClient.post(port, host, "/api/achievements")
            .sendJsonObject(achievement)
            .map(response -> {
                if (response.statusCode() == 200) {
                    logger.info("Achievement added successfully: {}", achievement.getString("name", "Unknown"));
                    return null;
                } else {
                    String errorMsg = "Failed to add achievement: " + response.statusMessage() + " (Status code: " + response.statusCode() + ")";
                    logger.error(errorMsg);
                    throw new RuntimeException(errorMsg);
                }
            });
    }

    public Future<JsonArray> getUserAchievements(String username) {
        return webClient.get(port, host, "/api/users/" + username + "/achievements")
                .putHeader("Authorization", "Bearer " + getStoredToken())
                .send()
                .map(HttpResponse::bodyAsJsonArray)
                .onSuccess(achievements -> {
                    logger.info("User achievements retrieved successfully: {}", achievements);
                })
                .onFailure(err -> {
                    logger.error("Error retrieving user achievements: {}", err.getMessage());
                });
    }

    public void signUp() {
        logger.info("Clicked the sign up button");
    }

    // Set stored JWT session token
    public void setStoredToken(String token) {
        this.storedToken = token;
    }

    // Get stored JWT session token
    public String getStoredToken() {
        return storedToken;
    }

    // Returns the
    public Future<JsonObject> getUserPrincipal() {
        return webClient.get(port, host, "/api/user")
                .putHeader("Authorization", "Bearer " + getStoredToken())
                .send()
                .map(HttpResponse::bodyAsJsonObject)
                .onSuccess(userPrincipal -> {
                    logger.info("User details retrieved successfully: {}", userPrincipal);
                })
                .onFailure(err -> {
                    logger.error("Error retrieving user details: {}", err.getMessage());
                });
    }

    // Returns the full user details (excluding password) for a user
    public Future<JsonObject> getUserDetails(String username) {
        return webClient.get(port, host, "/api/user/" + username)
                .putHeader("Authorization", "Bearer " + getStoredToken()).send()
                .map(HttpResponse::bodyAsJsonObject)
                .map(userDetails -> {
                    userDetails.remove("password");
                    return userDetails;
                })
                .onSuccess(userDetails -> {
                    logger.info("User details retrieved successfully: {}", userDetails);
                })
                .onFailure(err -> {
                    logger.error("Error retrieving user details: {}", err.getMessage());
                });
    }

    public Future<String> getUsernameFromEmail(String email) {
        return webClient.get(port, host, "/api/get-username/" + email)
                .send()
                .map(HttpResponse::bodyAsString)
                .onSuccess(username -> {
                    logger.info("Username retrieved successfully: {}", username);
                })
                .onFailure(err -> {
                    logger.error("Error retrieving username: {}", err.getMessage());
                });
    }

    public Future<JsonObject> updateUserDetails(String username, JsonObject userDetails) {
        return webClient.post(port, host, "/api/user/update/" + username)
                .putHeader("Authorization", "Bearer " + getStoredToken())
                .sendJsonObject(userDetails)
                .map(HttpResponse::bodyAsJsonObject)
                .onSuccess(updatedUserDetails -> {
                    logger.info("User details updated successfully: {}", updatedUserDetails);
                })
                .onFailure(err -> {
                    logger.error("Error updating user details: {}", err.getMessage());
                });
    }

    public Future<JsonObject> getCurrentUserProfile() {
        return webClient.get(port, host, "/api/profile")
                .putHeader("Authorization", "Bearer " + getStoredToken())
                .send()
                .map(HttpResponse::bodyAsJsonObject)
                .map(userDetails -> {
                    return userDetails;
                })
                .onSuccess(userDetails -> {
                    logger.info("User details retrieved successfully: {}", userDetails);
                })
                .onFailure(err -> {
                    logger.error("Error retrieving user details: {}", err.getMessage());
                });
    }

    public Future<JsonObject> getUserProfile(String username) {
        return webClient.get(port, host, "/api/profile/" + username)
                .send()
                .map(HttpResponse::bodyAsJsonObject)
                .map(userDetails -> {
                    return userDetails;
                })
                .onSuccess(userDetails -> {
                    logger.info("User details retrieved successfully: {}", userDetails);
                })
                .onFailure(err -> {
                    logger.error("Error retrieving user details: {}", err.getMessage());
                });
    }


    public Future<JsonObject> sendEmail(JsonObject emailDetails) {
        return webClient.post(port, host, "/api/send-email")
                .sendJsonObject(emailDetails)
                .onSuccess(res -> {
                    logger.info("Email sent: {}", res);
                })
                .map(HttpResponse::bodyAsJsonObject)
                .onFailure(err -> {
                    logger.error("Error sending email: {}", err.getMessage());
                    throw new RuntimeException(err);
                });
    }

    /**
     * Establish the websocket connection between the client and the server
     * create the command "getState"
     * write the command to the server
     * add a binary message handler to listen to the server
     * 
     * @param rp
     * @param playerName
     */
    private void handleWebSocketRequest(RenderingPackage rp, String playerName) {
        client.webSocket(port, host, "/", (ctx) -> { // Establish the websocket connection between the client and the
                                                     // server

            JsonObject getState = new JsonObject().put("command", "getState");
            JsonObject getPlayerName = new JsonObject().put("command", "getPlayerName");
            JsonObject getTimeRemain = new JsonObject().put("command", "getTimeRemain");

            List<JsonObject> jsonList = new ArrayList<JsonObject>();
            jsonList.add(getState);
            jsonList.add(getPlayerName);
            jsonList.add(getTimeRemain);

            logger.info("Web Socket jsonList {}", jsonList);

            var comms = new CommandPackage(rp.metadata().gameServer(),
                    rp.metadata().name(),
                    playerName,
                    jsonList);

            ctx.result().write(JsonObject.mapFrom(comms).toBuffer()); // write the command to the server

            ctx.result().binaryMessageHandler(bin -> { // add a binary message handler to listen to the server
                var gameStateRP = RenderingPackage.fromJson(new JsonObject(bin));
                runRenderingPackage(gameStateRP);
            });

        });
    }

    // test methods for addAchievement
    public Future<Void> testAddAchievement() {
        JsonObject testAchievement = new JsonObject()
            .put("game", "TestGame")
            .put("name", "Test Achievement")
            .put("description", "This is a test achievement")
            .put("points", 100)
            .put("image", "test_image.png");

        return addAchievement(testAchievement);
    }

    public void runAchievementTest() {
        testAddAchievement()
            .onSuccess(v -> {
                logger.info("Achievement test passed successfully");
            });
    }
}
