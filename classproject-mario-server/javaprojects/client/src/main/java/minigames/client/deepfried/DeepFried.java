package minigames.client.deepfried;

import java.awt.event.ActionListener;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.util.Collections;

import io.vertx.core.json.JsonObject;
import minigames.client.GameClient;
import minigames.client.MinigameNetworkClient;
import minigames.client.deepfried.states.MainMenuState;
import minigames.commands.CommandPackage;
import minigames.rendering.GameMetadata;
//import minigames.rendering.NativeCommands.QuitToMenu; // might use this

/**
 * A simple interface for a static screen cooking game.
 * 
 * Created by Hotfix Heroes.
 * 
 * // TO DO: implement GameStateManager class (handles game state of overall game)
 * 
 * 
 * 
 * ./gradlew server:run --args="12345"
 * ./gradlew client:run --args="localhost:12345"
 * 
 */

public class DeepFried implements GameClient {

    MinigameNetworkClient mnClient;
    GameMetadata gm;

    /** Your name */    
    String player;
    
    //private CardLayout cardLayout;
    //private JPanel cardPanel;
    
    private GameStateManager gameStateManager;

    /* Constructor */
    public DeepFried() {
        // not needed right now
    }

    /** 
     * Sends a command to the game at the server.
     * All commands are sent in JSON format,
     * eg. { "command": command }
     * 
     */
    public void sendCommand(String command) {
        JsonObject json = new JsonObject().put("command", command);
        // Collections.singletonList() is a quick way of getting a "list of one item"
        mnClient.send(new CommandPackage(gm.gameServer(), gm.name(), player, Collections.singletonList(json)));
    }
 

    /**
     * What we do when our client is loaded into the main screen
     */
    @Override
    public void load(MinigameNetworkClient mnClient, GameMetadata game, String player) {
        this.mnClient = mnClient;
        this.gm = game;
        this.player = player;

        gameStateManager = new GameStateManager(mnClient);

        // Push the initial MainMenuState to starrt with
        gameStateManager.pushState(new MainMenuState(gameStateManager, mnClient));

        // make a game loop
        runGameLoop();
    }

    @Override
    public void execute(GameMetadata game, JsonObject command) {
        this.gm = game; 
    }

    @Override
    public void closeGame() {
        // Nothing to do   
        //QuitToMenu.quitToMenu(mnClient);     
    }


    public void runGameLoop() {
        // Timer to simulate the game loop, calls the ActionListener (16ms == 60 FPS)
        Timer gameLoopTimer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Update the current state
                gameStateManager.update();
                // Get the graphics context and render
                mnClient.getMainWindow().getFrame().repaint(); 
            }
        });
        gameLoopTimer.start();
    }
    
}
