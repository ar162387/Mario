package minigames.server.repositories;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import minigames.server.api.profile.Profile;
import minigames.server.api.profile.ProfileServiceImpl;
import minigames.server.database.repositories.ProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceImplTest {

    @Mock
    private ProfileRepository profileRepository;

    private ProfileServiceImpl profileService;

    @BeforeEach
    void setUp() {
        profileService = new ProfileServiceImpl(profileRepository);
    }

    @Test
    @DisplayName("Get profile for existing user returns correct JSON")
    void getExistingProfile() {
        String username = "testUser";
        Profile mockProfile = new Profile();
        mockProfile.set(Profile.Field.FIRST_NAME, "John");
        mockProfile.set(Profile.Field.LAST_NAME, "Doe");
        mockProfile.set(Profile.Field.TOTAL_PLAY_TIME_MINUTES, "60");

        when(profileRepository.getProfile(username)).thenReturn(Future.succeededFuture(mockProfile));

        Future<JsonObject> result = profileService.getProfile(username);

        assertTrue(result.succeeded());
        JsonObject profileJson = result.result();
        assertEquals("John", profileJson.getString("first_name"));
        assertEquals("Doe", profileJson.getString("last_name"));
        assertEquals("60", profileJson.getString("total_play_time_minutes"));
    }

    @Test
    @DisplayName("Get non-existing profile returns empty JSON")
    void getNonExistingProfile() {
        String username = "nonExistingUser";
        when(profileRepository.getProfile(username)).thenReturn(Future.succeededFuture(null));

        Future<JsonObject> result = profileService.getProfile(username);

        assertTrue(result.succeeded());
        assertTrue(result.result().isEmpty());
    }

    @Test
    @DisplayName("Create profile with valid data succeeds")
    void createValidProfile() {
        String username = "newUser";
        JsonObject profileData = new JsonObject()
                .put("username", username)
                .put("first_name", "Jane")
                .put("last_name", "Doe")
                .put("total_play_time_minutes", "0");

        when(profileRepository.createProfile(eq(username), any(Profile.class))).thenReturn(Future.succeededFuture());

        Future<Void> result = profileService.createProfile(username, profileData);

        assertTrue(result.succeeded());
        verify(profileRepository).createProfile(eq(username), any(Profile.class));
    }

    @Test
    @DisplayName("Update existing profile succeeds")
    void updateExistingProfile() {
        String username = "existingUser";
        JsonObject updatedData = new JsonObject()
                .put("username", username)
                .put("bio", "Updated bio")
                .put("total_play_time_minutes", "30");

        Profile existingProfile = new Profile();
        when(profileRepository.getProfile(username)).thenReturn(Future.succeededFuture(existingProfile));
        when(profileRepository.updateProfile(eq(username), any(Profile.class))).thenReturn(Future.succeededFuture());

        Future<Void> result = profileService.updateProfile(username, updatedData);

        assertTrue(result.succeeded());
        verify(profileRepository).updateProfile(eq(username), any(Profile.class));
    }

    @Test
    @DisplayName("Update non-existing profile fails")
    void updateNonExistingProfile() {
        String username = "nonExistingUser";
        JsonObject updatedData = new JsonObject()
                .put("username", username)
                .put("bio", "New bio");

        when(profileRepository.getProfile(username)).thenReturn(Future.succeededFuture(null));

        Future<Void> result = profileService.updateProfile(username, updatedData);

        assertTrue(result.failed());
        assertEquals("Profile not found", result.cause().getMessage());
    }

    @Test
    @DisplayName("Increment play time for existing profile succeeds")
    void incrementExistingProfilePlayTime() {
        String username = "gamer";
        long minutes = 30;
        Profile existingProfile = new Profile();
        existingProfile.set(Profile.Field.TOTAL_PLAY_TIME_MINUTES, "60");

        when(profileRepository.getProfile(username)).thenReturn(Future.succeededFuture(existingProfile));
        when(profileRepository.updateProfile(eq(username), any(Profile.class))).thenReturn(Future.succeededFuture());

        Future<Void> result = profileService.incrementPlayTime(username, minutes);

        assertTrue(result.succeeded());
        verify(profileRepository).updateProfile(eq(username), argThat(profile -> 
            "90".equals(profile.get(Profile.Field.TOTAL_PLAY_TIME_MINUTES))
        ));
    }

    @Test
    @DisplayName("Increment play time for non-existing profile fails")
    void incrementNonExistingProfilePlayTime() {
        String username = "nonExistingUser";
        long minutes = 30;

        when(profileRepository.getProfile(username)).thenReturn(Future.succeededFuture(null));

        Future<Void> result = profileService.incrementPlayTime(username, minutes);

        assertTrue(result.failed());
        assertEquals("Profile not found", result.cause().getMessage());
    }
}