package minigames.client.EightBall;

import java.util.Collections;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.json.JsonObject;
import minigames.client.MinigameNetworkClient;
import minigames.commands.CommandPackage;
import minigames.rendering.GameMetadata;

/**
 * Abstracted from EightBallGame.java, this class is used to send commands to the server.
 * Can be passed to other elements of the game as needed.
 * 
 * There are two types of commands that can be sent to the server:
 *   - Simple commands:  These are commands that do not contain any additional information and are executed as is. Typically
 *                       for requesting information from the server, testing, or basic functionality.
 * 
 *   - Complex commands: These are commands that contain additional information in the form of a JSON Object. These are used to
 *                       send information about the game state or force to be imparted on the cue ball. This allows both players 
 *                       to have a synchronised game state without having to send near constant rendering commands from a centralised
 *                       server to both players. 
 */
public class EightBallCommands {
    // Instantiates the logger for this class
    Logger Logger = LogManager.getLogger(EightBallGame.class);

    /**
     * Fields for the Commands class
     * 
     * mnClient   - MinigameNetworkClient instance used to send commands to the server
     * gm         - GameMetadata instance containing information about the game currnetly being played
     * playerName - String representing the player's name
     */
    MinigameNetworkClient mnClient;
    GameMetadata gm;
    String playerName;

    /**
     * Constructor for the EightBallCommands class.
     * This is passed to all relevant sections of the game to allow for command sending to the server.
     * 
     * @param mnClient   - MinigameNetworkClient instance used to send commands to the server
     * @param gm         - GameMetadata instance containing information about the game currently being played
     * @param playerName - String representing the player's name
     */
    public EightBallCommands(MinigameNetworkClient mnClient, GameMetadata gm, String playerName) {
        this.mnClient = mnClient;
        this.gm = gm;
        this.playerName = playerName;
    }

    /**
     * Send a command to the server. Does not carry any further information.
     * 
     * @param command - The command to be sent to the server. Does not contain any additional information and is executed as is.
     */
    public void sendSimpleCommand(String command) {
        Logger.info("Sending simple command: {}", command);
        mnClient.send(new CommandPackage(
            gm.gameServer(), gm.name(), playerName, 
            Collections.singletonList(new JsonObject().put("command", command))
        ));
    }

    /**
     * Send a complex command with further information to the server.
     * An alternative to sendSimpleCommand() that allows the player to send information (in a JSON Object) to the server.
     * Should implement one of the following commandTypes:
     *                  "gameState" - command argument should carry a JSONObject with the position of each ball. 
     *                                Should be of the form {"1": {"x": 0, "y": 0, "type": Stripes, "Sunk": false}, "2": {"x": 0, "y": 0}, ...}
     *                  "lastShot" - command argument should carry a JSONObject with the force/angle on the shooter for the last shot
     * @param commandType - String representing the type of command being sent. 
     * @param command     - JSONObject containing information about the game state or force to be imparted on the cue ball
     */
    public void sendComplexCommand(String commandType, JsonObject command) {
        Logger.info("Sending compelx command: {} with Json: {}", commandType, command);
        mnClient.send(new CommandPackage(
            gm.gameServer(), gm.name(), playerName, 
            Collections.singletonList(new JsonObject().put(commandType, command))
        ));
    }
}
