package minigames.server.database;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import minigames.server.api.user.AppUser;
import minigames.server.api.user.UserProperties;
import minigames.server.api.achievement.Achievement;
import minigames.server.api.auth.HashingService;
import minigames.server.api.profile.Profile;
import minigames.server.database.repositories.LeaderboardRepository;
import minigames.server.database.repositories.UserRepository;
import minigames.server.database.repositories.LeaderboardRepository;
import minigames.server.database.repositories.ProfileRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class DatabaseSeeder {
    private static final Logger logger = LogManager.getLogger(DatabaseSeeder.class);
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final LeaderboardRepository leaderboardRepository;

    public DatabaseSeeder(UserRepository userRepository, ProfileRepository profileRepository, LeaderboardRepository leaderboardRepository) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.leaderboardRepository = leaderboardRepository;
    }

    public void seedDatabase() {
        try {
            emptyDatabase();

            String jsonContent = readJsonFile("/seed_data.json");
            JsonObject seedData = new JsonObject(jsonContent);

            seedUsersAndProfiles(seedData.getJsonArray("users"));
            seedAchievements(seedData.getJsonArray("achievements"));
            seedUserAchievements(seedData.getJsonArray("userAchievements"));
            seedLeaderboards(seedData.getJsonArray("leaderboards"));

            logger.info("Database seeded successfully");
        } catch (Exception e) {
            logger.error("Error seeding database", e);
        }
    }

    private void emptyDatabase() {
        DatabaseUtils.executeUpdate("DELETE FROM achievement_join");
        DatabaseUtils.executeUpdate("DELETE FROM achievements");
        DatabaseUtils.executeUpdate("DELETE FROM profiles");
        DatabaseUtils.executeUpdate("DELETE FROM users");
        DatabaseUtils.executeUpdate("DELETE FROM leaderboards");
        logger.info("Database emptied");
    }

    private void seedUsersAndProfiles(JsonArray users) {
        for (int i = 0; i < users.size(); i++) {
            JsonObject userJson = users.getJsonObject(i);
            String username = userJson.getString("username");
            String password = userJson.getString("password");
            String email = userJson.getString("email");

            String hashedPassword = HashingService.hashPassword(password);
            JsonObject propertiesJson = new JsonObject()
                    .put("hashed_password", hashedPassword)
                    .put("email", email);
            UserProperties properties = new UserProperties(propertiesJson);
            AppUser user = new AppUser(null, username, properties);

            userRepository.addUser(user).onComplete(ar -> {
                if (ar.succeeded()) {
                    logger.info("User added: " + username);

                    if (userJson.containsKey("profile")) {
                        JsonObject profileJson = userJson.getJsonObject("profile");
                        seedProfile(username, profileJson);
                    } else {
                        logger.info("No profile data for user: " + username);
                    }
                } else {
                    logger.error("Failed to add user: " + username, ar.cause());
                }
            });
        }
    }

    private void seedProfile(String username, JsonObject profileJson) {
        Profile profile = new Profile(profileJson);
        profileRepository.createProfile(username, profile).onComplete(ar -> {
            if (ar.succeeded()) {
                logger.info("Profile added for user: " + username);
            } else {
                logger.error("Failed to add profile for user: " + username, ar.cause());
            }
        });
    }

    private void seedAchievements(JsonArray achievements) {
        for (int i = 0; i < achievements.size(); i++) {
            JsonObject achievementJson = achievements.getJsonObject(i);
            Achievement achievement = new Achievement(
                    null,
                    achievementJson.getString("game"),
                    achievementJson.getString("name"),
                    achievementJson.getString("description"),
                    achievementJson.getInteger("points"),
                    null,
                    achievementJson.getString("image"));

            userRepository.addAchievement(achievement).onComplete(ar -> {
                if (ar.succeeded()) {
                    logger.info("Achievement added: " + achievement.getName());
                } else {
                    logger.error("Failed to add achievement: " + achievement.getName(), ar.cause());
                }
            });
        }
    }

    private void seedUserAchievements(JsonArray userAchievements) {
        Map<String, Long> achievementIds = new HashMap<>();
        userRepository.getAllAchievements().onComplete(ar -> {
            if (ar.succeeded()) {
                for (Achievement achievement : ar.result()) {
                    achievementIds.put(achievement.getName(), achievement.getId());
                }
                for (int i = 0; i < userAchievements.size(); i++) {
                    JsonObject userAchievement = userAchievements.getJsonObject(i);
                    String username = userAchievement.getString("username");
                    String achievementName = userAchievement.getString("achievementName");
                    Long achievementId = achievementIds.get(achievementName);

                    if (achievementId != null) {
                        userRepository.unlockAchievement(username, achievementId.toString()).onComplete(unlockAr -> {
                            if (unlockAr.succeeded()) {
                                logger.info("Achievement unlocked for user: " + username + " - " + achievementName);
                            } else {
                                logger.error(
                                        "Failed to unlock achievement for user: " + username + " - " + achievementName,
                                        unlockAr.cause());
                            }
                        });
                    } else {
                        logger.error("Achievement not found: " + achievementName);
                    }
                }
            } else {
                logger.error("Failed to get achievements", ar.cause());
            }
        });
    }

    private void seedLeaderboards(JsonArray leaderboards) {
        if (leaderboards == null || leaderboards.isEmpty()) {
            logger.error("Leaderboards data is null or empty.");
            return;
        }

        for (int i = 0; i < leaderboards.size(); i++) {
            JsonObject leaderboardJson = leaderboards.getJsonObject(i);

            String playerName = leaderboardJson.getString("name");
            int score = leaderboardJson.getInteger("score");
            String gameType = leaderboardJson.getString("gametype");

            leaderboardRepository.addScore(playerName, score, gameType).onSuccess(v -> {
                logger.info("Successfully added score for player: {}, score: {}, gameType: {}", playerName, score, gameType);
            }).onFailure(err -> {
                logger.error("Failed to add score for player: {}, gameType: {}, error: {}", playerName, gameType, err.getMessage());
            });
        }
    }

    private String readJsonFile(String fileName) throws Exception {
        try (InputStream inputStream = getClass().getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new Exception("File not found: " + fileName);
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}