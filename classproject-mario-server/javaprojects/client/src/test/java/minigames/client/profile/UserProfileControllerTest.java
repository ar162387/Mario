package minigames.client.profile;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import minigames.client.MinigameNetworkClient;

public class UserProfileControllerTest {

    private UserProfileController controller;


    @BeforeAll
    public static void setUp() { //Edit: Change to static
        //EDIT using mocks- Lixang Li lli32@myune.edu.au

        Vertx vertx = mock(Vertx.class);
        MinigameNetworkClient client = mock(MinigameNetworkClient.class);
        //EDIT Complete

        //MinigameNetworkClient client = new MinigameNetworkClient(vertx);
        UserProfileController controller = new UserProfileController(client);
    }

    
    @Test
    @Disabled // Disabled for now
    public void testFetchUserProfile() {
        //setUp();
        Future<UserProfileView> futureView = controller.fetchUserProfile();
        futureView.onComplete(result -> {
            assertTrue(result.succeeded());
            assertNotNull(result.result());
        });
    }

}
