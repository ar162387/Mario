package minigames.client.bomberman;

import io.vertx.core.json.JsonObject;
import javafx.animation.AnimationTimer;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.util.Pair;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * The GameController class is responsible for handling user input and updating the game 
 * Contributors: Daniel Gooden - dgooden@myune.edu.au
 *               Lixang Li  - lli32@myune.edu.au  
 */
public class GameController {
    private Board board;
    private final Pane root = new Pane();

    private final Pane floorLayer = new Pane();

    private final Pane charactersLayer = new Pane();

    private final Pane UILayer = new Pane();
    private AnimationTimer gameLoop;
    private final Game game;
    private Scene gameScene;
    CollisionManager collisionManager = CollisionManager.getInstance();
    DifficultyManager difficultyManager = DifficultyManager.getInstance();
    private boolean up, down, left, right;
    private UI UI = new UI();
    private double speed = 1.5; // for cheat code
    private int power = 1; // for cheat code
    private Player player;

    // Debug variables
    private long lastUpdate = 0;
    private long lastFpsUpdate = 0;
    private int frames = 0;
    private double fps = 0;
    private final Label fpsText = new Label();

    // Game tick variables
    private static final long NANOSECONDS_PER_SECOND = 1_000_000_000L;
    private static final double TARGET_FPS = 60.0;
    private static final long NANOSECONDS_PER_FRAME = (long) (NANOSECONDS_PER_SECOND / TARGET_FPS);

    private long lastFrameTime = 0;

    private final Stage stage;

    /**
     * Constructor
     * @param stage //was root before
     * @param game
     */
    public GameController(Stage stage, Game game) {
        this.stage = stage;
        this.game = game;
        initialize();
    }

    /**
     * Initialize the game controller
     */
    private void initialize() {
        // DEBUG STUFF
        fpsText.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");
        fpsText.setLayoutX(10);
        fpsText.setLayoutY(20);
        fpsText.setText("FPS: " + frames);
        root.getChildren().add(fpsText);

        root.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(this::handleKeyPress);
                newScene.setOnKeyReleased(this::handleKeyRelease);
            }
        });

        gameLoop = new AnimationTimer() {

            @Override
            public void handle(long now) {
                if (lastFrameTime == 0) {
                    lastFrameTime = now;
                    return;
                }

                long elapsedNanos = now - lastFrameTime;

                if (elapsedNanos >= NANOSECONDS_PER_FRAME) {
                    update(now);
                    lastFrameTime = now;
                }

                calculateFPS(now);
                fpsText.setVisible(DebugManager.getInstance().isDebugMode());
            }
        };
        final int height = GameConstants.BOARD_NO_OF_ROWS * GameConstants.TILE_SIZE;
        final int width = GameConstants.BOARD_NO_OF_COLUMNS * GameConstants.TILE_SIZE;

        gameScene = new Scene(root, width,height);
    }

    /**
     * Start the game loop
     */
    public void start() {
        difficultyManager.resetDifficultyFactor();
        resetGame();
        stage.setScene(gameScene);
        root.requestFocus();
        gameLoop.start();
    }

    public void resume() {
        stage.setScene(gameScene);
        root.requestFocus();
        gameLoop.start();
    }

    /**
     * Reset the game state
     */
    private void resetGame() {
        root.getChildren().clear();

        AddFloorCharactersUIPanesTo(root); //Edit: To fix enemies under tiles bug - Li

        // Create level using Difficulty Manager /new dependency 18/08
        int difficulty = difficultyManager.getDifficultyFactor();

        // Multiplayer testing TODO
        final boolean singlePlayer = true;
        LevelFactory.MultiplayerMaps map = LevelFactory.MultiplayerMaps.SPIRAL;

        Level newLevel = (singlePlayer) ? difficultyManager.getLevel(difficulty) : LevelFactory.getInstance().getLevelFromAscii(map.getAscii());

        // Using level class

        //Level newLevel = new Level(10, 13, 8, true, true, 25); // Changed to odd because Symmetry - Li

        // Get player
        Point playerLocation = newLevel.getPlayerLocation();
        player = new Player((int) playerLocation.getX(), (int) playerLocation.getY(), 3, (double) speed, (int) power);

        // Get enemies
        List<Enemy> enemies = newLevel.getEnemies();
        // Make board
        this.board = new Board(newLevel,player,enemies);
        BombermanGraphics.getInstance().renderMap(root, board.getLevel());

        // GRAPHICS


        // Enemies
        for (Enemy enemy : enemies) {
            root.getChildren().add(enemy.getGraphic());
        }

        // Player
        root.getChildren().add(board.getPlayer().getGraphic());
        root.getChildren().add(fpsText);

        // get UI
        VBox UIMenu = UI.getUIMenu();
        root.getChildren().add(UIMenu);
    }

    /**
     * Create separate layers for the floor tiles and entities (players/enemies):
     * @param root game root pane
     * @return boolean success or failure
     */
    private boolean AddFloorCharactersUIPanesTo(Pane root) {

        return root.getChildren().addAll(floorLayer, charactersLayer, UILayer);
    }

    /**
     * Handle key press events
     * @param event
     */
    private void handleKeyPress(KeyEvent event) {
        if (game.getState() == GameState.PLAYING) {
            KeyCode code = event.getCode();
            switch (code) {
                case UP -> up = true;
                case DOWN -> down = true;
                case LEFT -> left = true;
                case RIGHT -> right = true;
                case Z -> board.addBomb(board.getPlayer().placeBomb());
                case ESCAPE -> {gameLoop.stop();
                    game.setPaused(1);
                    game.setState(GameState.PAUSE); 
                }
                default -> {}
            }
        }
    }

    /**
     * Handle key release events
     * @param event
     */
    private void handleKeyRelease(KeyEvent event) {
        if (game.getState() == GameState.PLAYING) {
            KeyCode code = event.getCode();
            switch (code) {
                case UP -> up = false;
                case DOWN -> down = false;
                case LEFT -> left = false;
                case RIGHT -> right = false;
                default -> {}
            }
        }
    }

    /**
     * Update the game state
     * This method is called every tick of the game loop
     */
    private void update(long now) {
        Player player = board.getPlayer();

        int dx = 0;
        int dy = 0;

        if (up) dy -= 1;
        if (down) dy += 1;
        if (left) dx -= 1;
        if (right) dx += 1;

        boolean isWalking = false;

        // Try to move X first
        if (dx != 0) {
            player.move(dx, 0);
            if (collisionManager.checkTileCollision(player.getBounds(), board.getLevel().getTileMap())) {
                player.move(-dx, 0); // Revert the movement
            } else {
                isWalking = true;
                if (dx > 0) {
                    player.setDirection(Direction.RIGHT);
                } else if (dx < 0) {
                    player.setDirection(Direction.LEFT);
                }
            }
        }

        // Then try to move Y
        if (dy != 0) {
            player.move(0, dy);
            if (collisionManager.checkTileCollision(player.getBounds(), board.getLevel().getTileMap())) {
                player.move(0, -dy); // Revert the movement
            } else {
                isWalking = true;
                if (dy > 0) {
                    player.setDirection(Direction.DOWN);
                } else if (dy < 0) {
                    player.setDirection(Direction.UP);
                }
            }
        }

        player.setIsWalking(isWalking);
        player.update(now);

        // Update Bombs
        Iterator<Bomb> iteratorBomb = board.getBombs().iterator();
        while (iteratorBomb.hasNext()) {
            Bomb bomb = iteratorBomb.next();

            if (!bomb.hasExploded()) {
                bomb.updateCountdown(now);
            }

            if (!root.getChildren().contains(bomb.getGraphic())) {
                root.getChildren().add(bomb.getGraphic());
            }

            if (bomb.hasExploded()) {
                Explosion explosion = new Explosion(bomb.getX(), bomb.getY(), bomb.getPower(), board.getLevel().getTileMap());
                board.addExplosion(explosion);
                iteratorBomb.remove();
                root.getChildren().remove(bomb.getGraphic());
            }
        }

        // Update Explosions
        updateExplosions(now,player); // EDIT: Changed to method for testing

        // Re-add player every frame to ensure the player is the topmost graphic
        // TODO: create some multi layering system with multiple panes
        if (!root.getChildren().contains(player.getGraphic())) {
            root.getChildren().add(player.getGraphic());
        } else {
            root.getChildren().remove(player.getGraphic());
            root.getChildren().add(player.getGraphic());
        }

        // WIN CONDITION

        if (board.getEnemies().isEmpty()) {

            //SOUND
            Sound.getInstance().playSFX(Sound.Type.WIN);

            gameLoop.stop();
            //DISPLAY SCORE

            // Next level hahaha
            difficultyManager.levelUp(1);
            resetGame();
            UI.setLevel(UI.getLevel() + 1); // update level on UI
            gameLoop.start();
        };

        // Update enemies
        Iterator<Enemy> iteratorEnemy = board.getEnemies().iterator();
        while (iteratorEnemy.hasNext()) {
            Enemy enemy = iteratorEnemy.next();

            //enemy movement
            Pair<Integer,Integer> nextMove = enemy.getMovementStrategy().move(enemy,board);
            Integer x = nextMove.getKey();
            Integer y = nextMove.getValue();
            enemy.move(x,y);


            enemy.update(now);
            if (enemy.getLives() <= 0) {
                enemy.handleDeath(() -> root.getChildren().remove(enemy.getGraphic()));
                iteratorEnemy.remove();
                UI.setScore(1); // increase score by 1
                //TODO check enemies left and if none left win game
            }

            if (CollisionManager.getInstance().checkCollision(enemy.getGraphic(), player.getBounds())) {
                player.hit(now);
            }
        }

        // Update player
        if (player.getLives() <= 0) {
            DebugManager.getInstance().log("GAME OVER");
            player.handleDeath(() -> root.getChildren().remove(player.getGraphic()));

            //SOUND
            Sound.getInstance().playSFX(Sound.Type.PLAYER_DEATH);
            //Stopping time to fix playing the PLAYER DEATH SOUND A BILLION TIMES
            gameLoop.stop();
            //TODO: trigger game over logic here

            // Send score to leaderboard
            int score = getCurrentScore();
            String name = "TestUser"; // TODO: Integrate the name from the Client data
            submitScore(name, score, "bomberman");

            game.setState(GameState.GAME_OVER);
        }

        // Update UI
        if (player.getLives() != UI.getLives()){
            UI.setLives(player.getLives());
        }

        // DEBUG: Bounds for collision detection
        collisionManager.clearBounds(root);
        root.getChildren().remove(fpsText);
        if(DebugManager.getInstance().isDebugMode()) {
            collisionManager.drawBounds(root, player.getBounds());

            for (Enemy enemy : board.getEnemies()) {
                collisionManager.drawBounds(root, enemy.getBounds()); // Update Li
            }

            for (Explosion explosion : board.getExplosions()) {
                for (Node explosionNode : explosion.getExplosionNodes()) {
                    collisionManager.drawBounds(root, explosionNode);
                }
            }

            Map<Point, Tile> tiles = board.getLevel().getTileMap();
            for (Tile tile : tiles.values()) {
                if (!tile.passable()) {
                    collisionManager.drawBounds(root, tile.graphic());
                }
            }
            root.getChildren().add(fpsText);
        }
    }

    /**
     * Package private update explosions module
     * @param now
     */
    void updateExplosions(Long now, Player player) {
        Iterator<Explosion> iteratorExp = board.getExplosions().iterator();
        while (iteratorExp.hasNext()) {
            Explosion exp = iteratorExp.next();
            exp.update(now);

            for (Node explosionNode : exp.getExplosionNodes()) {
                if (explosionNode instanceof ImageView) { // Ensure it's the right type
                    ImageView imageView = (ImageView) explosionNode;
                    int nodeX = (int) (imageView.getX() / GameConstants.TILE_SIZE);
                    int nodeY = (int) (imageView.getY() / GameConstants.TILE_SIZE);
                    Point explosionPoint = new Point(nodeX, nodeY);

                    // Check the tile at this position
                    Tile tile = board.getLevel().getTileMap().get(explosionPoint);

                    // Check collisions
                    if (tile != null && tile.type() == TileType.DESTRUCTIBLE_WALL) {
                        // Handle the impact and change the tile to empty
                        board.handleExplosionImpact(explosionPoint, root);
                    }

                    // Player collisions
                    if(CollisionManager.getInstance().checkCollision(player.getBounds(), explosionNode)) {
                        player.hit(now);
                    }

                    // Enemy collisions
                    for (Enemy enemy : board.getEnemies()) {
                        if (CollisionManager.getInstance().checkCollision(enemy.getBounds(), explosionNode)) { // updated bounds - Li
                            enemy.hit(now);
                        }
                    }

                    // Add to node if it doesn't already have it
                    if (!root.getChildren().contains(explosionNode)) {
                        root.getChildren().add(explosionNode);
                    }
                }
            }

            if (exp.isFinished()) {
                for (Node node : exp.getExplosionNodes()) {
                    root.getChildren().remove(node); // Remove the explosion nodes
                }
                iteratorExp.remove(); // Remove the explosion from the active list
            }
        }
    }

    /**
     * Calculate the frames per second
     * @param now
     */
    private void calculateFPS(long now) {
        if (lastUpdate > 0) {
            double elapsed = (now - lastUpdate) / 1_000_000_000.0;
            frames++;
            if (now - lastFpsUpdate >= 1_000_000_000) { // Update FPS every second
                fps = frames / ((now - lastFpsUpdate) / 1_000_000_000.0);
                frames = 0;
                lastFpsUpdate = now;
                fpsText.setText("FPS: " + String.format("%.2f", fps));
            }
        } else {
            lastUpdate = now;
            lastFpsUpdate = now; // Initialize lastFpsUpdate
        }
        lastUpdate = now; // Always update lastUpdate
    }

    public void resetUI(){
        this.UI = new UI();
    }

    /**
     * added this method to create a cheat code for super speed
     * @param speed
     */
    public void setSpeed(double speed){
        this.speed = speed;
    }
 
    /**
     * this method sets the players power, added as a cheat code
     * @param power
     */
    public void setPower(int power) {
        this.power = power;
    }

    public void submitScore(String username, int score, String gameType) {
        JsonObject scoreData = new JsonObject()
                .put("name", username)
                .put("score", score)
                .put("gameType", gameType);

        // Use DataSingleton to send the POST request
        DataSingleton.getInstance().postData("/api/leaderboard/scores", scoreData, response -> {
            DebugManager.getInstance().log("Score submitted: " + response);
        });
    }

    public int getCurrentScore() {
        return UI.getScore();
    }

    public Player getPlayer() {
        return player;
    }
}
