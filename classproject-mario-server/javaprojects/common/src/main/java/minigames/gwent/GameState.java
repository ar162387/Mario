package minigames.gwent;

import minigames.gwent.cards.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * GameState manages the state of the game, including which player is currently active,
 * and who starts each round.
 */
public class GameState {
    private Player playerOne;
    private Player playerTwo;
    private int currentPlayerIndex;
    private int startingPlayerIndex;

    /**
     * Constructor for GameState.
     *
     * @param playerOne The first player.
     * @param playerTwo The second player.
     */
    public GameState(Player playerOne, Player playerTwo) {
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.startingPlayerIndex = 1; // TODO: temporary testing - change back to: new Random().nextInt(2)
        this.currentPlayerIndex = startingPlayerIndex;
    }

    /**
     * Gets the player whose turn it currently is.
     *
     * @return The current player.
     */
    public Player getCurrentPlayer() {
        return currentPlayerIndex == 0 ? playerOne : playerTwo;
    }

    /**
     * Gets the player who is not currently taking their turn.
     *
     * @return The opposing player.
     */
    public Player getOpposingPlayer() {
        return currentPlayerIndex == 0 ? playerTwo : playerOne;
    }

    /**
     * Switches the turn to the other player.
     */
    public void switchTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % 2;
        
    }

    /**
     * Starts a new round by switching the starting player and setting the current player accordingly.
     */
    public void startNewRound() {
        startingPlayerIndex = (startingPlayerIndex + 1) % 2;
        currentPlayerIndex = startingPlayerIndex;
    }

    /**
     * Gets the first player.
     *
     * @return Player one.
     */
    public Player getPlayerOne() {
        return playerOne;
    }

    /**
     * Gets the second player.
     *
     * @return Player two.
     */
    public Player getPlayerTwo() {
        return playerTwo;
    }

    /**
     * Gets the index of the current player (0 for player one, 1 for player two).
     *
     * @return The current player's index.
     */
    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    /**
     * Gets the index of the player who starts the current round.
     *
     * @return The starting player's index.
     */
    public int getStartingPlayerIndex() {
        return startingPlayerIndex;
    }

    /**
     * Sets the index of the current player - Mostly for testing.
     *
     * @param currentPlayerIndex The index of the current player (must be 0 or 1).
     */
    public void setCurrentPlayerIndex(int currentPlayerIndex) {
        if (currentPlayerIndex == 0 || currentPlayerIndex == 1) {
            this.currentPlayerIndex = currentPlayerIndex;
        } else {
            throw new IllegalArgumentException("Invalid player index. It must be 0 or 1.");
        }
    }

    /**
     * Returns true if it's the players turn, otherwise false.
     *
     * @param player    player to hceck
     * @return          boolean, true if it's the players turn.
     */
    public boolean isPlayerTurn(Player player) {
        return getCurrentPlayer() == player;
    }

    /**
     * Retrieves all weather cards currently on the board from both players.
     *
     * @return A list of weather cards on the board.
     */
    public List<Card> getWeatherCards() {
        List<Card> weatherCards = new ArrayList<>();

        // Add weather cards from player one
        weatherCards.addAll(playerOne.getWeatherCardsOnBoard());

        // Add weather cards from player two
        weatherCards.addAll(playerTwo.getWeatherCardsOnBoard());

        return weatherCards;
    }
}
