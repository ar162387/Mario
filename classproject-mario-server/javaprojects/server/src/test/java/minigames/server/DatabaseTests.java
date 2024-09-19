// package minigames.server;

// import org.junit.jupiter.api.AfterEach;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import static org.junit.jupiter.api.Assertions.*;

// import java.sql.Connection;
// import java.sql.Statement;
// import java.sql.ResultSet;
// import java.sql.SQLException;
// import java.io.File;

// import minigames.server.database.DatabaseTables;
// import minigames.server.database.DatabaseUtils;

// public class DatabaseTests {

//     private static final String TEST_DB_NAME = "testDB";
//     private static final String TEST_DB_URL = "jdbc:derby:" + TEST_DB_NAME + ";create=true";

//     @BeforeEach
//     void setUp() {
//         cleanupDatabase();
//         DatabaseUtils.connectToDatabase();
//         DatabaseTables.createTables();
//         String insertUserSQL = "INSERT INTO users (username, password, email) VALUES ('testuser', 'password123', 'test@example.com')";
//         DatabaseUtils.executeUpdate(insertUserSQL);
//     }

//     private void cleanupDatabase() {
//         try {
//             DatabaseUtils.shutdownDatabase(TEST_DB_NAME);
//         } catch (Exception e) {
//             // Do nothing because it might not exists
//         }
        
//         File dbDirectory = new File(TEST_DB_NAME);
//         if (dbDirectory.exists()) {
//             deleteRecursive(dbDirectory);
//         }
//     }

//     private void deleteRecursive(File file) {
//         if (file.isDirectory()) {
//             for (File child : file.listFiles()) {
//                 deleteRecursive(child);
//             }
//         }
//         file.delete();
//     }

//     @AfterEach
//     void tearDown() {
//         try {
//             DatabaseUtils.shutdownDatabase("testDB");
//             DatabaseUtils.dropDatabase("testDB");
//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//     }

//     @Test
//     @DisplayName("Connect to Database")
//     void connectToDatabase() {
//         Connection conn = DatabaseUtils.establishConnection(TEST_DB_URL);
//         assertNotNull(conn, "Connection should not be null");

//         Statement statement = DatabaseUtils.createStatement(conn);
//         assertNotNull(statement, "Statement should not be null");

//         String testName = "COSC220";

//         try {
//             String createTableSQL = "CREATE TABLE units (id INT PRIMARY KEY, name VARCHAR(100))";
//             DatabaseUtils.executeUpdate(createTableSQL);

//             String insertDataSQL = "INSERT INTO units (id, name) VALUES (1, '" + testName + "')";
//             DatabaseUtils.executeUpdate(insertDataSQL);

//             String querySQL = "SELECT * FROM units WHERE id = 1";
//             ResultSet rs = DatabaseUtils.executeQuery(querySQL);
//             assertTrue(rs.next(), "ResultSet should have at least one row");
//             assertEquals(testName, rs.getString("name"), "Retrieved name should match inserted name");
//         } catch (SQLException e) {
//             fail("SQL Exception: " + e.getMessage());
//         } finally {
//             try {
//                 DatabaseUtils.disconnectFromDatabase(statement, conn);
//             } catch (Exception e) {
//                 e.printStackTrace();
//             }
//         }
//     }

//     @Test
//     @DisplayName("Create Database Tables")
//     void createDatabaseTables() {
//         assertDoesNotThrow(() -> {
            
//             DatabaseTables.createTables();
//         }, "Table creation should not throw an exception");

//             ResultSet userResultSet = DatabaseUtils.executeQuery("SELECT * FROM users");
//             assertNotNull(userResultSet, "Users table should exist");

//             ResultSet profileResultSet = DatabaseUtils.executeQuery("SELECT * FROM profiles");
//             assertNotNull(profileResultSet, "Profiles table should exist");

//             ResultSet achievementsResultSet = DatabaseUtils.executeQuery("SELECT * FROM achievements");
//             assertNotNull(achievementsResultSet, "Achievements table should exist");

//             ResultSet userAchievementsResultSet = DatabaseUtils.executeQuery("SELECT * FROM achievement_join");
//             assertNotNull(userAchievementsResultSet, "Profiles table should exist");
//     }

//     @Test
//     @DisplayName("Add User and Profile with Foreign Key")
//     void addUserAndProfileWithForeignKey() {
//         try {
//             DatabaseTables.createTables();

//             String getUserIdSQL = "SELECT id FROM users WHERE username = 'testuser'";
//             ResultSet userRs = DatabaseUtils.executeQuery(getUserIdSQL);
//             assertTrue(userRs.next(), "User should exist");
//             int userId = userRs.getInt("id");

//             String insertProfileSQL = "INSERT INTO profiles (user_id, first_name, last_name, date_of_birth, bio, favorite_game, total_play_time_minutes) " +
//                     "VALUES (" + userId + ", 'John', 'Doe', '1990-01-01', 'Test bio', 'TestGame', 120.5)";
//             DatabaseUtils.executeUpdate(insertProfileSQL);

//             String querySQL = "SELECT p.* FROM profiles p JOIN users u ON p.user_id = u.id WHERE u.username = 'testuser'";
//             ResultSet rs = DatabaseUtils.executeQuery(querySQL);

//             assertTrue(rs.next(), "Profile should exist");
//             assertEquals("John", rs.getString("first_name"));
//             assertEquals("Doe", rs.getString("last_name"));
//             assertEquals("1990-01-01", rs.getString("date_of_birth"));
//             assertEquals("Test bio", rs.getString("bio"));
//             assertEquals("TestGame", rs.getString("favorite_game"));
//             assertEquals(120.5f, rs.getFloat("total_play_time_minutes"), 0.01f);

//         } catch (SQLException e) {
//             fail("SQL Exception: " + e.getMessage());
//         }
//     }

//     @Test
//     @DisplayName("Update User Profile")
//     void updateUserProfile() {
//         try {

//             DatabaseUtils.executeUpdate("INSERT INTO users (username, password, email) VALUES ('updateuser', 'password123', 'update@example.com')");
//             ResultSet userRs = DatabaseUtils.executeQuery("SELECT id FROM users WHERE username = 'updateuser'");
//             assertTrue(userRs.next(), "User should exist");
//             int userId = userRs.getInt("id");
            
//             DatabaseUtils.executeUpdate("INSERT INTO profiles (user_id, first_name, last_name, date_of_birth, bio, favorite_game, total_play_time_minutes) " +
//                     "VALUES (" + userId + ", 'John', 'Doe', '1990-01-01', 'Original bio', 'OriginalGame', 100.0)");

//             String updateProfileSQL = "UPDATE profiles SET first_name = 'Jane', bio = 'Updated bio', favorite_game = 'NewGame' " +
//                     "WHERE user_id = (SELECT id FROM users WHERE username = 'updateuser')";
//             DatabaseUtils.executeUpdate(updateProfileSQL);

//             String querySQL = "SELECT p.* FROM profiles p JOIN users u ON p.user_id = u.id WHERE u.username = 'updateuser'";
//             ResultSet rs = DatabaseUtils.executeQuery(querySQL);

//             assertTrue(rs.next(), "Updated profile should exist");
//             assertEquals("Jane", rs.getString("first_name"), "First name should be updated");
//             assertEquals("Doe", rs.getString("last_name"), "Last name should remain unchanged");
//             assertEquals("Updated bio", rs.getString("bio"), "Bio should be updated");
//             assertEquals("NewGame", rs.getString("favorite_game"), "Favorite game should be updated");
//             assertEquals(100.0f, rs.getFloat("total_play_time_minutes"), 0.01f, "Play time should remain unchanged");

//         } catch (SQLException e) {
//             fail("SQL Exception: " + e.getMessage());
//         }
//     }

//     @Test
//     @DisplayName("Increment User Play Time")
//     void incrementUserPlayTime() {
//         try {
//             DatabaseUtils.executeUpdate("INSERT INTO users (username, password, email) VALUES ('playtimeuser', 'password123', 'playtime@example.com')");
//             ResultSet userRs = DatabaseUtils.executeQuery("SELECT id FROM users WHERE username = 'playtimeuser'");
//             assertTrue(userRs.next(), "User should exist");
//             int userId = userRs.getInt("id");
            
//             DatabaseUtils.executeUpdate("INSERT INTO profiles (user_id, first_name, last_name, total_play_time_minutes) " +
//                     "VALUES (" + userId + ", 'Play', 'Time', 100.0)");
    
//             // reset play time to 50
//             String resetPlayTimeSQL = "UPDATE profiles SET total_play_time_minutes = 50.0 " +
//                     "WHERE user_id = (SELECT id FROM users WHERE username = 'playtimeuser')";
//             DatabaseUtils.executeUpdate(resetPlayTimeSQL);
    
//             float incrementAmount = 30.5f;
//             String incrementPlayTimeSQL = "UPDATE profiles SET total_play_time_minutes = total_play_time_minutes + " + incrementAmount +
//                     " WHERE user_id = (SELECT id FROM users WHERE username = 'playtimeuser')";
//             DatabaseUtils.executeUpdate(incrementPlayTimeSQL);
    
//             String querySQL = "SELECT p.total_play_time_minutes FROM profiles p JOIN users u ON p.user_id = u.id WHERE u.username = 'playtimeuser'";
//             ResultSet rs = DatabaseUtils.executeQuery(querySQL);
    
//             assertTrue(rs.next(), "Profile with updated play time should exist");
//             assertEquals(80.5f, rs.getFloat("total_play_time_minutes"), 0.01f, "Play time should be incremented correctly");
    
//         } catch (SQLException e) {
//             fail("SQL Exception: " + e.getMessage());
//         }
//     }

// }