package minigames.server.database;

import java.io.File;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseUtils {

    private static final String DB_URL = "jdbc:derby:DB;create=true";
    private static Connection conn = null;
    private static Statement statement = null;

    public static boolean connectToDatabase() {
        try {
            conn = establishConnection(DB_URL);
            statement = createStatement(conn);
            return true;
        } catch (Exception e) {
            System.err.println("Message: " + e);
            return false;
        }
    }

    public static Connection establishConnection(String requestedURL) {
        try {
            return DriverManager.getConnection(requestedURL);
        } catch (SQLException e) {
            System.err.println("Message: " + e);
            return null;
        }

    }

    public static Statement createStatement(Connection connection) {
        try {
            return connection.createStatement();
        } catch (SQLException e) {
            System.err.println("Message: " + e);
            return null;
        }
    }

    public static boolean executeUpdate(Statement statement, String query) {
        try {
            statement.executeUpdate(query);
            return true;
        } catch (SQLException e) {
            System.err.println("Message: " + e);
            return false;
        }
    }

    public static int executeUpdate(String query) {
        try {
            int rows = statement.executeUpdate(query);
            return rows;
        } catch (SQLException e) {
            System.err.println("Message: " + e);
            return 0;
        }
    }

    public static boolean executeQuery(Statement statement, String query) {
        try {
            statement.executeQuery(query);
            return true;
        } catch (SQLException e) {
            System.err.println("Message: " + e);
            return false;
        }
    }

    public static ResultSet executeQuery(String query) {
        try {
            return statement.executeQuery(query);
        } catch (SQLException e) {
            System.err.println("SQL Error executing query: " + query);
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static void disconnectFromDatabase(Statement statement, Connection conn) {
        try {
            statement.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println("Message: " + e);
        }
    }

    public static void shutdownDatabase(String dbName) {
        String URL = "jdbc:derby:" + dbName + ";shutdown=true";
        try (Connection conn = DriverManager.getConnection(URL)) {
        } catch (SQLException e) {
            System.out.println("Database shutdown successfully.");
        }
    }

    public static boolean dropDatabase(String dbName) {
        File dbDirectory = new File(dbName);
        return deleteDirectory(dbDirectory);
    }

    private static boolean deleteDirectory(File directory) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (!deleteDirectory(file)) {
                        return false;
                    }
                }
            }
        }
        return directory.delete();
    }
}