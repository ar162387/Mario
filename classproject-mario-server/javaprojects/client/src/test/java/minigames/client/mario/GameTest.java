package minigames.client.mario;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class GameTest {
    private Game game;
    private MarioClient marioClient;
    private Player player;

    @BeforeEach
    public void setUp() {
        marioClient = Mockito.mock(MarioClient.class);
        player = new Player(50, 50, 64, 96);
        game = new Game(marioClient, player);
    }

    @Test
    public void testUpdateEnemy() {
        game.updateEnemy(0, 100, 150, true);
        Enemy enemy = game.getEnemies().get(0);
        assertEquals(100, enemy.getX());
        assertEquals(150, enemy.getY());
        assertTrue(enemy.isActive());
    }

    @Test
    public void testUpdatePlayerPosition() {
        game.updatePlayerPosition(200, 300, true);
        assertEquals(200, player.getX());
        assertEquals(300, player.getY());
    }

    @Test
    public void testUpdatePlayerHealth() {
        game.updatePlayerHealth(75);
        assertEquals(75, game.getPlayerHealth());
    }
}
