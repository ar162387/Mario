package minigames.client.mario;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import io.vertx.core.json.JsonObject;
import minigames.client.MinigameNetworkClient;
import minigames.rendering.GameMetadata;

public class MarioClientTest {
    private MarioClient marioClient;
    private MinigameNetworkClient mnClient;

    @BeforeEach
    public void setUp() {
        marioClient = new MarioClient();
        mnClient = Mockito.mock(MinigameNetworkClient.class);
        marioClient.load(mnClient, Mockito.mock(GameMetadata.class), "player1");
    }

    @Test
    public void testSendCommand() {
        marioClient.sendCommand("move", "left", "medium");
        // Verify that mnClient.send is called with correct parameters
    }

    @Test
    public void testSendPositionUpdate() {
        marioClient.sendPositionUpdate(100, 200, true);
        // Verify that mnClient.send is called with correct parameters
    }

    @Test
    public void testExecute() {
        JsonObject command = new JsonObject().put("command", "updatePosition").put("x", 100).put("y", 200).put("onGround", true);
        marioClient.execute(Mockito.mock(GameMetadata.class), command);
        // Verify that the game state is updated correctly
    }

    @Test
    public void testCloseGame() {
        marioClient.closeGame();
        // Verify that resources are cleaned up correctly
    }
}