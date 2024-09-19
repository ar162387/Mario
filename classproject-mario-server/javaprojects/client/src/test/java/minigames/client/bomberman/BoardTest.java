package minigames.client.bomberman;

import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BoardTest {
    private Board board;
    private Level level;

    Enemy enemy = mock(Enemy.class);

    private GameController gameController;
    @BeforeEach
    void setUp() {
        level = new Level(10, 10, 2, true, true, 20);

        // Lets mock a player and enemies for the board constructor
        Player player = mock(Player.class);
        List<Enemy> enemies = new ArrayList<>(1);
        enemies.add(enemy);
        
        board = new Board(level, player, enemies); // Edit new constructor for testing - Li

        gameController = mock(GameController.class); // Edit: Take mocks for controller
    }

    @Test
    void testBoardInitialization() {
        assertNotNull(board.getPlayer());
        assertEquals(1, board.getEnemies().size()); //Edit Should only be one enemy now - Li
        assertTrue(board.getBombs().isEmpty());
    }

    @Test
    void testAddAndRemoveEnemy() {
        int initialEnemyCount = board.getEnemies().size();
        Enemy newEnemy = mock(Enemy.class); //Edit to use mock instead - Li
        board.addEnemy(newEnemy);
        assertEquals(initialEnemyCount + 1, board.getEnemies().size());

        board.removeEnemy(newEnemy);
        assertEquals(initialEnemyCount, board.getEnemies().size());
    }



    @Test
    void testBoardDimensions() {
        assertEquals(10, board.getWidth());
        assertEquals(10, board.getHeight());
    }

    @Test
    void testGetLevel() {
        assertEquals(level, board.getLevel());
    }

    @Test
    void testGetPlayer() {
        assertNotNull(board.getPlayer());
        assertTrue(board.getPlayer() instanceof Player);
    }

    @Test
    void testGetEnemies() {
        List<Enemy> enemies = board.getEnemies();
        assertNotNull(enemies);
        assertEquals(1, enemies.size()); //Edit its one enemy now omg - Li
        for (Enemy enemy : enemies) {
            assertNotNull(enemy);
            assertTrue(enemy instanceof Enemy);
        }
    }

    @Test
    void testGetBombs() {
        List<Bomb> bombs = board.getBombs();
        assertNotNull(bombs);
        assertTrue(bombs.isEmpty());
    }

    @Test
    void testAddAndRemoveBomb() {
        assertTrue(board.getBombs().isEmpty());
        //Bomb newBomb = new Bomb(3, 3,1);
        Bomb newBomb = mock(Bomb.class); //EDIT: Changed to mock to fix issue - Li
        board.addBomb(newBomb);
        assertEquals(1, board.getBombs().size());
        assertTrue(board.getBombs().contains(newBomb));

        board.removeBomb(newBomb);
        assertTrue(board.getBombs().isEmpty());
    }

    @Test
    void testExplosionDoesntHitDiagonalEnemy() {

        enemy.setPosition(1,1);


        Explosion explosion = new Explosion(0.1,0.1,2,board.getLevel().getTileMap());
        board.addExplosion(explosion);
        gameController.updateExplosions(1L,board.getPlayer());
    }
}