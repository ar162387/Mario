package minigames.server.api.user;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.JWTOptions;
import minigames.server.api.achievement.Achievement;
import minigames.server.api.auth.HashingService;
import minigames.server.database.repositories.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.inject.Inject;

public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final JWTAuth jwtAuth;

    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);

    @Inject
    public UserServiceImpl(UserRepository userRepository, JWTAuth jwtAuth) {
        this.userRepository = userRepository;
        this.jwtAuth = jwtAuth;
    }

    @Override
    public Future<String> registerUser(String username, String password, String email) {
        return userRepository.userExists(username).compose(exists -> {
            if (exists) {
                return Future.failedFuture("User already exists");
            }
            String hashedPassword = HashingService.hashPassword(password);
            JsonObject propertiesJson = new JsonObject()
                    .put("hashed_password", hashedPassword)
                    .put("email", email);
            UserProperties properties = new UserProperties(propertiesJson);
            AppUser newUser = new AppUser(null, username, properties);

            return userRepository.addUser(newUser)
                    .compose(v -> {
                        String token = jwtAuth.generateToken(newUser.toJson(),
                                new JWTOptions().setExpiresInMinutes(60));
                        return Future.succeededFuture(token);
                    });
        });
    }

    @Override
    public Future<String> loginUser(String username, String password) {
        return userRepository.getHashedPassword(username).compose(hashedPassword -> {
            if (hashedPassword == null) {
                return Future.failedFuture("User not found");
            }
            if (HashingService.comparePassword(password, hashedPassword)) {
                return userRepository.getUserData(username).compose(user -> {
                    JsonObject claims = user.toJson();
                    claims.remove("properties");
                    String token = jwtAuth.generateToken(claims, new JWTOptions().setExpiresInMinutes(60));
                    return Future.succeededFuture(token);
                });
            } else {
                return Future.failedFuture("Invalid password");
            }
        });
    }

    @Override
    public Future<JsonObject> getUserData(String username) {
        return userRepository.getUserData(username).map(AppUser::toJson);
    }

    public Future<String> getUsernameFromEmail(String email) {
        logger.info("Getting username from email: " + email);
        return userRepository.getUsernameFromEmail(email);
    }

    @Override
    public Future<List<JsonObject>> getAllUsers() {
        return userRepository.getAllUsers()
                .map(users -> users.stream()
                        .map(user -> {
                            JsonObject userJson = new JsonObject()
                                    .put("id", user.getId())
                                    .put("username", user.getUsername())
                                    .put("email", user.getProperties().getProperty(UserProperty.EMAIL))
                                    .put("created_at", user.getProperties().getProperty(UserProperty.CREATED_AT))
                                    .put("last_login", user.getProperties().getProperty(UserProperty.LAST_LOGIN));
                            return userJson;
                        })
                        .collect(Collectors.toList()));
    }

    @Override
    public Future<Void> updateUser(String username, JsonObject updateDetails) {
        return userRepository.getUserData(username)
                .compose(appUser -> {
                    if (appUser == null) {
                        return Future.failedFuture("User not found: " + username);
                    }

                    UserProperties properties = appUser.getProperties();
                    JsonObject propertiesJson = properties.toJson();

                    // Update only the provided fields
                    for (String field : updateDetails.fieldNames()) {
                        propertiesJson.put(field, updateDetails.getValue(field));
                    }

                    UserProperties updatedProperties = new UserProperties(propertiesJson);
                    AppUser updatedUser = new AppUser(appUser.getId(), appUser.getUsername(), updatedProperties);
                    return userRepository.updateUser(updatedUser);
                });
    }

    @Override
    public Future<Void> incrementPlayTime(String username, long minutes) {
        return userRepository.incrementPlayTime(username, minutes);
    }

    @Override
    public Future<JsonArray> getAllAchievements() {
        return userRepository.getAllAchievements()
                .map(achievements -> new JsonArray(achievements.stream()
                        .map(Achievement::toJsonNoDate)
                        .collect(Collectors.toList())));
    }

    @Override
    public Future<JsonObject> getAchievement(String achievementId) {
        return userRepository.getAchievement(achievementId)
                .map(achievement -> achievement != null ? achievement.toJson() : null);
    }

    @Override
    public Future<JsonArray> getUserAchievements(String username) {
        return userRepository.getUserAchievements(username)
                .map(achievements -> new JsonArray(achievements.stream()
                        .map(Achievement::toJson)
                        .collect(Collectors.toList())));
    }

    @Override
    public Future<Void> unlockAchievement(String username, String achievementId) {
        return userRepository.unlockAchievement(username, achievementId);
    }

    @Override
    public Future<Void> unlockAchievementByName(String username, String achievementName) {
        return userRepository.unlockAchievementByName(username, achievementName);
    }

    @Override
    public Future<Void> addAchievement(Achievement achievement) {
        return userRepository.addAchievement(achievement);
    }

    @Override
    public Future<JsonArray> getUserScores(String username) {
        return userRepository.getUserScores(username);
    }

    @Override
    public Future<Void> addScore(String username, String gameName, int score) {
        return userRepository.addScore(username, gameName, score);
    }

    @Override
    public Future<JsonArray> getGlobalScoreboard() {
        return userRepository.getGlobalScoreboard();
    }

    @Override
    public Future<JsonArray> getGameScoreboard(String gameName) {
        return userRepository.getGameScoreboard(gameName);
    }

}