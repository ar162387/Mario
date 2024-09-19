package minigames.client.snake;
// EDITED by Corey Wiford  11/09/2024
// added the package above to allow project to compile

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import minigames.client.snake.ui.SnakeGraphic;
import minigames.client.snake.ui.GameBoard;
import minigames.client.snake.ui.SnakeGame;
import static org.mockito.Mockito.*;

public class SnakeGraphicToGameBoardObserverTest {

    private SnakeGraphic snake;
    private GameBoard gameBoard;
    private SnakeGame snakeGame;  // Mocked SnakeGame

    @BeforeEach
    public void setUp() {
        // Mock SnakeGame instead of passing null
        snakeGame = Mockito.mock(SnakeGame.class);

        // Initialize the SnakeGraphic object
        snake = new SnakeGraphic(560, 600, 23, 6);

        // Create a spy on the GameBoard, using the mocked SnakeGame
        gameBoard = Mockito.spy(new GameBoard(snakeGame, snake));

        // Register the GameBoard as an observer of the SnakeGraphic
        snake.addObserver(gameBoard);
    }

    @Test
    public void testGameBoardRepaintOnSnakeMove() {
        // Act: Simulate snake movement
        snake.move();

        // Assert: Verify that the GameBoard's repaint method was called once
        verify(gameBoard, times(1)).repaint();
    }
}
