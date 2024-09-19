package minigames.server.database.repositories;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import minigames.server.api.achievement.Achievement;
import minigames.server.api.user.AppUser;
import minigames.server.api.user.UserProperties;
import minigames.server.api.user.UserProperty;
import minigames.server.database.DatabaseUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserRepositoryImpl implements UserRepository {

    private static final Logger logger = LogManager.getLogger(UserRepositoryImpl.class);

    @Override
    public Future<String> getHashedPassword(String username) {
        Promise<String> promise = Promise.promise();
        String query = "SELECT password FROM users WHERE username = '" + username + "'";
        ResultSet rs = DatabaseUtils.executeQuery(query);
        try {
            if (rs != null && rs.next()) {
                String hashedPassword = rs.getString("password");
                promise.complete(hashedPassword);
            } else {
                promise.complete(null);
            }
        } catch (SQLException e) {
            promise.fail(e);
        }
        return promise.future();
    }

    @Override
    public Future<Boolean> userExists(String username) {
        Promise<Boolean> promise = Promise.promise();
        String query = "SELECT * FROM users WHERE username = '" + username + "'";
        ResultSet rs = DatabaseUtils.executeQuery(query);
        try {
            promise.complete(rs != null && rs.next());
        } catch (SQLException e) {
            promise.fail(e);
        }
        return promise.future();
    }

    @Override
    public Future<Boolean> userExists(Long id) {
        Promise<Boolean> promise = Promise.promise();
        String query = "SELECT * FROM users WHERE id = '" + id + "'";
        ResultSet rs = DatabaseUtils.executeQuery(query);
        try {
            promise.complete(rs != null && rs.next());
        } catch (SQLException e) {
            promise.fail(e);
        }
        return promise.future();
    }

    @Override
    public Future<Void> addUser(AppUser user) {
        Promise<Void> promise = Promise.promise();
        UserProperties properties = user.getProperties();
        String query = "INSERT INTO users (username, password, email) VALUES ('" +
                user.getUsername() + "', '" +
                properties.getProperty(UserProperty.HASHED_PASSWORD) + "', '" +
                properties.getProperty(UserProperty.EMAIL) + "')";
        int rowsAffected = DatabaseUtils.executeUpdate(query);
        if (rowsAffected > 0) {
            promise.complete();
        } else {
            promise.fail("Failed to add user");
        }
        return promise.future();
    }

    @Override
    public Future<AppUser> getUserData(String username) {
        Promise<AppUser> promise = Promise.promise();
        String query = "SELECT * FROM users WHERE username = '" + username + "'";
        ResultSet rs = DatabaseUtils.executeQuery(query);
        try {
            if (rs != null && rs.next()) {
                JsonObject propertiesJson = new JsonObject()
                        .put("hashed_password", rs.getString("password"))
                        .put("email", rs.getString("email"))
                        .put("created_at", rs.getTimestamp("created_at").toString())
                        .put("last_login", rs.getTimestamp("last_login").toString());

                UserProperties properties = new UserProperties(propertiesJson);
                AppUser user = new AppUser(rs.getLong("id"), username, properties);
                promise.complete(user);
            } else {
                promise.complete(null);
            }
        } catch (SQLException e) {
            promise.fail(e);
        }
        return promise.future();
    }

    @Override
    public Future<AppUser> getUserData(Long id) {
        Promise<AppUser> promise = Promise.promise();
        String query = "SELECT * FROM users WHERE id = '" + id + "'";
        ResultSet rs = DatabaseUtils.executeQuery(query);
        try {
            if (rs != null && rs.next()) {
                JsonObject propertiesJson = new JsonObject()
                        .put("hashed_password", rs.getString("password"))
                        .put("email", rs.getString("email"))
                        .put("created_at", rs.getTimestamp("created_at").toString())
                        .put("last_login", rs.getTimestamp("last_login").toString());

                UserProperties properties = new UserProperties(propertiesJson);
                AppUser user = new AppUser(rs.getLong("id"), rs.getString("username"), properties);
                promise.complete(user);
            } else {
                promise.complete(null);
            }
        } catch (SQLException e) {
            promise.fail(e);
        }
        return promise.future();
    }

    public Future<String> getUsernameFromEmail(String email) {
        logger.info("Looking up username for email '{}'", email);
        Promise<String> promise = Promise.promise();
        String query = "SELECT username FROM users WHERE email = '" + email + "'";
        ResultSet rs = DatabaseUtils.executeQuery(query);
        try {
            if (rs != null && rs.next()) {
                String username = rs.getString("username");
                promise.complete(username);
            } else {
                promise.complete(null);
            }
        } catch (SQLException e) {
            promise.fail(e);
        }
        return promise.future();
    }

    @Override
    public Future<List<AppUser>> getAllUsers() {
        Promise<List<AppUser>> promise = Promise.promise();
        String query = "SELECT * FROM users";
        ResultSet rs = DatabaseUtils.executeQuery(query);
        try {
            List<AppUser> users = new ArrayList<>();
            while (rs != null && rs.next()) {
                JsonObject propertiesJson = new JsonObject()
                        .put("hashed_password", rs.getString("password"))
                        .put("email", rs.getString("email"))
                        .put("created_at", rs.getTimestamp("created_at").toString())
                        .put("last_login", rs.getTimestamp("last_login").toString());

                UserProperties properties = new UserProperties(propertiesJson);
                AppUser user = new AppUser(rs.getLong("id"), rs.getString("username"), properties);
                users.add(user);
            }
            promise.complete(users);
        } catch (SQLException e) {
            promise.fail(e);
        }
        return promise.future();
    }

    @Override
    public Future<Void> updateUser(AppUser user) {
        Promise<Void> promise = Promise.promise();
        UserProperties properties = user.getProperties();
        String query = "UPDATE users SET " +
                "password = '" + properties.getProperty(UserProperty.HASHED_PASSWORD) + "', " +
                "email = '" + properties.getProperty(UserProperty.EMAIL) + "' " +
                "WHERE username = '" + user.getUsername() + "'";
        int rowsAffected = DatabaseUtils.executeUpdate(query);
        if (rowsAffected > 0) {
            promise.complete();
        } else {
            promise.fail("Failed to update user");
        }
        return promise.future();
    }

    @Override
    public Future<Void> incrementPlayTime(String username, long minutes) {
        Promise<Void> promise = Promise.promise();
        String query = "UPDATE users SET play_time = play_time + " + minutes +
                " WHERE username = '" + username + "'";
        int rowsAffected = DatabaseUtils.executeUpdate(query);
        if (rowsAffected > 0) {
            promise.complete();
        } else {
            promise.fail("Failed to increment play time");
        }
        return promise.future();
    }

    @Override
    public Future<Achievement> getAchievement(String achievementId) {
        Promise<Achievement> promise = Promise.promise();
        String query = "SELECT * FROM achievements WHERE id = " + achievementId;
        ResultSet rs = DatabaseUtils.executeQuery(query);
        try {
            if (rs != null && rs.next()) {
                Achievement achievement = createAchievementFromResultSet(rs);
                promise.complete(achievement);
            } else {
                promise.complete(null);
            }
        } catch (SQLException e) {
            promise.fail(e);
        }
        return promise.future();
    }

    @Override
    public Future<List<Achievement>> getUserAchievements(String username) {
        Promise<List<Achievement>> promise = Promise.promise();
        String query = "SELECT a.*, aj.achieved_at FROM achievements a " +
                       "JOIN achievement_join aj ON a.id = aj.achievement_id " +
                       "JOIN users u ON u.id = aj.user_id " +
                       "WHERE u.username = '" + username + "'";
        ResultSet rs = DatabaseUtils.executeQuery(query);
        try {
            List<Achievement> achievements = new ArrayList<>();
            while (rs != null && rs.next()) {
                achievements.add(createAchievementFromResultSet(rs));
            }
            promise.complete(achievements);
        } catch (SQLException e) {
            promise.fail(e);
        }
        return promise.future();
    }

    @Override
    public Future<Void> addAchievement(Achievement achievement) {
        Promise<Void> promise = Promise.promise();
        
        String escapedGame = achievement.getGame().replace("'", "''");
        String escapedName = achievement.getName().replace("'", "''");
        String escapedDescription = achievement.getDescription().replace("'", "''");
        String escapedImage = achievement.getImage().replace("'", "''");
        
        String query = "INSERT INTO achievements (game, name, description, points, image) VALUES ('" +
                escapedGame + "', '" +
                escapedName + "', '" +
                escapedDescription + "', " +
                achievement.getPoints() + ", '" +
                escapedImage + "')";
        
        int rowsAffected = DatabaseUtils.executeUpdate(query);
        if (rowsAffected > 0) {
            promise.complete();
        } else {
            promise.fail("Failed to add achievement");
        }
        return promise.future();
    }

    @Override
    public Future<Void> unlockAchievement(String username, String achievementId) {
        Promise<Void> promise = Promise.promise();
        String query = "INSERT INTO achievement_join (user_id, achievement_id) " +
                       "SELECT u.id, " + achievementId + " FROM users u " +
                       "WHERE u.username = '" + username + "'";
        int rowsAffected = DatabaseUtils.executeUpdate(query);
        if (rowsAffected > 0) {
            promise.complete();
        } else {
            promise.fail("Failed to unlock achievement");
        }
        return promise.future();
    }

    @Override
    public Future<Void> unlockAchievementByName(String username, String achievementName) {
        Promise<Void> promise = Promise.promise();
        logger.info("Looking up achievement ID for '{}' to unlock for user '{}'", achievementName, username);
    
        String escapedAchievementName = achievementName.replace("'", "''");
        
        String lookupQuery = "SELECT id FROM achievements WHERE name = '" + escapedAchievementName + "'";
        
        try {
            ResultSet rs = DatabaseUtils.executeQuery(lookupQuery);
            if (rs != null && rs.next()) {
                String achievementId = rs.getString("id");
                logger.info("Found achievement ID {} for name '{}'", achievementId, achievementName);
    
                String unlockQuery = "INSERT INTO achievement_join (user_id, achievement_id) " +
                                     "SELECT u.id, " + achievementId + " FROM users u " +
                                     "WHERE u.username = '" + username + "'";
                
                int rowsAffected = DatabaseUtils.executeUpdate(unlockQuery);
                if (rowsAffected > 0) {
                    logger.info("Successfully unlocked achievement '{}' for user '{}'", achievementName, username);
                    promise.complete();
                } else {
                    logger.warn("No rows affected when unlocking achievement '{}' for user '{}'", achievementName, username);
                    promise.fail("Failed to unlock achievement");
                }
            } else {
                logger.warn("Achievement '{}' not found", achievementName);
                promise.fail("Achievement not found");
            }
        } catch (SQLException e) {
            logger.error("Error unlocking achievement '{}' for user '{}': {}", achievementName, username, e.getMessage());
            promise.fail(e);
        }
    
        return promise.future();
    }


    private Achievement createAchievementFromResultSet(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String game = rs.getString("game");
        String name = rs.getString("name");
        String description = rs.getString("description");
        Integer points = rs.getInt("points");
        String dateAchieved = null;
        Timestamp timestamp = rs.getTimestamp("achieved_at");
        if (timestamp != null) {
            dateAchieved = timestamp.toString();
        }
        String image = rs.getString("image");
    
        return new Achievement(id, game, name, description, points, dateAchieved, image);
    }

    @Override
    public Future<List<Achievement>> getAllAchievements() {
        Promise<List<Achievement>> promise = Promise.promise();
        String query = "SELECT * FROM achievements";
        ResultSet rs = DatabaseUtils.executeQuery(query);
        try {
            List<Achievement> achievements = new ArrayList<>();
            while (rs != null && rs.next()) {
                achievements.add(createAchievementFromResultSetWithoutDate(rs));
            }
            promise.complete(achievements);
        } catch (SQLException e) {
            promise.fail(e);
        }
        return promise.future();
    }

    private Achievement createAchievementFromResultSetWithoutDate(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String game = rs.getString("game");
        String name = rs.getString("name");
        String description = rs.getString("description");
        Integer points = rs.getInt("points");
        String image = rs.getString("image");
    
        return new Achievement(id, game, name, description, points, null, image);
    }

    @Override
    public Future<JsonArray> getGameScoreboard(String gameName) {
        return Future.failedFuture("Not implemented");
    }

    @Override
    public Future<JsonArray> getUserScores(String username) {
        return Future.failedFuture("Not implemented");
    }

    @Override
    public Future<Void> addScore(String username, String gameName, int score) {
        return Future.failedFuture("Not implemented");
    }

    @Override
    public Future<JsonArray> getGlobalScoreboard() {
        return Future.failedFuture("Not implemented");
    }
}