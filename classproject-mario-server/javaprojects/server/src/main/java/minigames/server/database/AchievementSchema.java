package minigames.server.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;

/* Example usage:
    AchievementSchema.createAchievement("test1", 10);
    AchievementSchema.createAchievement("test2", 20);
    AchievementSchema.giveUserAchievement(1,1); //userId: 1 and achievementId: 1
    AchievementSchema.getAllAchievements();
    AchievementSchema.getUserAchievements(1);
 */
public class AchievementSchema {
    private static JsonObject getAchievementJson(ResultSet rs) {
        JsonObject achievementJson = new JsonObject();
        try {
            achievementJson.put("id", rs.getInt("id"));
            achievementJson.put("name", rs.getString("name"));
            achievementJson.put("points", rs.getInt("points"));
            System.out.println(achievementJson);
        } catch (SQLException e) {
            System.err.println("Message: " + e);
        }
        return achievementJson;
    }

    public static void createAchievement(String name, int points) {
        String query = "INSERT INTO achievements (name, points) VALUES ('" + name + "', " + points + ")";
        System.out.println(DatabaseUtils.executeUpdate(query));
    }

    public static JsonArray getAllAchievements() {
        String query = "SELECT * FROM achievements";
        ResultSet rs = DatabaseUtils.executeQuery(query);
        JsonArray achievementArray = new JsonArray();
        try {
            while (rs.next()) {
                achievementArray.add(getAchievementJson(rs));
            }
        } catch (SQLException e) {
            System.err.println("Message: " + e);
        }
        return achievementArray;
    }

    public static void giveUserAchievement(int userId, int achievementId) {
        String query = "SELECT * FROM achievement_join WHERE user_id = " + userId + " AND achievement_id = " + achievementId;
        ResultSet rs = DatabaseUtils.executeQuery(query);
        try {
            if (!rs.next()) {
                String update = "INSERT INTO achievement_join (user_id, achievement_id) VALUES (" + userId + ", " + achievementId + ")";
                DatabaseUtils.executeUpdate(update);
            }
        } catch (SQLException e) {
            System.err.println("Message: " + e);
        }
    }

    public static JsonArray getUserAchievements(int userId) {
        String query = "SELECT a.* FROM achievements a INNER JOIN achievement_join aj ON a.id = aj.achievement_id WHERE aj.user_id = " + userId;
        ResultSet rs = DatabaseUtils.executeQuery(query);
        JsonArray achievementArray = new JsonArray();
        try {
            while (rs.next()) {
                achievementArray.add(getAchievementJson(rs));
            }
        } catch (SQLException e) {
            System.err.println("Message: " + e);
        }
        return achievementArray;
    }
}