package minigames.server.services;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import minigames.server.api.achievement.Achievement;
import minigames.server.api.auth.HashingService;
import minigames.server.api.user.AppUser;
import minigames.server.api.user.UserProperties;
import minigames.server.api.user.UserProperty;
import minigames.server.api.user.UserServiceImpl;
import minigames.server.database.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JWTAuth jwtAuth;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(userRepository, jwtAuth);
    }

    @Test
    @DisplayName("Register new user successfully")
    void registerUserSuccess() {
        String username = "newUser";
        String password = "password123";
        String email = "user@example.com";

        when(userRepository.userExists(username)).thenReturn(Future.succeededFuture(false));
        when(userRepository.addUser(any(AppUser.class))).thenReturn(Future.succeededFuture());
        when(jwtAuth.generateToken(any(JsonObject.class), any(JWTOptions.class))).thenReturn("mockedToken");

        Future<String> result = userService.registerUser(username, password, email);

        assertTrue(result.succeeded());
        assertEquals("mockedToken", result.result());
        verify(userRepository).addUser(any(AppUser.class));
    }

    @Test
    @DisplayName("Register user fails when user already exists")
    void registerUserFailsWhenUserExists() {
        String username = "existingUser";
        String password = "password123";
        String email = "user@example.com";

        when(userRepository.userExists(username)).thenReturn(Future.succeededFuture(true));

        Future<String> result = userService.registerUser(username, password, email);

        assertTrue(result.failed());
        assertEquals("User already exists", result.cause().getMessage());
    }

    @Test
    @DisplayName("Login user successfully")
    void loginUserSuccess() {
        String username = "existingUser";
        String password = "password123";
        String hashedPassword = HashingService.hashPassword(password);

        JsonObject userProps = new JsonObject()
                .put(UserProperty.HASHED_PASSWORD.name().toLowerCase(), hashedPassword)
                .put(UserProperty.EMAIL.name().toLowerCase(), "user@example.com");

        when(userRepository.getHashedPassword(username)).thenReturn(Future.succeededFuture(hashedPassword));
        when(userRepository.getUserData(username))
                .thenReturn(Future.succeededFuture(new AppUser(1L, username, new UserProperties(userProps))));
        when(jwtAuth.generateToken(any(JsonObject.class), any(JWTOptions.class))).thenReturn("mockedToken");

        Future<String> result = userService.loginUser(username, password);

        assertTrue(result.succeeded());
        assertEquals("mockedToken", result.result());
    }

    @Test
    @DisplayName("Login user fails with invalid password")
    void loginUserFailsWithInvalidPassword() {
        String username = "existingUser";
        String password = "wrongPassword";
        String hashedPassword = HashingService.hashPassword("correctPassword");

        when(userRepository.getHashedPassword(username)).thenReturn(Future.succeededFuture(hashedPassword));

        Future<String> result = userService.loginUser(username, password);

        assertTrue(result.failed());
        assertEquals("Invalid password", result.cause().getMessage());
    }

    @Test
    @DisplayName("Get user data successfully")
    void getUserDataSuccess() {
        String username = "existingUser";
        JsonObject userProps = new JsonObject()
                .put(UserProperty.EMAIL.name().toLowerCase(), "user@example.com")
                .put(UserProperty.CREATED_AT.name().toLowerCase(), "2023-01-01T00:00:00Z");
        AppUser mockUser = new AppUser(1L, username, new UserProperties(userProps));

        when(userRepository.getUserData(username)).thenReturn(Future.succeededFuture(mockUser));

        Future<JsonObject> result = userService.getUserData(username);

        assertTrue(result.succeeded());
        assertEquals(mockUser.toJson(), result.result());
    }

    @Test
    @DisplayName("Get all users successfully")
    void getAllUsersSuccess() {
        JsonObject userProps1 = new JsonObject()
                .put(UserProperty.EMAIL.name().toLowerCase(), "user1@example.com");
        JsonObject userProps2 = new JsonObject()
                .put(UserProperty.EMAIL.name().toLowerCase(), "user2@example.com");

        List<AppUser> mockUsers = Arrays.asList(
                new AppUser(1L, "user1", new UserProperties(userProps1)),
                new AppUser(2L, "user2", new UserProperties(userProps2)));

        when(userRepository.getAllUsers()).thenReturn(Future.succeededFuture(mockUsers));

        Future<List<JsonObject>> result = userService.getAllUsers();

        assertTrue(result.succeeded());
        assertEquals(2, result.result().size());
    }

    @Test
    @DisplayName("Update user successfully")
    void updateUserSuccess() {
        JsonObject newUserDetails = new JsonObject()
                .put("username", "existingUser")
                .put("properties", new JsonObject().put("email", "updated@example.com"));

        JsonObject existingUserProps = new JsonObject()
                .put(UserProperty.EMAIL.name().toLowerCase(), "user@example.com");

        when(userRepository.getUserData("existingUser")).thenReturn(
                Future.succeededFuture(new AppUser(1L, "existingUser", new UserProperties(existingUserProps))));
        when(userRepository.updateUser(any(AppUser.class))).thenReturn(Future.succeededFuture());

        Future<Void> result = userService.updateUser("existingUser", newUserDetails);

        assertTrue(result.succeeded());
        verify(userRepository).updateUser(any(AppUser.class));
    }

    @Test
    @DisplayName("Increment play time successfully")
    void incrementPlayTimeSuccess() {
        String username = "existingUser";
        long minutes = 30;

        when(userRepository.incrementPlayTime(username, minutes)).thenReturn(Future.succeededFuture());

        Future<Void> result = userService.incrementPlayTime(username, minutes);

        assertTrue(result.succeeded());
        verify(userRepository).incrementPlayTime(username, minutes);
    }

    @Test
    void testGetUserAchievements() {
        String username = "testUser";
        List<Achievement> mockAchievements = Arrays.asList(
                new Achievement(1L, "Game1", "Achievement1", "Description1", 10, null, "image1.png"),
                new Achievement(2L, "Game2", "Achievement2", "Description2", 20, null, "image2.png"));

        when(userRepository.getUserAchievements(username)).thenReturn(Future.succeededFuture(mockAchievements));

        Future<JsonArray> result = userService.getUserAchievements(username);

        result.onComplete(ar -> {
            if (ar.succeeded()) {
                JsonArray jsonArray = ar.result();
                assertEquals(2, jsonArray.size());
                assertEquals("Achievement1", jsonArray.getJsonObject(0).getString("name"));
                assertEquals("Achievement2", jsonArray.getJsonObject(1).getString("name"));
            } else {
                ar.cause().printStackTrace();
            }
        });
    }

    @Test
    void testGetAllAchievements() {
        List<Achievement> mockAchievements = Arrays.asList(
                new Achievement(1L, "Game1", "Achievement1", "Description1", 10, null, "image1.png"),
                new Achievement(2L, "Game2", "Achievement2", "Description2", 20, null, "image2.png"));

        when(userRepository.getAllAchievements()).thenReturn(Future.succeededFuture(mockAchievements));

        Future<JsonArray> result = userService.getAllAchievements();

        result.onComplete(ar -> {
            if (ar.succeeded()) {
                JsonArray jsonArray = ar.result();
                assertEquals(2, jsonArray.size());
                assertEquals("Achievement1", jsonArray.getJsonObject(0).getString("name"));
                assertEquals("Achievement2", jsonArray.getJsonObject(1).getString("name"));
            } else {
                ar.cause().printStackTrace();
            }
        });
    }

    @Test
    void testGetAchievement() {
        String achievementId = "1";
        Achievement mockAchievement = new Achievement(1L, "Game1", "Achievement1", "Description1", 10, null,
                "image1.png");

        when(userRepository.getAchievement(achievementId)).thenReturn(Future.succeededFuture(mockAchievement));

        Future<JsonObject> result = userService.getAchievement(achievementId);

        result.onComplete(ar -> {
            if (ar.succeeded()) {
                JsonObject jsonObject = ar.result();
                assertEquals("Achievement1", jsonObject.getString("name"));
                assertEquals("Game1", jsonObject.getString("game"));
            } else {
                ar.cause().printStackTrace();
            }
        });
    }

    @Test
    @DisplayName("Get user scores successfully")
    void getUserScoresSuccess() {
        String username = "existingUser";
        JsonArray mockScores = new JsonArray().add(new JsonObject().put("game", "game1").put("score", 100));

        when(userRepository.getUserScores(username)).thenReturn(Future.succeededFuture(mockScores));

        Future<JsonArray> result = userService.getUserScores(username);

        assertTrue(result.succeeded());
        assertEquals(mockScores, result.result());
    }

    @Test
    @DisplayName("Add score successfully")
    void addScoreSuccess() {
        String username = "existingUser";
        String gameName = "game1";
        int score = 100;

        when(userRepository.addScore(username, gameName, score)).thenReturn(Future.succeededFuture());

        Future<Void> result = userService.addScore(username, gameName, score);

        assertTrue(result.succeeded());
        verify(userRepository).addScore(username, gameName, score);
    }

    @Test
    @DisplayName("Get global scoreboard successfully")
    void getGlobalScoreboardSuccess() {
        JsonArray mockScoreboard = new JsonArray()
                .add(new JsonObject().put("username", "user1").put("score", 100))
                .add(new JsonObject().put("username", "user2").put("score", 90));

        when(userRepository.getGlobalScoreboard()).thenReturn(Future.succeededFuture(mockScoreboard));

        Future<JsonArray> result = userService.getGlobalScoreboard();

        assertTrue(result.succeeded());
        assertEquals(mockScoreboard, result.result());
    }

    @Test
    @DisplayName("Get game scoreboard successfully")
    void getGameScoreboardSuccess() {
        String gameName = "game1";
        JsonArray mockScoreboard = new JsonArray()
                .add(new JsonObject().put("username", "user1").put("score", 100))
                .add(new JsonObject().put("username", "user2").put("score", 90));

        when(userRepository.getGameScoreboard(gameName)).thenReturn(Future.succeededFuture(mockScoreboard));

        Future<JsonArray> result = userService.getGameScoreboard(gameName);

        assertTrue(result.succeeded());
        assertEquals(mockScoreboard, result.result());
    }
}