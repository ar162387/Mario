package minigames.client.EightBall;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import io.vertx.core.json.JsonObject;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import minigames.client.GameClient;
import minigames.client.MinigameNetworkClient;
import minigames.client.EightBall.UI.MenuManager;
import minigames.rendering.GameMetadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * EightBallGame is the client-side implementation of the EightBall game.
 * This is the class instantiated when a player creates a new game 
 * through the minigame network client. It is used to instantiate
 * the EightBallCommands class to pass through the game engine.
 */
public class EightBallGame implements GameClient {
    // Instantiates the logger for this class
    Logger Logger = LogManager.getLogger(EightBallGame.class);

    /**
     * Fields for the EightBallGame class
     * 
     * mnClient      - MinigameNetworkClient instance used to send commands to the server
     * gm            - GameMetadata instance containing information about the game currnetly being played
     * online        - Boolean representing if the game is hosted online (online by default, command used to host offline)
     * playerName    - String representing the player's name
     * commandServer - EightBallCommands instance passed to controller (potentially also engine) to send commands
     * controller    - EightBallController instance used to control the game
     * engine        - EightBallEngine instance used to run the game. Currently commented out as not used.
     */
    MinigameNetworkClient mnClient;
    GameMetadata gm;
    Boolean online;
    String playerName;
    EightBallCommands commandServer;
    EightBallController controller;
    // EightBallEngine engine;
    MenuManager manager;


    /**
     * Load a game instance with the given metadata and player name.
     * This is called when a player creates or joins a game from the minigame network.
     * 
     * @param mnClient - MinigameNetworkClient instance to send commands to the server
     * @param meta     - GameMetadata instance containing information about the game
     * @param player   - String representing the player's name
     */
    @Override
    public void load(MinigameNetworkClient mnClient, GameMetadata meta, String player) {
        this.mnClient = mnClient;
        this.gm = meta;
        this.playerName = player;   
        this.commandServer = new EightBallCommands(mnClient, meta, player);
        run();   
    }

    /**
     * Creates the game window, called when game is loaded by the Minigame Client.
     * 
     * Creates a swing window/frame then attaches a JFXPanel that the game is
     * rendered inside of. Also responsible for passing the command server to 
     * the controller (and sometimes engine).
     */
    public void run() {
        // TODO closing the window does not stop the game.
        // TODO closing the window does not allow you to create a new game afterwords (blank window)
        // Function begins Eightball minigame
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("8Ball Window");
            JFXPanel fxPanel = new JFXPanel();

            frame.add(fxPanel);
            frame.setSize(1024, 768);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setVisible(true);

            Platform.runLater(() -> {
                try {
                    manager = new MenuManager(commandServer);
                    fxPanel.setScene(manager.getMainStage().getScene());   
                    frame.pack();
                } catch(Exception e) {{
                    e.printStackTrace();
                }}
            });
        });
    }

    /**
     * Execute a command received from the server. May either be a simple command or a complex command
     * which are divded into two switch statements.This is called by the minigame server and does not
     * have to be actioned in the codebase of the client.
     * 
     * @param meta    - GameMetadata instance containing information about the game. 
     * @param command - JSONObject containing the command to be executed. Commands containing "command" as a key
     *                  are simple commands, all others are considered complex commands and are handled in the second switch statement.
     */
    @Override
    public void execute(GameMetadata meta, JsonObject command) {
        // Filter between simple and complex commands
        // Simple Commands
        if (command.containsKey("command")) {
            Logger.info("Received simple command: {}", command);
            switch (command.getString("command")) {
                case "testCommand":
                    Logger.info("Client has received test command.");
                    break;
                case "clientReady":
                    // From the server, game controller has been initialised
                    Logger.info("Passing command server to game");
                    this.controller = manager.getGameViewManager().getController();
                    controller.setCommandServer(commandServer);
                    
                    // this.engine = controller.getEngine();
                    // engine.setCommandServer(commandServer);
                    break;
                case "endGame":
                    Logger.info("Game has ended.");
                    // TODO - Implement end game method
                    // controller.endGame();
                    break;
                case "offline":
                    Logger.info("Game is now a local game.");
                    this.online = false;
                    break;
                default:
                    Logger.warn("Unknown command from the server: {}", command);
                    break;
            }
        } else {
            // Complex Commands
            Logger.info("Received complex command: {}", command);
            // All commands are sent as singletons
            switch (command.fieldNames() // returns a set
                        .toArray()[0]    // to an array and get the first element
                        .toString()){    // convert to string)
                case "gameState":
                    // Updates the game state with the information from the server
                    Logger.info("Received game state command.");
                    controller.setBallPos(command.getJsonObject("gameState"));
                    break;
                case "lastShot":
                    // Shares the last shot with the game for a "replay"
                    Logger.info("Received last shot from Server.");
                    controller.setLastShot(command.getJsonObject("lastShot"));
                    break;
                default:
                    Logger.warn("Unknown command from the server: {}", command);
                    break;
            }
        }
    }

    /**
     * Close the game. This is called when the player closes the game window.
     * Sends a simple command to the server to end the game.
     * 
     * //TODO - Should also be integrated into the GUI
     */
    @Override
    public void closeGame() {
        commandServer.sendSimpleCommand("endGame");
    }
}