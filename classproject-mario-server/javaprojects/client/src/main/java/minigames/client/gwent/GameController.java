package minigames.client.gwent;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import minigames.client.MinigameNetworkClient;
import minigames.client.gwent.ui.GameBoard;
import minigames.client.gwent.ui.MainMenu;
import minigames.gwent.Deck.Deck;
import minigames.gwent.Deck.DeckBuilder;
import minigames.gwent.GameState;
import minigames.gwent.Player;
import minigames.gwent.cards.Card;
import minigames.gwent.cards.abilities.Ability;
import minigames.gwent.cards.abilities.Spy;

/**
 * GameController manages the state and logic of the Gwent game.
 * TODO: Handle networking && ui
 */
public class GameController {

    private static final Logger logger = LogManager.getLogger(GameController.class);

    private final MinigameNetworkClient networkClient;
    private GameBoard gameBoard;
    private MainMenu mainMenu;
    private GameState gameState;

    /**
     * Constructor for GameController.
     *
     * @param networkClient The network client used for communication.
     */
    public GameController(MinigameNetworkClient networkClient) {
        this.networkClient = networkClient;
        initialiseMainMenu();
    }

    /**
     * Initialises the main menu.
     */
    private void initialiseMainMenu() {
        mainMenu = new MainMenu(this);
        mainMenu.show();
    }

    /**
     * Starts a new game by initialising the game board.
     */
    public void startNewGame() {
        logger.info("Initialising new game");

        DeckBuilder deckBuilder = new DeckBuilder();
        Deck player1Deck = null;
        Deck player2Deck = null;

        try {
            logger.info("Loading cards from json...");
            player1Deck = deckBuilder.buildDeckFromJson();
            player2Deck = deckBuilder.buildDeckFromJson();
        } catch (IOException e) {
            logger.error("Error building decks from JSON", e);
        }

        logger.info("Shuffling decks");
        player1Deck.shuffleDeck();
        player2Deck.shuffleDeck();

        Player player1 = new Player("Player 1", player1Deck);
        Player player2 = new Player("Player 2", player2Deck);
        gameState = new GameState(player1, player2);

        logger.info("Drawing initial cards for players");
        for (int i = 0; i < 10; i++) {
            gameState.getPlayerOne().drawFromDeck();
            gameState.getPlayerTwo().drawFromDeck();
        }

        //TODO: Implement redraw phase (allow players to redraw up to 2 cards before the first round)

        newRound();

        gameBoard = new GameBoard(this, this.gameState);
        gameBoard.updateBoard();
        gameBoard.show();
    }

    /**
     * Returns the network client.
     *
     * @return The network client.
     */
    public MinigameNetworkClient getNetworkClient() {
        return networkClient;
    }

    /**
     * Method for when a player has played a card from the UI
     */
    public void playCard(int pos, Card card) {
        Player currentPlayer = gameState.getCurrentPlayer(); // Get the current player from the game state

        // Only play if the action is from the current player
        if (currentPlayer.getHand().getCards().contains(card)) {
            logger.info("Player {} attempting to play {}", currentPlayer.getName(), card.getName());
            if (!currentPlayer.getHand().removeCard(card)) {
                logger.info("{} does not have card {} in hand", currentPlayer.getName(), card);
                throw new IllegalArgumentException("Card not found in hand.");
            }

        // If the card is a spy card, add it to the opponent's board and activate the ability as a one-off
        if (card.getAbility() instanceof Spy) {
            gameState.getOpposingPlayer().addCardToBoard(pos, card);
    
        } else {

            // Add the card to the board
            gameState.getCurrentPlayer().addCardToBoard(pos, card);
        }
        

        //Call the activateBoardAbilities method to activate all abilities on the board
        activateBoardAbilities();

        // Switch turns after player plays a card
        gameState.switchTurn();

        // Refresh the game board to reflect the new state
        gameBoard.updateBoard();

        // Check if the current player has no more cards
        if (currentPlayer.getHand().getCards().isEmpty()) {
            currentPlayer.passRound();
            }
        }
    }

    /**
     * Method for activating all abilities on the board
     * Excludes spy cards to prevent repeated card draw
     */
    public void activateBoardAbilities() {
        logger.info("Activating board abilities");
        
        //Activate power card abilities for both players
        gameState.getPlayerOne().getPowerCardsOnBoard().forEach(card -> {
            Ability ability = (card).getAbility();
            if (ability != null && !(ability instanceof Spy)) { 
                ability.activate(gameState);
            }
        });
        gameState.getPlayerTwo().getPowerCardsOnBoard().forEach(card -> {
            Ability ability = (card).getAbility();
            if (ability != null && !(ability instanceof Spy)) {
                ability.activate(gameState);
            }
        });

        //Activate weather card abilities for both players
        gameState.getWeatherCards().forEach(card -> {
            Ability ability = (card).getAbility();
            if (ability != null) {
                ability.activate(gameState);
            }
        });
    }



    /**
     * Method for when a new round starts
     */
    public void newRound(){
        logger.info("Starting a new round");
        gameState.getPlayerOne().clearBoard();
        gameState.getPlayerTwo().clearBoard();
        
        logger.info("Drawing card for new round");
        gameState.getPlayerOne().drawFromDeck();
        gameState.getPlayerTwo().drawFromDeck();
        
        gameState.startNewRound();
    }

    /**
     * Method for when a player has passed the turn in UI
     */
    public void passRound() {
        logger.info("Player {} passing round", gameState.getCurrentPlayer().getName());

        gameState.getCurrentPlayer().passRound();

        if (gameState.getOpposingPlayer().hasEndedRound()) {
            logger.info("Both players passed turn - determining round winner");
            Player winner = determineRoundWinner();  // Determine the round winner

            if (winner != null) {  // If there is no draw
                System.out.println(winner.getName() + " wins the round!");
                Player loser = (winner == gameState.getPlayerOne()) ? gameState.getPlayerTwo() : gameState.getPlayerOne();
                if (loser.reduceHealth() == 0) {
                    hasWonGame(winner);  // Winner wins the game
                    return;
                }
            } 
            //TODO: Check draw logic
            //else {
               // System.out.println("Draw! Both players lose a life.");
               // gameState.getPlayerOne().reduceHealth();
               // gameState.getPlayerTwo().reduceHealth();
            //}
            if (gameState.getPlayerOne().getHealth() > 0 && gameState.getPlayerTwo().getHealth() > 0) {
                newRound();  // Start a new round
                gameBoard.updateBoard(); // update the board as we draw more cards each round
            }
        } else {
            logger.info("Switching turns");
            gameState.switchTurn();  // If the opposing player hasn't ended the round, switch turns
        }
    }

    /**
     * Determines the winner of a round.
     *
     * @return Player who won, null if draw
     */
    public Player determineRoundWinner() {
        int playerOneStrength = gameState.getPlayerOne().calculatePlayerStrength();
        int playerTwoStrength = gameState.getPlayerTwo().calculatePlayerStrength();

        if (playerOneStrength > playerTwoStrength) {
            return gameState.getPlayerOne();
        } else if (playerTwoStrength > playerOneStrength) {
            return gameState.getPlayerTwo();
        } else {
            return null;
        }
    }

    /**
     * Logic to handle winning a game.
     *
     * @param player Player who won
     */
    public void hasWonGame(Player player) {
        System.out.println(player.getName() + " has won the game!");

        //Hide the game board
        gameBoard.hide();
        
        //Open the main menu
        initialiseMainMenu();
    }
}
