package minigames.client.bomberman;

import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;
import java.awt.Point;

/**
 * The Board class represents the game board. It contains the player, enemies,
 * bombs, and map.
 * Contributors: Daniel Gooden - dgooden@myune.edu.au
 *
 */

public class Board {
    private final Level level;
    private final Player player;
    private final List<Enemy> enemies;
    private final List<Bomb> bombs = new ArrayList<>();
    private final List<Explosion> explosions = new ArrayList<>();


    /**
     * Constructor
     *
     * @param level   - pass a level in to make a new board.
     * @param player
     * @param enemies
     */

    public Board(Level level, Player player, List<Enemy> enemies) { // Added parameters and moved constructors to constructor call method - Li
        this.level = level;
        this.player = player;
        this.enemies = enemies;
    }

    public void handleExplosionImpact(Point position, Pane root) {
        Tile tile = level.getTileMap().get(position);

        if (tile != null && tile.type() == TileType.DESTRUCTIBLE_WALL) {
            changeTileToEmpty(position, root);
        }
    }

    private void changeTileToEmpty(Point position, Pane root) {
        Tile oldTile = level.getTileMap().get(position);
        if (oldTile != null) {
            root.getChildren().remove(oldTile.graphic());
            level.getTileMap().remove(position);

            // Convert to pixels
            DebugManager.getInstance().log("Placing tile at: " + position);
            position.x *= GameConstants.TILE_SIZE;
            position.y *= GameConstants.TILE_SIZE;
            DebugManager.getInstance().log("Pixel location: " + position);

            Tile emptyTile = new Tile(TileType.EMPTY, false, false, true, BombermanGraphics.getInstance().getTileSprite(TileType.EMPTY));
            level.getTileMap().put(position, emptyTile);
            emptyTile.graphic().setX(position.x);
            emptyTile.graphic().setY(position.y);
            root.getChildren().add(emptyTile.graphic());
        }
    }

    /**
     * Get the player
     * @return player
     */

    public Player getPlayer() {
        return player;
    }

    /**
     * Get the enemies
     * @return enemies
     */

    public List<Enemy> getEnemies() {
        return enemies;
    }

    /**
     * Add an enemy to the board
     * @param enemy
     */

    public void addEnemy(Enemy enemy) {
        enemies.add(enemy);
    }

    /**
     * Remove an enemy from the board
     * @param enemy
     */

    public void removeEnemy(Enemy enemy) {
        enemies.remove(enemy);
    }

    /**
     * Get the width of the board
     * @return width
     */

    public int getWidth() {
        return level.getWIDTH();
    }

    /**
     * Get the height of the board
     * @return height
     */

    public int getHeight() {
        return level.getHEIGHT();
    }

    /**
     * Get the bombs on the board
     * @return bombs
     */

    public List<Bomb> getBombs() {
        return bombs;
    }

    /**
     * Add a bomb to the board
     * @param bomb
     */

    public void addBomb(Bomb bomb) {
        bombs.add(bomb);
    }

    /**
     * Remove a bomb from the board
     * @param bomb
     */

    public void removeBomb(Bomb bomb) {
        bombs.remove(bomb);
    }

    public List<Explosion> getExplosions() {
        return this.explosions;
    }

    public void addExplosion (Explosion explosion) {
        this.explosions.add(explosion);
    }

    public void removeExplosion (Explosion explosion) {
        this.explosions.remove(explosion);
    }

    public Level getLevel() {
        return level;
    }
}
