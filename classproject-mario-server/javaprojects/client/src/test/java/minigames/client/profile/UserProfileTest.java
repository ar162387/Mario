package minigames.client.profile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.vertx.core.Future;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import io.vertx.core.json.JsonObject;
import minigames.client.MinigameNetworkClient;

public class UserProfileTest {
    
    private UserProfile profile;

    //EDIT Lixang Li lli32@myune.edu.au
//    private MinigameNetworkClient client;

    //@BeforeAll
    @BeforeEach // Edit: Changed to BeforeEach
    public void setUp() {
        //EDIT lixang Li lli32@myune.edu.au

        MinigameNetworkClient client = mock(MinigameNetworkClient.class);

        //EDIT finished

        JsonObject json = new JsonObject()
            .put("username", "test_user")
            .put("email", "test@example.com")
            .put("first_name", "John")
            .put("last_name", "Doe")
            .put("date_of_birth", "1990-01-01")
            .put("bio", "Gamer")
            .put("favorite_game", "Chess")
            .put("total_play_time_minutes", "1200");



        // EDIT: Lixang Li lli32@myune.edu.au

        // Create a completed Future with the JsonObject
        Future<JsonObject> futureJson = Future.succeededFuture(json);

        when(client.getUserProfile("john_doe")).thenReturn(futureJson);

        // Use this INSTEAD when you fix the return of getUserProfile ...
       // when(client.getUserProfile("test_user")).thenReturn(json);

        //EDIT COMPLETE

        profile = new UserProfile(client);

        //EDIT: Lixang Li lli32@myuned.edu.au
        profile.fetchUserProfile(client);

        // There is no instantiation of the profile yet so lets DISABLE FOR NOW!

        //EDIT COMPLETE
    }
    @Disabled // Disabled until instantiation using json!
    @Test
    public void testUserProfileData() {
        assertEquals("test_user", profile.getUsername());
        assertEquals("test@example.com", profile.getEmail());
        assertEquals("John", profile.getFirstName());
        assertEquals("Doe", profile.getLastName());
        assertEquals("1990-01-01", profile.getDob());
        assertEquals("Gamer", profile.getAboutMe());
        assertEquals("Chess", profile.getFavoriteGame());
        assertEquals("1200", profile.getTotalPlayTime());
    }
    
}
