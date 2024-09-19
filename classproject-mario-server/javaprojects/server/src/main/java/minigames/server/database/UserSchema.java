package minigames.server.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import io.vertx.core.json.JsonObject;

/* Example Usage
    UserSchema.createUser("testuser", "testpassword");
    JsonObject json = UserSchema.queryUserByUsername("testuser");
    System.out.println(json.encodePrettily());
    UserSchema.checkIfUserExists("testuser");
 */

public class UserSchema {
    private static JsonObject getUserJson(ResultSet rs) {
        JsonObject userJson = new JsonObject();
        try {
            rs.next();
            userJson.put("id", rs.getInt("id"));
            userJson.put("username", rs.getString("username"));
            userJson.put("password", rs.getString("password"));
            userJson.put("email", rs.getString("email"));
            userJson.put("created_at", rs.getTimestamp("created_at"));
            userJson.put("last_login", rs.getTimestamp("last_login"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userJson;
    }

    public static void createUser(String username, String password, String email) {
        String query = "INSERT INTO users (username, password, email) VALUES ('" + username + "', '" + password + "', '" + email + "')";
        DatabaseUtils.executeUpdate(query);
    }

    public static JsonObject queryUserByUsername(String username) {
        String query = "SELECT * FROM users WHERE username = '" + username + "'";
        ResultSet rs = DatabaseUtils.executeQuery(query);
        if (rs != null) {
            JsonObject json = getUserJson(rs);
            return json;
        }
        return new JsonObject();
    }

    public static boolean checkIfUserExists(String username) {
        String query = "SELECT * FROM users WHERE username = '" + username + "'";
        ResultSet rs = DatabaseUtils.executeQuery(query);
        if (rs == null) return false;
        return true;
    }

    public static void updatePassword(String username, String newPassword) {
        String query = "UPDATE users SET password = '" + newPassword + "' WHERE username ='" + username + "'";
        DatabaseUtils.executeUpdate(query);
    }
}