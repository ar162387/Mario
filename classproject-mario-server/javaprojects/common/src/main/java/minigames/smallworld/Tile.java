package minigames.smallworld;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import javax.imageio.ImageIO;


/**
 * Holds information about each Tile entitity
 * 
 * A tile is just a square of pixels that can be rendered on the screen.
 * 
* atm is just a coloured square, in the future will be a sprite
 * 
 */
public enum Tile {


    /**
     * calls enum constructor to define named types of tiles
     * 
     * These can be referenced as e.g. Tile.DIRT
     * 
     * N.B. the syntax "new Tile(...)" won't work
     * 
     */
    GRASS("grass.png", 'g', true, -1),
    DIRT("dirt.png", 'm', true, -1),
    SKY("sky.png", 's', false, -1),
    LAVA("lava.png", 'l', false, -1),
    CPU("processor.png", '#', false, 0),
    TREE("tree.png", 'T', false, -1),
    LEAVES("leaves.png", 'L', false, -1),
    CLOUD("cloud.png", 'C', false, -1),
    CAVE("cave.png", 'u', false, -1),
    BEDROCK("bedrock.png",'b',true,-1),
    WATER("water.png", 'w', false, -1),
    DEFAULT("box.png", '*', true, -1);

    char ascii;
    Color colour;
    BufferedImage sprite;
    public static int SIZE = 32;
    boolean isCollidable;
    int specialItemID;
    String typeName;

    private Tile(String spritePath, char ascii, boolean isCollidable, int specialItemID) {
        this.sprite = loadSprite(spritePath);
        this.ascii = ascii;
        this.isCollidable = isCollidable;
        this.colour = Color.BLACK;
        this.specialItemID = specialItemID;
        this.typeName = spritePath.replace(".png", "");

    }
    
    /**
     * Loads a sprite from the resources/smallworld/images directory
     * 
     * 
     * @param path
     * @return
     */
    private BufferedImage loadSprite(String path) {
        String filePath = "smallworld/images/" + path;
        
        try {
            URL imageURL = getClass().getClassLoader().getResource(filePath);

            if(imageURL != null) {
                return ImageIO.read(imageURL);
            } else {
                throw new IOException("Image not found: " + filePath);
            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }
    
    public boolean isCollidable() {
        return this.isCollidable;
    }

    public int getSpecialItemID() {
        return this.specialItemID;
    }

    public char getAscii() {
        return this.ascii;
    }

    public String getTypeName() {
        return this.typeName;
    }

    public static Tile fromChar(char ascii) {
        for (Tile tile : Tile.values()) {
            if (tile.getAscii() == ascii) {
                return tile;
            }
        }
        throw new IllegalArgumentException("No tile with ascii char: " + ascii);
    }

    public static Tile fromTypeName(String type) {
        for (Tile tile : Tile.values()) {
            if (Objects.equals(tile.getTypeName(), type)) {
                return tile;
            }
        }
        throw new IllegalArgumentException("No tile with type name: " + type);
    }
    
}
