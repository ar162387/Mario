package minigames.server.EightBall;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.json.JsonObject;

import minigames.commands.CommandPackage;
import minigames.rendering.GameMetadata;
import minigames.rendering.NativeCommands;
import minigames.rendering.NativeCommands.LoadClient;
import minigames.rendering.RenderingCommand;
import minigames.rendering.RenderingPackage;


/**
 * EightBallGame is the server-side implementation of the EightBall game.
 * 
 * This class is responsible for managing the game state and players in a game of EightBall.
 * It stores the positions of balls following a shot as well as the shot itself.
 * 
 * This information is passed back to the clients at the start of each turn.
 *  1. The previous shot info is sent to the client and executed.
 *  2. The game state is sent and the client checks that their board matches the server
 */
public class EightBallGame {
    // Instantiates the logger for this class
    private static final Logger logger = LogManager.getLogger(EightBallGame.class);

    /**
     * Fields for the EightBallGame class<br>
     * name - The name of the game<br>
     * isJoinable - Boolean representing if the game is joinable<br>
     * maxLobbySize - Integer representing the maximum number of players in a game. Default 2.<br>
     * players - ArrayList of Player instances representing the players in the game<br>
     * ballPos - JsonObject representing the position of the balls on the table<br>
     *           In the format: { "1": {"x":0, "y":0, "type":stripes, "sunk":false}, ...<br>
     * shotInfo - JsonObject representing the shot information<br>
     *            In the format: { "angle":0, "power":0 }<br>
     */
    private final String name;
    private boolean isJoinable = true;
    // No more than 2 players supported
    private static int maxLobbySize = 2;
    private JsonObject ballPos;    
    private JsonObject shotInfo;


    /** Stores players currently in a game **/
    private ArrayList<String> players = new ArrayList<>();

    /**
     * Constructor for the EightBallGame class
     * Only used in the EightBallServer class to create
     * a new game in the minigame server.
     * @param gameName - The name of the game. Uses the player's name
     */
    public EightBallGame(String gameName) {
        this.name = gameName;
    }

    /**
     * Runs commands sent from the client to the server.
     * Sent commands come in two varieties:
     *  - Simple commands:  These are commands that do not contain any additional information and are executed as is. They are
     *                      typically used for basic testing and functionality.
     *  - Complex commands: These are commands that contain additional information in the form of a JSON Object. These are used to
     *                      send information about the game state or force to be imparted on the cue ball. This allows both players
     *                      to remain synchronised throughout the game.
     * @param cp - CommandPackage instance containing the commands to be run. This is sent using the sendSimple/ComplexCommand method wrappers
     *             in the EightBallGame (client side) class. The commands are passed to the MiniGameNetworkClient instance and sent to the server.
     * @return RenderingPackage instance containing the game metadata and the commands to be run. This is then passed to the client
     *         through the MinigameNetworkClient instance and executed in EightBallGame (client side).
     */
    public RenderingPackage runCommands(CommandPackage cp) {   
        logger.info("Received command package {}", cp);

        // Get player
        String player = players.stream()
                .filter(p -> p.equals(cp.player()))
                .findFirst()
                .orElse(null);

        // Ensure valid player
        if (player == null) {
            return new RenderingPackage(
                    getGameMetaData(),
                    List.of(new NativeCommands.ShowMenuError("Unable to find username " + cp.player()).toJson())
            );
        }

        ArrayList<JsonObject> commandList = new ArrayList<>();
        
        for (JsonObject command : cp.commands()) {
            // Simple commands, update server and test commands
            if(command.containsKey("command")) {
                switch(command.getString("command")) {
                    case "commandTest":
                        // Test command
                        commandList.add(new JsonObject().put("command", "commandTest"));
                        logger.info("Server has received the test command.");
                        break;
                    case "offline":
                        // Changes from online to local
                        this.isJoinable = false;
                        commandList.add(new JsonObject().put("command", "offline"));
                        logger.info(cp.gameId() + " is now a local game.");
                        break;
                    case "clientReady":
                        // Informs the client it can access the game engine
                        commandList.add(new JsonObject().put("command", "clientReady"));
                        logger.info("Client created, passing command server to game.");
                        break;
                    case "endGame":
                        // Closing game
                        commandList.add(new JsonObject().put("command", "endGame"));
                        logger.info("Closing game: " + cp.gameId());
                        break;
                    case "lastShot": 
                        // Send last shot to client
                        commandList.add(new JsonObject().put("lastShot", shotInfo));
                        logger.info("Sending Last Shot to " + player);
                        break;
                    case "startOfTurn":
                        // Send game state to client
                        commandList.add(new JsonObject().put("gameState", ballPos));
                        logger.info("Sending Game State to " + player);
                        break;
                    default:
                        commandList.add(new JsonObject().put("error", "Could not find command: " + command));
                        logger.warn(cp.player() + " has submitted an Unknown Command: " + command);
                        break;
                }
            } else {
                // Complex commands, passing info to server
                switch(command.fieldNames().toArray()[0].toString()) {
                    case "shotInfo":
                        // Update game state on server to be sent through startOfTurn
                        shotInfo = command.getJsonObject("shotInfo");
                        logger.info("Saved shot from: " + player);
                        break;
                    case "endOfTurn":
                        // Update game state on server to be sent through startOfTurn
                        ballPos = command.getJsonObject("endOfTurn");
                        logger.info("Game State Updated: " + command.getString("gameState"));
                        break;
                    default:
                        commandList.add(new JsonObject().put("error", "Could not find command: " + command));
                        logger.warn(cp.player() + " has submitted an Unknown Command: " + command);
                        break;
                }
            }
        }
        // Send commands back to client as necessary
        return new RenderingPackage(getGameMetaData(), commandList);
    }

    /**
     * Allows a player to join a game of EightBall.
     * Called from the EightBallServer class when a player attempts to join a game. 
     * @param playerName - The name of the player attempting to join the game.
     * @return RenderingPackage instance containing the game metadata and the commands to be run. This is then passed to the client
     */
    public RenderingPackage joinGame(String playerName) {
        // Is game joinable
        if (!isJoinable) {
            logger.info("Player {} cannot join game {}", playerName, name);
            return new RenderingPackage(
                    getGameMetaData(),
                    Arrays.stream(new RenderingCommand[] {
                            new NativeCommands.ShowMenuError("You cannot join this game. It is either full or singleplayer.")
                    }).map((r) -> r.toJson()).toList()
            );
        // Do not allow duplicate names in the same game. 
        } else if (players.stream().anyMatch(player -> player.equals(playerName))) {
            logger.info("Player with name {} already in {}",playerName,name);
            return new RenderingPackage(
                    getGameMetaData(),
                    Arrays.stream(new RenderingCommand[] {
                            new NativeCommands.ShowMenuError("That name is already taken!")
                    }).map((r) -> r.toJson()).toList()
            );
        } else {
            logger.info("Player {} joined game {}", playerName, name);
            // Adds player to game 
            players.add(playerName);
            
            // Update isJoinable status when lobby full.
            if (players.size() >= maxLobbySize) isJoinable = false;

            // Sends rendering commands to client to load the game
            ArrayList<JsonObject> renderingCommands = new ArrayList<>();
            renderingCommands.add(new LoadClient("EightBall", "EightBallServer", name, playerName).toJson());
            return new RenderingPackage(getGameMetaData(), renderingCommands);
        }
    }

    /** 
     * @return The name of the game. Just the host player's name
     */
    public String getServerName() {
        return name;
    }

    /** 
     * @return Boolean representing if the game is joinable. 
     */
    public boolean isJoinable() {
        return isJoinable;
    }

    /** 
     * @return Array of player names in the game. 
     */
    public String[] getPlayerNames() {
        return players.toArray(String[]::new);
    }

    /** 
     * Metadata for the current game.
     * Used to send commands to the client.
     * @return GameMetadata instance containing the game name, player names, and joinable status.
     */
    public GameMetadata getGameMetaData() {
        return new GameMetadata("EightBall", name, getPlayerNames(), isJoinable);
    }
}
