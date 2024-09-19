package minigames.client.hangman;

import io.vertx.core.json.JsonObject;
import minigames.client.GameClient;
import minigames.client.MinigameNetworkClient;
import minigames.rendering.GameMetadata;
import minigames.commands.CommandPackage;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;


/**
 * Hangman game client implementation.
 */
public class Hangman implements GameClient {

    // Instance variables for the Hangman game client
    private MinigameNetworkClient mnClient;  // The network client used to communicate with the server
    private GameMetadata gameMetadata;       // Metadata associated with the current game session
    private String player;                   // The name of the player

    /**
     * Constructor for the Hangman game client.
     * Initializes a new instance of the Hangman class.
     */
    public Hangman() {
        // No initialization required in the default constructor
    }

    /**
     * Loads the Hangman game client with necessary data.
     *
     * @param mnClient The MinigameNetworkClient instance for communication with the server.
     * @param game     The game metadata containing details about the current game session.
     * @param player   The name of the player for this game session.
     */
    public void load(MinigameNetworkClient mnClient, GameMetadata game, String player) {
        this.mnClient = mnClient;      // Assign the provided MinigameNetworkClient instance to the local variable
        this.gameMetadata = game;      // Store the game metadata for use during the game session
        this.player = player;          // Set the player name for this game session
    }

    /**
     * Executes a command received from the server or client actions.
     * This method is called to perform actions based on the provided command.
     *
     * @param game     The game metadata associated with the current game session.
     * @param command  The command in JSON format that needs to be executed.
     */
    @Override
    public void execute(GameMetadata game, JsonObject command) {
        // Process and execute the received command
    }

    /**
     * Closes the Hangman game client.
     * This method is called when the game session needs to be terminated.
     */
    @Override
    public void closeGame() {
        // Perform any cleanup or termination logic for the game client
    }
}
