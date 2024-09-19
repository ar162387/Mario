package minigames.server.mario;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.json.JsonObject;
import minigames.commands.CommandPackage;
import minigames.rendering.GameMetadata;
import minigames.rendering.NativeCommands.LoadClient;
import minigames.rendering.RenderingPackage;

/**
 * Represents an actual Mario game in progress.
 */
public class MarioGame {

    private static final Logger logger = LogManager.getLogger(MarioGame.class);
    private CollisionDetection collisionDetection;
    private String name;
    private Map<String, Player> players = new HashMap<>();
    private List<Enemy> enemies = new ArrayList<>();
    private Random random = new Random();
    private static final int MIN_SPAWN_INTERVAL = 3000;
    private static final int MAX_SPAWN_INTERVAL = 10000;
    private int enemySpawnInterval = MIN_SPAWN_INTERVAL;
    private double lastEnemySpawnTime;
    private int score;
    private final ScheduledExecutorService gameLoopExecutor;

    private static final int MIN_SPAWN_INTERVAL_TICKS = 30; // Minimum interval in game ticks
    private static final int MAX_SPAWN_INTERVAL_TICKS = 200; // Maximum interval in game ticks
    private int enemySpawnIntervalTicks = MIN_SPAWN_INTERVAL_TICKS; // Current spawn interval in ticks
    private int currentTick = 0; // Game tick counter
    private int lastEnemySpawnTick = 0; // Last tick when an enemy was spawned
    private List<Herb> herbs = new ArrayList<>();
    private static final int MIN_HERB_SPAWN_INTERVAL_TICKS = 50; // Minimum interval in game ticks for herb spawning
    private static final int MAX_HERB_SPAWN_INTERVAL_TICKS = 5000; // Maximum interval in game ticks for herb spawning
    private int herbSpawnIntervalTicks = MIN_HERB_SPAWN_INTERVAL_TICKS; // Current spawn interval for herbs
    private int lastHerbSpawnTick = 0; // Last tick when a herb was spawned
    private int lastWaveSpawnTick = 0; // Last tick when an enemy wave was spawned
    private static final int WAVE_SPAWN_INTERVAL_TICKS = 800; // Interval in ticks for wave spawning
    private int waveCounter = 0;


    /**
     * Constructs a new MarioGame object.
     *
     * @param name The name of the game.
     */
    public MarioGame(String name) {
        this.name = name;
        this.gameLoopExecutor = Executors.newSingleThreadScheduledExecutor();
        this.collisionDetection = new CollisionDetection();
        initGame();
        startGameLoop();
    }

    /**
     * Initializes the game state, setting the initial score and any necessary game setup.
     */
    private void initGame() {
        score = 2000;
        logger.info("Game initialized - server side");
    }

    /**
     * Resets the game state to its initial configuration.
     */
    public void resetGame() {
        initGame();
        logger.info("Game reset.");
    }

    /**
     * Starts the game loop using a ScheduledExecutorService to regularly call updateGame().
     * The game loop is called every 100ms.
     */
    private void startGameLoop() {
        gameLoopExecutor.scheduleAtFixedRate(() -> {
            try {
                updateGame();
            } catch (Exception e) {
                logger.error("Error during game update: ", e);
            }
        }, 0, 50, TimeUnit.MILLISECONDS);
    }

    /**
     * Performs the main game loop updates, including spawning enemies, moving them, and checking for collisions.
     */
    public void updateGame() {
        currentTick++; // Increment the game tick counter
        adjustSpawnRateBasedOnScore();

        // Check for herb spawning
        if (shouldSpawnHerb()) {
            spawnHerb();
        }

        if (shouldSpawnEnemy()) {
            spawnEnemy();
        }

        if (shouldSpawnWave()) { // If certain conditions are met to spawn a wave
            spawnEnemyWave();
            lastEnemySpawnTick = currentTick; // Reset spawn timer after a wave
        }

        moveEnemies();
        removeDefeatedEnemies();
        removeCollectedHerbs();

        collisionDetection.checkCollisions(players, enemies, herbs);

        for (Player player : players.values()) {
            if (player.isJumping()) {
                player.jump();  // Continuously call jump until it completes
            }
        }

        for (Enemy enemy : enemies) {
            if (!enemy.isActive()) { // Check if the enemy is defeated
                enemy.moveDown();  // Call moveDown to move the defeated enemy below the ground
            }
        }

        for (Herb herb : herbs) {
            if (herb.isActive()) { // Only move active herbs
                herb.fall(); // Call the fall method to make the herb fall towards the ground
            }
        }
    }


    private boolean shouldSpawnHerb() {
        // Check if enough ticks have passed to spawn a new herb
        if (currentTick - lastHerbSpawnTick >= herbSpawnIntervalTicks) {
            lastHerbSpawnTick = currentTick; // Update last spawn tick for herb
            return true;
        }
        return false;
    }

    private void spawnHerb() {
        if (herbs.size() < 2) { // Limit the number of herbs on the screen to 3 at a time
            int spawnX = random.nextInt(1000); // Random X between 0 and the width of the map
            int spawnY = 0; // Start from the top of the map

            herbs.add(new Herb(spawnX, spawnY, 30, 30)); // Create a new herb and add it to the list

            // Log and adjust the next spawn interval
            herbSpawnIntervalTicks = random.nextInt(MAX_HERB_SPAWN_INTERVAL_TICKS - MIN_HERB_SPAWN_INTERVAL_TICKS + 1) + MIN_HERB_SPAWN_INTERVAL_TICKS;
            logger.debug("Next herb will spawn in: {} ticks", herbSpawnIntervalTicks);
        }
    }

    private boolean shouldSpawnEnemy() {
        // Check if enough ticks have passed to spawn a new enemy
        if (currentTick - lastEnemySpawnTick >= enemySpawnIntervalTicks) {
            lastEnemySpawnTick = currentTick; // Update last spawn tick
            return true;
        }
        return false;
    }

    /**
     * Spawns a new enemy and adds it to the list of enemies, if conditions are met.
     */
    // Adjusted to use the random enemy speed and wave spawn pattern
    private void spawnEnemy() {
        if (enemies.size() < 5) { // Allows up to 5 enemies at a time
            int randomSpeed = random.nextInt(5 , 11); // Random speed between 5 and 10
            int spawnX = random.nextInt(1150 - 100) + 100; // Random X between 100 and 1150
            int spawnY = 820; // Fixed Y or can randomize further

            enemies.add(new Enemy(spawnX, spawnY, 50, 50, randomSpeed));

            // Log and adjust the next spawn interval
            enemySpawnInterval = random.nextInt(MAX_SPAWN_INTERVAL - MIN_SPAWN_INTERVAL + 1) + MIN_SPAWN_INTERVAL;
            logger.debug("Next enemy will spawn in: {} ms", enemySpawnInterval);
        }
    }

    // Implement wave-based spawning logic
    private void spawnEnemyWave() {
        for (int i = 0; i < 5; i++) {
            int randomSpeed = random.nextInt(6) + 5; // Random speed between 5 and 10
            int spawnX = (i * 200) % 1250; // Pattern-based spawn positions
            int spawnY = 820;

            enemies.add(new Enemy(spawnX, spawnY, 50, 50, randomSpeed));
        }
        logger.debug("Spawned a wave of enemies.");
    }

    // Adjust the spawn rate based on the player's score
    private void adjustSpawnRateBasedOnScore() {
        int highestScore = getHighestPlayerScore();

        if (highestScore >= 100 && highestScore < 200) {
            enemySpawnIntervalTicks = Math.max(MIN_SPAWN_INTERVAL_TICKS, MAX_SPAWN_INTERVAL_TICKS - 10); // 190 ticks
        } else if (highestScore >= 300 && highestScore < 400) {
            enemySpawnIntervalTicks = Math.max(MIN_SPAWN_INTERVAL_TICKS, MAX_SPAWN_INTERVAL_TICKS - 20); // 180 ticks
        } else if (highestScore >= 500 && highestScore < 600) {
            enemySpawnIntervalTicks = Math.max(MIN_SPAWN_INTERVAL_TICKS, MAX_SPAWN_INTERVAL_TICKS - 40); // 160 ticks
        } else if (highestScore >= 700 && highestScore < 800) {
            enemySpawnIntervalTicks = Math.max(MIN_SPAWN_INTERVAL_TICKS, MAX_SPAWN_INTERVAL_TICKS - 60); // 140 ticks
        } else if (highestScore >= 900 && highestScore < 1000) {
            enemySpawnIntervalTicks = Math.max(MIN_SPAWN_INTERVAL_TICKS, MAX_SPAWN_INTERVAL_TICKS - 80); // 120 ticks
        } else if (highestScore >= 1200 && highestScore < 1350) {
            enemySpawnIntervalTicks = Math.max(MIN_SPAWN_INTERVAL_TICKS, MAX_SPAWN_INTERVAL_TICKS - 100); // 100 ticks
        } else if (highestScore >= 1500) {
            enemySpawnIntervalTicks = Math.max(MIN_SPAWN_INTERVAL_TICKS, MAX_SPAWN_INTERVAL_TICKS - 120); // 80 ticks
        }

        logger.debug("Adjusted enemy spawn rate: {} ticks for highest score: {}", enemySpawnIntervalTicks, highestScore);
    }


    private int getHighestPlayerScore() {
        int highestScore = 0;
        for (Player player : players.values()) {
            highestScore = player.getScore();
            return highestScore;
        }

return  highestScore;
    }

    // Remove defeated enemies
    private void removeDefeatedEnemies() {
        enemies.removeIf(Enemy::getDefeated); // Using a lambda to remove defeated enemies
    }
    private void removeCollectedHerbs()
    {
        herbs.removeIf(Herb :: isCollected);
    }

    private boolean shouldSpawnWave() {
        // Check if enough ticks have passed since the last wave spawn to spawn a new wave
        if (currentTick - lastWaveSpawnTick >= WAVE_SPAWN_INTERVAL_TICKS) {
            lastWaveSpawnTick = currentTick; // Update last wave spawn tick
            waveCounter++;
            return true;
        }
        return false;
    }


    /**
     * Moves all existing enemies in the game.
     */
    private void moveEnemies() {
        for (Enemy enemy : enemies) {
            enemy.move();
        }
    }

    /**
     * Processes a movement command for a player.
     */
    public void processMovementCommand(Player player, String direction) {
        player.move(direction);
        logger.debug("Player {} moved to position: ({}), direction: {}", player.getName(), player.getX(), direction);
    }

    /**
     * Handles the UPDATE request from the client.
     */
    public RenderingPackage handleUpdate(String playerName) {
        Player player = players.get(playerName);

        if (player == null) {
            logger.warn("Player {} not found in game {}", playerName, name);
            return errorPackage("Player not found");
        }

        logger.debug("Server: Sending position update for player {}: x={}, y={}, direction={}",
                player.getName(), player.getX(), player.getY(), player.getDirection());

        return createRenderingPackage();
    }

    /**
     * Runs commands sent by the client and processes them for the player.
     */
    public RenderingPackage runCommands(CommandPackage cp) {
        Player player = players.get(cp.player());

        if (player == null) {
            logger.warn("Player {} not found in game {}", cp.player(), name);
            return errorPackage("Player not found");
        }

        for (JsonObject command : cp.commands()) {
            String type = command.getString("type");
            processCommand(type, command, player);
        }

        return createRenderingPackage();
    }

    private void processCommand(String type, JsonObject command, Player player) {
        switch (type) {
            case "move":
                String direction = command.getString("direction");
                logger.debug("Processing move command for player {} in direction {}", player.getName(), direction);
                player.move(direction);
                break;
            case "jump":
                logger.debug("Processing jump command for player {}", player.getName());
                player.jump();
                break;
            case "setDirection":
                String newDirection = command.getString("direction");
                logger.debug("Setting direction for player {} to {}", player.getName(), newDirection);
                player.setDirection(newDirection);
                break;
            case "updateHealth":
                int health = command.getInteger("health");
                player.setHealth(health);
                break;
            default:
                logger.warn("Unknown command type: {}", type);
                break;
        }
    }

    /**
     * Handles a player joining the game and initializing their state.
     */
    public RenderingPackage joinGame(String playerName) {
        if (players.containsKey(playerName)) {
            logger.warn("Player name {} already taken in game {}", playerName, name);
            return errorPackage("That name's not available");
        } else {
    //        Player p = new Player(playerName, 50, 475, 32, 64, 4);
            Player p = new Player(playerName, 50, 425, 32, 64, 4);
            players.put(playerName, p);
   //         logger.info("Player {} joined the game at position: (0, {})", playerName, 520);
            logger.info("Player {} joined the game at position: (0, {})", playerName, 425);

            ArrayList<JsonObject> renderingCommands = new ArrayList<>();
            renderingCommands.add(new LoadClient("Mario", "Mario", name, playerName).toJson());
            renderingCommands.add(new JsonObject().put("command", "updatePosition").put("x", p.getX()).put("y", p.getY()));
            renderingCommands.add(new JsonObject().put("command", "updateScore").put("score", score));
            return new RenderingPackage(gameMetadata(), renderingCommands);
        }
    }

    private RenderingPackage errorPackage(String message) {
        return new RenderingPackage(gameMetadata(), List.of(
                new JsonObject().put("command", "showError").put("message", message)
        ));
    }

    private RenderingPackage createRenderingPackage() {
        return new RenderingPackage(gameMetadata(), generateRenderingCommands());
    }

    private ArrayList<JsonObject> generateRenderingCommands() {
        ArrayList<JsonObject> renderingCommands = new ArrayList<>();

        for (Player player : players.values()) {
            renderingCommands.add(createPlayerUpdate(player));
        }

        for (int i = 0; i < enemies.size(); i++) {
            renderingCommands.add(createEnemyUpdate(i, enemies.get(i)));
        }
        for (int i = 0; i < herbs.size(); i++) {
            renderingCommands.add(createHerbUpdate(i, herbs.get(i)));
        }
        renderingCommands.add(waveUpdate());

        return renderingCommands;
    }

    private JsonObject createPlayerUpdate(Player player) {
        return new JsonObject()
                .put("command", "updatePosition")
                .put("x", player.getX())
                .put("y", player.getY())
                .put("onGround", player.isOnGround())
                .put("direction", player.getDirection())
                .put("health", player.getHealth())
                .put("score" , player.getScore());
    }


    private JsonObject createEnemyUpdate(int index, Enemy enemy) {
        return new JsonObject()
                .put("command", "updateEnemy")
                .put("index", index)
                .put("x", enemy.getX())
                .put("y", enemy.getY())
                .put("active", enemy.isActive());
    }
    private JsonObject waveUpdate() {
        return new JsonObject()
                .put("command", "waveUpdate")
                .put("wave", waveCounter);
    }

    private JsonObject createHerbUpdate(int index, Herb herb) {
        return new JsonObject()
                .put("command", "updateHerb")
                .put("index", index)
                .put("x", herb.getX())
                .put("y", herb.getY())
                .put("active", herb.isActive());
    }




    /**
     * Returns the name of the game.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns an array of player names currently in the game.
     */
    public String[] getPlayerNames() {
        return players.keySet().toArray(new String[0]);
    }

    /**
     * Returns metadata for the game, including the game name and player list.
     */
    public GameMetadata gameMetadata() {
        return new GameMetadata("Mario", name, getPlayerNames(), true);
    }
}
