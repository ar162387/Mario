package minigames.client.bomberman;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

/**
 * Represents a sprite sheet for managing and creating tile images.
 * This class handles the loading and manipulation of a sprite sheet image,
 * creation of individual tile views from the sheet.
 *
 * Original Bomb Party sprite sheet by Matt Hackett of Lost Decade Games, expanded by Cem Kalyoncu and /usr/share.
 *
 * Contributors: Lixang Li lli32@myune.edu.au
 *
 * Example usage:
 * <pre>
 * // Create a new SpriteSheet
 * SpriteSheet sheet = new SpriteSheet("path/to/spritesheet.png", 32, 32, 0, 0, 0, 8);
 *
 * // Create a tile ImageView
 * ImageView tileView = new ImageView();
 * ImageView configuredTile = sheet.createTile(10, tileView);
 *
 * // Get the entire sprite sheet ImageView
 * ImageView fullSheet = sheet.getImageView();
 * </pre>
 *
 * <p>The sprite sheet is expected to be organized in a grid format, with optional
 * header space at the top. The class accounts for sprite dimensions, padding,
 * and the number of sprites per row to accurately extract individual tiles.</p>
 */
public class SpriteSheet {
    private ImageView spritesheet;
    private int spriteWidth;
    private int spriteHeight;
    private int headerHeight;
    private int horizontalPadding;
    private int verticalPadding;
    private int spritesInOneRow;

    /**
     * Constructor for SpriteSheet class
     * @param imagePath The file path to the spritesheet image
     * @param spriteWidth The width of each individual sprite in pixels
     * @param spriteHeight The height of each individual sprite in pixels
     * @param headerHeight The height of any header/filler in the spritesheet image in pixels
     * @param horizontalPadding The horizontal padding between sprites in pixels
     * @param verticalPadding The vertical padding between sprite rows in pixels
     * @param spritesInOneRow The number of sprites in one row of the spritesheet

     */
    public SpriteSheet(String imagePath, int spriteWidth, int spriteHeight, int headerHeight, int horizontalPadding, int verticalPadding, int spritesInOneRow) {
        // Load image
        this.spritesheet = setImageView(imagePath);
        this.spriteWidth = spriteWidth;
        this.spriteHeight = spriteHeight;
        this.headerHeight = headerHeight;
        this.horizontalPadding = horizontalPadding;
        this.verticalPadding = verticalPadding;
        this.spritesInOneRow = spritesInOneRow;

    }

    /**
     * Creates an ImageView for a specific tile from the sprite sheet.
     *
     * @param tileNumber The index of the tile in the sprite sheet.
     * @param tileView   The ImageView to be configured for the tile.
     * @return The configured ImageView for the specified tile.
     */
    public ImageView createTile(int tileNumber,ImageView tileView) {
//        ImageView tileView = spritesheet; Updated to include dependency injection for testing purposes
        // Calculate the position of the tile in the sprite sheet, considering the header
        int spriteRow = (tileNumber / spritesInOneRow); // Adjust based on your sprite sheet layout
        int spriteColumn = (tileNumber % spritesInOneRow); //Loop to next row
        int offsetX = spriteColumn * spriteHeight;
        int offsetY = headerHeight + (spriteRow * spriteHeight); // Skip header height

        // Set the viewport to show only the specific tile
        tileView.setViewport(new Rectangle2D(offsetX, offsetY, spriteHeight, spriteHeight));
        return tileView;
    }

    /**
     * Creates and returns an ImageView object from the specified image location.
     *
     * @param location The file path of the image.
     * @return An ImageView object representing the loaded image, or null if loading fails.
     */
    ImageView setImageView(String location) {
        try {
            DebugManager.getInstance().log("Loaded spritesheet: " + location); // Use debugger

            return new ImageView(location);
        } catch (Exception e) {
            DebugManager.getInstance().logError("Could not load SpriteSheet: " + location);
            return null;
        }
    }
    /**
     * Retrieves the ImageView representing the entire sprite sheet.
     *
     * @return The ImageView of the sprite sheet.
     */
    ImageView getImageView() {
        return new ImageView(this.spritesheet.getImage());
    }
}
