package minigames.server.repositories;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import minigames.server.api.user.AppUser;
import minigames.server.api.user.UserProperties;
import minigames.server.database.DatabaseUtils;
import minigames.server.database.repositories.UserRepositoryImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserRepositoryImplTest {

    private UserRepositoryImpl userRepository;
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() {
        userRepository = new UserRepositoryImpl();
        mockResultSet = mock(ResultSet.class);
    }

    @Test
    @DisplayName("Get hashed password for existing user")
    void getHashedPasswordForExistingUser() throws SQLException {
        try (MockedStatic<DatabaseUtils> mockedDatabaseUtils = mockStatic(DatabaseUtils.class)) {
            mockedDatabaseUtils.when(() -> DatabaseUtils.executeQuery(anyString())).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getString("password")).thenReturn("hashedPassword123");

            Future<String> future = userRepository.getHashedPassword("existingUser");

            assertTrue(future.succeeded());
            assertEquals("hashedPassword123", future.result());
        }
    }

    @Test
    @DisplayName("Get hashed password for non-existing user")
    void getHashedPasswordForNonExistingUser() throws SQLException {
        try (MockedStatic<DatabaseUtils> mockedDatabaseUtils = mockStatic(DatabaseUtils.class)) {
            mockedDatabaseUtils.when(() -> DatabaseUtils.executeQuery(anyString())).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(false);

            Future<String> future = userRepository.getHashedPassword("nonExistingUser");

            assertTrue(future.succeeded());
            assertNull(future.result());
        }
    }

    @Test
    @DisplayName("Check if user exists by username")
    void userExistsByUsername() throws SQLException {
        try (MockedStatic<DatabaseUtils> mockedDatabaseUtils = mockStatic(DatabaseUtils.class)) {
            mockedDatabaseUtils.when(() -> DatabaseUtils.executeQuery(anyString())).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);

            Future<Boolean> future = userRepository.userExists("existingUser");

            assertTrue(future.succeeded());
            assertTrue(future.result());
        }
    }

    @Test
    @DisplayName("Check if user exists by id")
    void userExistsById() throws SQLException {
        try (MockedStatic<DatabaseUtils> mockedDatabaseUtils = mockStatic(DatabaseUtils.class)) {
            mockedDatabaseUtils.when(() -> DatabaseUtils.executeQuery(anyString())).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);

            Future<Boolean> future = userRepository.userExists("existingUser");

            assertTrue(future.succeeded());
            assertTrue(future.result());
        }
    }

    @Test
    @DisplayName("Add new user")
    void addUser() {
        try (MockedStatic<DatabaseUtils> mockedDatabaseUtils = mockStatic(DatabaseUtils.class)) {
            mockedDatabaseUtils.when(() -> DatabaseUtils.executeUpdate(anyString())).thenReturn(1);

            UserProperties properties = new UserProperties(new JsonObject()
                    .put("hashed_password", "hashedPassword123")
                    .put("email", "user@example.com"));
            AppUser user = new AppUser(1L, "newUser", properties);

            Future<Void> future = userRepository.addUser(user);

            assertTrue(future.succeeded());
        }
    }

    @Test
    @DisplayName("Get user data for existing user by username")
    void getUserDataForExistingUserByUsername() throws SQLException {
        try (MockedStatic<DatabaseUtils> mockedDatabaseUtils = mockStatic(DatabaseUtils.class)) {
            mockedDatabaseUtils.when(() -> DatabaseUtils.executeQuery(anyString())).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getLong("id")).thenReturn(1L);
            when(mockResultSet.getString("password")).thenReturn("hashedPassword123");
            when(mockResultSet.getString("email")).thenReturn("user@example.com");
            when(mockResultSet.getTimestamp("created_at")).thenReturn(new Timestamp(System.currentTimeMillis()));
            when(mockResultSet.getTimestamp("last_login")).thenReturn(new Timestamp(System.currentTimeMillis()));

            Future<AppUser> future = userRepository.getUserData("existingUser");

            assertTrue(future.succeeded());
            assertNotNull(future.result());
            assertEquals("existingUser", future.result().getUsername());
        }
    }

    @Test
    @DisplayName("Get user data for existing user by id")
    void getUserDataForExistingUserById() throws SQLException {
        try (MockedStatic<DatabaseUtils> mockedDatabaseUtils = mockStatic(DatabaseUtils.class)) {
            mockedDatabaseUtils.when(() -> DatabaseUtils.executeQuery(anyString())).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getLong("id")).thenReturn(1L);
            when(mockResultSet.getString("password")).thenReturn("hashedPassword123");
            when(mockResultSet.getString("email")).thenReturn("user@example.com");
            when(mockResultSet.getTimestamp("created_at")).thenReturn(new Timestamp(System.currentTimeMillis()));
            when(mockResultSet.getTimestamp("last_login")).thenReturn(new Timestamp(System.currentTimeMillis()));

            Future<AppUser> future = userRepository.getUserData(1L);

            assertTrue(future.succeeded());
            assertNotNull(future.result());
            assertEquals(1L, future.result().getId());
        }
    }

    @Test
    @DisplayName("Get all users")
    void getAllUsers() throws SQLException {
        try (MockedStatic<DatabaseUtils> mockedDatabaseUtils = mockStatic(DatabaseUtils.class)) {
            mockedDatabaseUtils.when(() -> DatabaseUtils.executeQuery(anyString())).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true, true, false);
            when(mockResultSet.getLong("id")).thenReturn(1L, 2L);
            when(mockResultSet.getString("username")).thenReturn("user1", "user2");
            when(mockResultSet.getString("password")).thenReturn("hashedPassword1", "hashedPassword2");
            when(mockResultSet.getString("email")).thenReturn("user1@example.com", "user2@example.com");
            when(mockResultSet.getTimestamp("created_at")).thenReturn(new Timestamp(System.currentTimeMillis()));
            when(mockResultSet.getTimestamp("last_login")).thenReturn(new Timestamp(System.currentTimeMillis()));

            Future<List<AppUser>> future = userRepository.getAllUsers();

            assertTrue(future.succeeded());
            assertNotNull(future.result());
            assertEquals(2, future.result().size());
        }
    }

    @Test
    @DisplayName("Update existing user")
    void updateUser() {
        try (MockedStatic<DatabaseUtils> mockedDatabaseUtils = mockStatic(DatabaseUtils.class)) {
            mockedDatabaseUtils.when(() -> DatabaseUtils.executeUpdate(anyString())).thenReturn(1);

            UserProperties properties = new UserProperties(new JsonObject()
                    .put("hashed_password", "newHashedPassword")
                    .put("email", "updated@example.com"));
            AppUser user = new AppUser(1L, "existingUser", properties);

            Future<Void> future = userRepository.updateUser(user);

            assertTrue(future.succeeded());
        }
    }

    @Test
    @DisplayName("Increment play time for user")
    void incrementPlayTime() {
        try (MockedStatic<DatabaseUtils> mockedDatabaseUtils = mockStatic(DatabaseUtils.class)) {
            mockedDatabaseUtils.when(() -> DatabaseUtils.executeUpdate(anyString())).thenReturn(1);

            Future<Void> future = userRepository.incrementPlayTime("existingUser", 30);

            assertTrue(future.succeeded());
        }
    }
}