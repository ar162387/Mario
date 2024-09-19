package minigames.client.bomberman;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;
import minigames.client.bomberman.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class TurnRightStrategyTest {

    private TurnRightStrategy strategy;
    private CollisionManager collisionManager;
    private Enemy enemy;
    private Board board;

    @BeforeEach
    public void setUp() {
        collisionManager = mock(CollisionManager.class);
        board = mock(Board.class);
        Level level = mock(Level.class);
        when(board.getLevel()).thenReturn(level);
        when(level.getTileMap()).thenReturn(mock(Map.class));

        enemy = mock(Enemy.class);
        when(enemy.getX()).thenReturn(1.0);
        when(enemy.getY()).thenReturn(1.0);
        when(enemy.getBounds()).thenReturn(new Rectangle(1.0,1.0));

        strategy = new TurnRightStrategy();
    }

    @Test
    @DisplayName("Test enemy in intersection no collision on the right")
    public void testMoveNoCollisionOnTheRight() {
        when(collisionManager.checkTileCollision(any(), any())).thenReturn(false);

        when(enemy.getDirection()).thenReturn(Direction.UP);
        Pair<Integer, Integer> movement = strategy.move(enemy, board);

        // Verify movement and direction
        assertEquals(new Pair<>(1, 0), movement); // Should move right
    }

    @Test
    @DisplayName("Test enemy movement going down with collision on its right will go straight")
    public void testMoveWithCollisionOnRight() {
        // Simulate collision then empty tile on the right
        when(enemy.getDirection()).thenReturn(Direction.DOWN);
        when(collisionManager.checkTileCollision(any(), any())).thenReturn(true).thenReturn(false);

        Pair<Integer, Integer> movement = strategy.move(enemy, board);

       // Going STRAIGHT
        assertEquals(new Pair<>(-1, 0), movement,"Should be going down");
    }
    @Test
    @DisplayName("Test enemy movement going left with collision right and straight will go left (DOWN)")
    public void testMoveWithCollisionFrontRight() {

        when(enemy.getDirection()).thenReturn(Direction.LEFT); // Moving left
        // Simulate collision right,front
        when(collisionManager.checkTileCollision(any(), any())).thenReturn(true).thenReturn(true).thenReturn(false);

        Pair<Integer, Integer> movement = strategy.move(enemy, board);
        // Going DOWN
        assertEquals(new Pair<>(0, -1), movement,"Should be turning left now, so moving down");
    }
    @Test
    @DisplayName("Test enemy movement going right with collision right, straight,left will go back (LEFT)")
    public void testMoveWithCollisionFrontRightLeft() {

        when(enemy.getDirection()).thenReturn(Direction.RIGHT); // Moving RIGHT
        // Simulate collision right,front, left
        when(collisionManager.checkTileCollision(any(), any())).thenReturn(true).thenReturn(true).thenReturn(true);

        Pair<Integer, Integer> movement = strategy.move(enemy, board);
        // Going back (LEFT)
        assertEquals(new Pair<>(0, 1), movement,"Should be turning back now, so moving LEFt");
    }
}