package minigames.client.minesweeper;
/**
 * This class is used to handle the various states in the minesweeper game.
 * Author: Matt Hayes
 */
public class GameController {
    private Minesweeper minesweeper;
    private userInterface userInterface;

    public GameController(Minesweeper minesweeper) {
        this.minesweeper = minesweeper;
    }
    
    public void navMainMenu() {
        // Transition to WIN state
        minesweeper.changeGameState(GameState.MENU);
    }

    public void startGame() {
        // Transition to WIN state
        minesweeper.changeGameState(GameState.PLAYING);
    }

    public void playerWins() {
        // Transition to WIN state
        minesweeper.changeGameState(GameState.WIN);
    }

    public void playerLoses() {
        // Transition to LOSE state
        minesweeper.changeGameState(GameState.LOSE);
    }

    public void showLeaderboard() {
        // Transition to LEADERBOARD state
        minesweeper.changeGameState(GameState.LEADERBOARD);
    }

    public void resetGame() {
        // Transition to beginner state
        minesweeper.changeGameState(GameState.PLAYING);
    }
}


