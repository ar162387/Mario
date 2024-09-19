package minigames.client.minesweeper;

/**
 * This interface is used as a listener for between Grid.java and userInterface.java.
 * Author:Jake Mayled
 */
public interface GridAndUserInterfaceListener {
    /**
     * This method is called when the number of flagged mines is updated 
     * @param flagsPlaced number of mines remaining
     */
    void onMinesRemainingChanged(int flagsPlaced);

    /**
     * This method is called when the score is updated
     * @param score, updated score
     */
    void onScoreChange(int score);
}
