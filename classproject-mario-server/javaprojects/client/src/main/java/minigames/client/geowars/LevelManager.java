package minigames.client.geowars;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import minigames.client.geowars.colliders.Collider;
import minigames.client.geowars.gameobjects.*;
import minigames.client.geowars.gameobjects.enemies.*;
import minigames.client.geowars.scenes.*;
import minigames.client.geowars.util.*;

import java.util.ArrayList;

/**
 * The {@code LevelManager} class manages the internal logic of a level in the
 * GeoWars game.
 * It is responsible for spawning enemies, keeping score, tracking time, and
 * managing the player's state (e.g., lives).
 * This class follows the Singleton design pattern, ensuring that only one
 * instance is active during gameplay.
 */
public class LevelManager {
    private static final Logger logger = LogManager.getLogger(LevelManager.class);

    private static LevelManager instance; // Singleton instance for LevelManager
    private GeoWars engine;
    private GameManager gameManager;
    private Level currentLevel;
    private Player player;
    private ArrayList<Enemy> enemies;
    private SpawnEvent nextSpawn;
    private double lastSpawnTime;
    private int lastWaveNumber;
    private boolean levelStarted;
    private boolean playerDead;
    private boolean pastPlayerDead;
    private boolean gameOver;
    private double playerDiedTime;
    private double playerDeathInterval = 1.5;
    private double score;

    /**
     * Private constructor for {@code LevelManager}.
     * Initialises the level manager with the game engine, current level, and player
     * details.
     * Sets the initial game state and prepares for enemy spawning.
     *
     * @param engine     the game engine
     * @param level      the current level being managed
     * @param playerName the name of the player
     */
    private LevelManager(GeoWars engine, Level level) {
        this.engine = engine;
        this.currentLevel = level;
        this.enemies = new ArrayList<>();
        nextSpawn = null;
        lastSpawnTime = 0;
        lastWaveNumber = 0;
        levelStarted = false;
        playerDead = false;
        pastPlayerDead = false;
        gameOver = false;

        if (GameManager.isInstanceNull()) {
            System.err.println("GameManager instance is null. Cannot start level.");
        } else {
            gameManager = GameManager.getInstance(null, null);
        }
    }

    /**
     * Checks whether the Singleton instance of {@code LevelManager} is null.
     *
     * @return {@code true} if the instance is null, otherwise {@code false}
     */
    public static boolean isInstanceNull() {
        return instance == null;
    }

    /**
     * Retrieves the singleton instance of {@code LevelManager}, creating it if
     * necessary.
     *
     * @param engine     the game engine
     * @param level      the current level being managed
     * @param playerName the name of the player
     * @return the singleton instance of {@code LevelManager}
     */
    public static LevelManager getInstance(GeoWars engine, Level level) {
        if (instance == null) {
            instance = new LevelManager(engine, level);
        }
        return instance;
    }

    /**
     * Starts the level and prepares the first spawn event.
     * This method should be called to initiate the level's gameplay and spawn
     * logic.
     */
    public void startLevel() {
        this.player = new Player(engine, currentLevel.getPlayerStartPosition().x,
                currentLevel.getPlayerStartPosition().y, currentLevel.getPlayerStartingLives());
        levelStarted = true;
        DeltaTime.startLevelTime(); // Start tracking level time
        nextSpawn = currentLevel.getNextSpawnEvent(0, 0, 0, false); // Get the first spawn event
    }

    /**
     * Updates the state of the level, including enemy spawning and player status.
     * This method should be called every frame from the game loop, even when the
     * game is paused.
     */
    public void updateLevel() {
        // TODO: Add time checks for: levelStart, playerDead, etc.
        if (!levelStarted) {
            startLevel();
        }

        if (gameOver) {
            // If the game is over, wait a short time before displaying the game over panel.
            if (DeltaTime.levelTime() - playerDiedTime >= playerDeathInterval && !engine.isPaused()) {
                // Pause the game
                gameManager.pauseLevel(true);
                // Display the game over panel
                currentLevel.gameOver();
            }

        } else {

            // On the first frame after the player dies, disable the player's colliders
            if (playerDead && !pastPlayerDead) {
                for (Collider collider : player.getColliders()) {
                    collider.setEnabled(false);
                }
            }

            // If the player is dead, handle the player death sequence
            if (playerDead) {
                // Step 6: Move the player to the starting position after a small delay.
                if (DeltaTime.levelTime() - playerDiedTime >= playerDeathInterval) {
                    // Move the player to the starting position
                    player.setPosition(
                            new Vector2D(currentLevel.getPlayerStartPosition().x,
                                    currentLevel.getPlayerStartPosition().y));
                    // Rotate the player to face up.
                    player.setRotation(0 - Math.PI / 2);
                }

                // Step 7: Re-enable the player's colliders and controls after another small
                // delay.
                if (DeltaTime.levelTime() - playerDiedTime >= playerDeathInterval * 2) {
                    for (Collider collider : player.getColliders()) {
                        collider.setEnabled(true);
                    }
                    playerDead = false;
                    player.setDead(false);
                }
            }

            // Update the level timer and score
            currentLevel.setTimer(DeltaTime.levelTime());
            currentLevel.setScore((int) this.score);

            // Check if the next spawn event is ready to execute
            if (nextSpawn != null && DeltaTime.levelTime() >= nextSpawn.getSpawnTime()) {
                spawnWave();
            }
        }

        pastPlayerDead = playerDead;
    }

    /**
     * Spawns the next wave of enemies.
     * This method is called when the current spawn event's conditions are met.
     */
    private void spawnWave() {
        lastWaveNumber++;
        lastSpawnTime = DeltaTime.levelTime();

        // Spawn the wave of enemies
        enemies.addAll(nextSpawn.spawnWave(engine, this));

        if (nextSpawn.getEnemyType() == 3) {
            Sound.getInstance().playSFX(Sound.Type.SPENEMY); // Spawn special enemy wave sound
        } else {
            Sound.getInstance().playSFX(Sound.Type.WAVE); // Spawn enemy wave sound
        }

        // Get the next spawn event
        nextSpawn = currentLevel.getNextSpawnEvent(lastSpawnTime, DeltaTime.levelTime(), lastWaveNumber, false);
    }

    /**
     * Adds an enemy to the list of active enemies in the level.
     *
     * @param enemy the {@code Enemy} to be added
     */
    public void addEnemy(Enemy enemy) {
        this.enemies.add(enemy);
    }

    /**
     * Handles the defeat of an enemy by removing it from the list and updating the
     * player's score.
     * If all enemies are defeated, the next wave is triggered.
     *
     * @param enemy the {@code Enemy} that was defeated
     */
    public void enemyDefeated(Enemy enemy) {

        this.score += enemy.getScore();

        /*
         * The player cannot fire bullets while dead, but if a bullet that was fired
         * before the player died hits an enemy, the player should receive score, but
         * the enemy should not be "removed" from the level manager. This is because
         * when the player dies, the enemy list is cleared, so if a player manages to
         * hit an enemy after they die, this can result in a strange scenario where the
         * post-death wave that has been generated spawns very early.
         */
        if (!playerDead) {

            this.enemies.remove(enemy);

            if (enemies.size() == 0) {
                spawnWave(); // Start the next wave when all enemies are defeated
            }
        }
    }

    /**
     * Handles the logic when the player is hit by an enemy.
     * This method is called when a collision is detected between the player and an
     * enemy.
     * 
     * To handle the player dying, there are multiple steps that need to be taken:
     * 1. The player's lives are decremented.
     * 2. The player's collider is disabled, and a flag set on both the player and
     * the levelManager that the player is currently dead.
     * 3. The time is captured when the player died.
     * 4. The screen is wiped of enemies.
     * 5. A new wave is scheduled if the player still has lives remaining, if not,
     * skip to step 8.
     * 6. After a short delay, the player is moved to the starting position.
     * 7. After another short delay, the player's collider is re-enabled, and the
     * player is marked as alive. The sequence ends here.
     * 8. After a short delay, the game over panel is displayed. The sequence ends
     * here.
     */
    public void playerDied() {
        // Step 1: Decrement player lives
        int playerLives = player.getLives();
        playerLives--;
        player.setLives(playerLives);
        currentLevel.setPlayerLives(playerLives);

        // Step 2: Set playerDead flags (player and levelManager)
        // The Player Collider(s) are disabled in the update method.
        playerDead = true;
        player.setDead(true);

        // Step 3: Capture the time the player died
        playerDiedTime = DeltaTime.levelTime();

        // Step 4: Wipe the screen of enemies
        new PlayerExplosion(engine, player.getPosition(), playerDeathInterval);
        enemies.clear();

        // Step 5: Schedule the next wave if the player still has lives
        if (playerLives > 0) {
            nextSpawn = currentLevel.getNextSpawnEvent(lastSpawnTime, DeltaTime.levelTime(), lastWaveNumber, true);

            // Step 6 and 7 are handled in the update method
        } else {
            // Step 8: Display the game over panel
            gameOver = true;
        }
    }

    public void setPause(boolean pause) {
        if (!gameOver) {
            currentLevel.setPause(pause);
        }
    }

    /**
     * Cleans up the level by destroying all enemies, resetting the player and level
     * state, and releasing resources.
     * This method should be called when the level ends or needs to be reset.
     */
    public void cleanup() {
        // Send the score to the game manager to be stored.
        gameManager.addArcadeScore((int) this.score, DeltaTime.levelTime());

        for (Enemy enemy : enemies) {
            enemy.destroy();
        }
        player.destroy();
        player = null;
        currentLevel = null;
        engine = null;
        this.enemies.clear();
        System.out.println("Level manager stopped.");
        instance = null; // Reset the singleton instance when the level ends
    }

    /**
     * Returns the player object.
     *
     * @return the {@code Player} object
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Returns the current level being managed by this {@code LevelManager}.
     *
     * @return the current {@code Level}
     */
    public Level getCurrentLevel() {
        return currentLevel;
    }
}