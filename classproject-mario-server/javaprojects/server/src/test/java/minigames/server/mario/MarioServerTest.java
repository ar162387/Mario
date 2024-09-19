package minigames.server.mario;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.vertx.core.Future;
import minigames.commands.CommandPackage;
import minigames.rendering.RenderingPackage;

class MarioServerTest {

    private MarioServer server;

    @BeforeEach
    void setUp() {
        server = new MarioServer();
    }

    @Test
    void testNewGame() {
        Future<RenderingPackage> future = server.newGame("Player1");
        assertTrue(future.succeeded());
        RenderingPackage rp = future.result();
        assertNotNull(rp);
    }

    @Test
    void testJoinGame() {
        server.newGame("Player1");
        Future<RenderingPackage> future = server.joinGame("TestGame", "Player2");
        assertTrue(future.succeeded());
        RenderingPackage rp = future.result();
        assertNotNull(rp);
    }

    @Test
    void testCallGame() {
        // Mock CommandPackage and MarioGame
        CommandPackage cp = mock(CommandPackage.class);
        when(cp.gameId()).thenReturn("TestGame");
        when(cp.player()).thenReturn("Player1");

        MarioGame game = mock(MarioGame.class);
        server.games.put("TestGame", game);

        Future<RenderingPackage> future = server.callGame(cp);
        assertTrue(future.succeeded());
        RenderingPackage rp = future.result();
        assertNotNull(rp);
    }
}