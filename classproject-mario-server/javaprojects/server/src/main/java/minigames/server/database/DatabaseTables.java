package minigames.server.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import minigames.server.MinigameNetworkServer;

public class DatabaseTables {
    private static final Logger logger = LogManager.getLogger(MinigameNetworkServer.class);

    public static void createTables() {
        createTableIfNotExists("users", createUserTableQuery());
        createTableIfNotExists("profiles", createProfileTableQuery());
        createTableIfNotExists("achievements", createAchievementTableQuery());
        createTableIfNotExists("achievement_join", createAchievementJoinTableQuery());
        createTableIfNotExists("leaderboards", createLeaderboardsTableQuery());
    }

    private static void createTableIfNotExists(String tableName, String createTableQuery) {
        String checkTableQuery = "SELECT 1 FROM SYS.SYSTABLES WHERE TABLENAME = '" + tableName.toUpperCase() + "'";
        boolean tableExists = false;

        try {
            ResultSet rs = DatabaseUtils.executeQuery(checkTableQuery);
            if (rs != null && rs.next()) {
                tableExists = true;
            }
        } catch (SQLException e) {
            logger.error("Error checking if " + tableName + " table exists", e);
            return;
        }

        if (!tableExists) {
            try {
                DatabaseUtils.executeUpdate(createTableQuery);
                logger.info(tableName + " table created successfully.");
            } catch (Exception e) {
                logger.error("Error creating " + tableName + " table.", e);
            }
        } else {
            logger.info(tableName + " table already exists.");
        }
    }

    private static String createUserTableQuery() {
        return "CREATE TABLE users ( " +
                "id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY," +
                "username VARCHAR(255) UNIQUE," +
                "password VARCHAR(255), " +
                "email VARCHAR(255)," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "last_login TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
    }

    private static String createProfileTableQuery() {
        return "CREATE TABLE profiles (" +
                "username VARCHAR(255) PRIMARY KEY," +
                "first_name VARCHAR(255)," +
                "last_name VARCHAR(255)," +
                "date_of_birth DATE," +
                "bio VARCHAR(5000)," +
                "favorite_game VARCHAR(255)," +
                "total_play_time_minutes INT," +
                "FOREIGN KEY (username) REFERENCES users(username)" +
                ")";
    }

    private static String createAchievementTableQuery() {
        return "CREATE TABLE achievements (" +
                "id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY," +
                "game VARCHAR(255)," +
                "name VARCHAR(255)," +
                "description VARCHAR(5000)," +
                "points INT," +
                "date_achieved TIMESTAMP," +
                "image VARCHAR(2048)" +
                ")";
    }

    private static String createAchievementJoinTableQuery() {
        return "CREATE TABLE achievement_join (" +
                "user_id INT NOT NULL, " +
                "achievement_id INT NOT NULL, " +
                "achieved_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "PRIMARY KEY (user_id, achievement_id), " +
                "FOREIGN KEY (user_id) REFERENCES users(id), " +
                "FOREIGN KEY (achievement_id) REFERENCES achievements(id)" +
                ")";
    }

    private static String createLeaderboardsTableQuery() {
        return "CREATE TABLE leaderboards (" +
                "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " +
                "name VARCHAR(255) NOT NULL, " +
                "score INT NOT NULL, " +
                "gametype VARCHAR(50) NOT NULL" +
                ")";
    }
}