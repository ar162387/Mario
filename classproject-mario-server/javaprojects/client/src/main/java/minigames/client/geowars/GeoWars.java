package minigames.client.geowars;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.*;
import static java.util.concurrent.TimeUnit.*;

import javax.swing.JPanel;

import minigames.client.*;
import minigames.client.geowars.colliders.Collider;
import minigames.client.geowars.colliders.Collision;
import minigames.client.geowars.gameobjects.*;
import minigames.client.geowars.ui.UIElement;
import minigames.client.geowars.util.*;
import minigames.commands.CommandPackage;
import minigames.rendering.GameMetadata;
import minigames.rendering.NativeCommands.*;

/**
 * GeoWars GameEngine class.
 * Handles the intial connection from the server to the client.
 * Creates the GameManager on load.
 * Runs the Game Loop by calling Update() on every GameObject.
 * Triggers collisions.
 */
public class GeoWars implements GameClient, Runnable {

    // Misc Util
    private static final Logger logger = LogManager.getLogger(GeoWars.class);

    // Server Information
    private MinigameNetworkClient mnClient;
    private GameMetadata gm;

    // Engine Data
    private GeoWarsRenderer renderer;
    public JPanel mainPanel;
    public GameManager gameManager;
    private InputManager inputManager;
    public int gameState;
    private boolean sceneChanged = false;
    private ArrayList<GameObject> gameObjects;
    private ArrayList<GameObject> stagedGameObjects;
    private ArrayList<GameObject> destroyedGameObjects;
    private ArrayList<Collider> colliders;
    private ArrayList<Collider> stagedColliders;
    private ArrayList<Collider> destroyedColliders;

    // Game Loop
    private ScheduledFuture<?> gameLoop;
    private ScheduledFuture<?> gameLoopMonitor;
    private long gameLoopExecution = 0;

    // Game default settings
    private int gameLoopPeriod = 20; // Number of milliseconds between game loop updates
    public static int SCREEN_WIDTH = 1280;
    public static int SCREEN_HEIGHT = 720; // Should probably make this larger.

    // Flags
    private boolean paused = false;

    /**
     * GeoWars constructor.
     */
    public GeoWars() {
        gameObjects = new ArrayList<>();
        stagedGameObjects = new ArrayList<>();
        destroyedGameObjects = new ArrayList<>();
        colliders = new ArrayList<>();
        stagedColliders = new ArrayList<>();
        destroyedColliders = new ArrayList<>();
        gameState = 0;
    }

    // TODO - All initialisation of the overall game should be done here.
    @Override
    public void load(MinigameNetworkClient mnClient, GameMetadata game, String player) {
        // Get information from the server.
        logger.info("Launching GeoWars");
        this.mnClient = mnClient;
        this.gm = game;

        // Instantiate Renderer
        renderer = GeoWarsRenderer.getInstance(this);

        // Instantiate GameManager
        gameManager = GameManager.getInstance(this, player);

        // Instantiate InputManager
        inputManager = InputManager.getInstance(this, mainPanel);

        // Show the window
        renderer.show();

        // Start the animator
        mnClient.getAnimator().requestTick(renderer);

        // Start the game
        startGameLoop();
        gameManager.startGame();
    }

    @Override
    public void execute(GameMetadata game, JsonObject command) {

    }

    @Override
    public void closeGame() {
        // Stop the game loop
        logger.info("Stopping the Game Loop");
        gameLoopMonitor.cancel(true);
        gameLoop.cancel(false);

        // Clean up the InputManager
        inputManager.cleanup();
        inputManager = null;

        // Clean up the GameManager
        gameManager = null;

        // Clean up the engine
        stagedColliders.clear();
        stagedGameObjects.clear();
        destroyedColliders.clear();
        destroyedGameObjects.clear();

        // Stop the animator
        logger.info("Stopping the Animator");
        renderer.stop();

        logger.info("Closing GeoWars");
    }

    // Sets up the Game Loop
    private void startGameLoop() {
        logger.info("Starting Game Loop");
        DeltaTime.startDeltaTime();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        gameLoop = scheduler.scheduleAtFixedRate(this, 0, gameLoopPeriod, MILLISECONDS);

        // Monitor the game loop
        ScheduledExecutorService monitor = Executors.newScheduledThreadPool(1);
        gameLoopMonitor = monitor.schedule(() -> {
            try {
                gameLoop.get();
            } catch (Exception e) {
                logger.error("Game Loop has stopped!");
                e.printStackTrace();
            }
        }, 1000, MILLISECONDS);
    }

    // Runs the Game Loop
    @Override
    public void run() {
        // logger.info(System.currentTimeMillis());
        DeltaTime.calcDeltaTime();

        /**
         * This still seems vulnerable to concurrecny issues.
         * What happens if a new object is added to stagedGameObjects while we're in the
         * addAll() method?
         * We might need to look into a locking mechanism.
         */

        // Remove any destroyed GameObjects and Colliders
        if (destroyedGameObjects.size() > 0) {
            // logger.info("Removing destroyed GameObjects");
            gameObjects.removeAll(destroyedGameObjects);
            destroyedGameObjects.clear();
        }
        if (destroyedColliders.size() > 0) {
            // logger.info("Removing destroyed Colliders");
            colliders.removeAll(destroyedColliders);
            destroyedColliders.clear();
        }

        // Add any staged GameObjects and Colliders
        if (stagedGameObjects.size() > 0) {
            // logger.info("Adding staged GameObjects");
            for (GameObject o : stagedGameObjects) {
                o.start();
            }
            gameObjects.addAll(stagedGameObjects);
            stagedGameObjects.clear();
        }
        if (stagedColliders.size() > 0) {
            // logger.info("Adding staged Colliders");
            colliders.addAll(stagedColliders);
            stagedColliders.clear();
        }

        // Update the Level
        gameManager.updateLevel();

        // If the level updated and caused the scene to change, we need to end the
        // execution of this game loop here.
        if (sceneChanged) {
            sceneChanged = false;
            return;
        }

        // Update all GameObjects
        // logger.info("Number of GameObjects: " + gameObjects.size());
        for (GameObject o : gameObjects) {
            if (o.isEnabled()) {
                // If the game is paused, only update UIElements
                if (o instanceof UIElement || !paused) {
                    o.update();

                    // If this object is now off-screen, destroy it.
                    o.checkOffScreen();

                    // If an update caused the scene to change, we need to end the execution of
                    // this game loop here.
                    if (sceneChanged) {
                        sceneChanged = false;
                        return;
                    }
                }
            }
        }

        /*
         * This is O(n^2) and could be a performance bottleneck, keep an eye on this,
         * and if it
         * becomes a problem, we can optimise in various ways.
         */
        // No collisions are checked while the game is paused.
        if (!paused) {
            // Check for collisions
            for (Collider current : colliders) {
                if (current.isEnabled()) {
                    for (Collider other : colliders) {
                        if (current != other && other.isEnabled() && other.getParent() != current.getParent()) {
                            if (current.isColliding(other)) {
                                current.collide(new Collision(other, current));

                                // If a collision caused the scene to change, we need to end
                                // the execution of this game loop here.
                                if (sceneChanged) {
                                    sceneChanged = false;
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }

        // Increment the game loop execution count
        // gameLoopExecution++;

        // logger.info("Finished Update Loop!");
    }

    // Getters

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public ArrayList<GameObject> getGameObjects() {
        return gameObjects;
    }

    public ArrayList<Collider> getColliders() {
        return colliders;
    }

    public boolean isPaused() {
        return paused;
    }

    // Setters

    public void setMainPanel(JPanel mainPanel) {
        this.mainPanel = mainPanel;
    }

    // Register a GameObject with the GameEngine
    public void registerGameObject(GameObject o) {
        stagedGameObjects.add(o);

        // If the object can be drawn, add it to the renderer
        if (o.draw(null)) {
            renderer.registerGameObject(o);
        }
    }

    // Remove a GameObject from the GameEngine
    public void removeGameObject(GameObject o) {
        destroyedGameObjects.add(o);

        // If the object can be drawn, remove it from the renderer as well.
        if (o.draw(null)) {
            renderer.removeGameObject(o);
        }
    }

    /**
     * Register a Collider with the GameEngine.
     * 
     * @param c The Collider to register.
     */
    public void registerCollider(Collider c) {
        stagedColliders.add(c);
    }

    /**
     * Remove a Collider from the GameEngine.
     * 
     * @param c The Collider to remove.
     */
    public void removeCollider(Collider c) {
        destroyedColliders.add(c);
    }

    /**
     * Safely destroy all registered GameObjects and Colliders.
     */
    public void cleanEngine() {
        for (GameObject o : gameObjects) {
            o.destroy();
        }
        for (Collider c : colliders) {
            c.destroy();
        }
    }

    /**
     * Set the pause state of the game.
     * 
     * @param p The pause state of the game.
     */
    public void setPause(boolean p) {
        if (p) {
            logger.info("Game Paused");
        } else {
            logger.info("Game Resumed");
        }
        paused = p;
        inputManager.setPaused(p);
    }

    /**
     * Retrieves the current state the game is set to.
     * 0 - Menu
     * 1 - Game in progress
     * 2 - Game Paused
     * 
     * @return gameState
     */
    public int getGameState() {
        return this.gameState;
    }

    /**
     * Updates the game state to be a new value:
     * Current valid values:
     * 0 - Menu
     * 1 - Game in progress
     * 2 - Game Paused
     * 
     * @param newState - state the should be set to.
     */
    public void setGameState(int newState) {
        this.gameState = newState;
    }

    /**
     * Sets the sceneChanged flag to true.
     * This will trigger the game loop to end the current execution.
     */
    public void sceneChanged() {
        sceneChanged = true;
    }

    /**
     * Quits the game.
     * Sends a command to the server to quit the game.
     */
    public void quit() {
        JsonObject commandPackage = new JsonObject();
        JsonObject command = (new QuitToMenu()).toJson();
        JsonArray commands = new JsonArray();
        commands.add(command);
        commandPackage.put("commands", commands);
        commandPackage.put("gameServer", gm.gameServer());
        commandPackage.put("gameId", gm.name());
        commandPackage.put("player", gameManager.getPlayerName());
        System.out.println(commandPackage);

        CommandPackage cp = CommandPackage.fromJson(commandPackage);
        System.out.println(cp);
        mnClient.send(cp);
    }
}
