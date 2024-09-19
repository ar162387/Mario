package minigames.client.bomberman;

import javafx.scene.image.ImageView;

/**
 * Represents a tile in the Bomberman game as a record because it already has setters and getters included.
 * A tile can have different properties such as being destructible, interactible, and passable.
 * It also contains information about its sprite, and size
 *
 * Contributors: Lixang Li lli32@myune.edu.au
 */
public record Tile(
        TileType type,
        boolean destructible,
        boolean interactible,
        boolean passable,
        ImageView graphic
){


    /**
     * Provides a string prettification of the tile, including all its properties.
     *
     * @return a string representation of the tile.
     */
    @Override
    public String toString() {
        return "Tile{" +
                "type=" + type +
                ", destructible=" + destructible +
                ", interactible=" + interactible +
                ", passable=" + passable +
                '}';
    }

    public Tile copy() {
        ImageView graphicCopy = BombermanGraphics.getInstance().getTileSprite(type);
        return new Tile(type, destructible, interactible, passable, graphicCopy);
    }
}