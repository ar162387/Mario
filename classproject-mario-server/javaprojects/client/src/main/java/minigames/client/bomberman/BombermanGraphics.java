package minigames.client.bomberman;

import javafx.geometry.Rectangle2D;
import javafx.scene.CacheHint;
import javafx.scene.image.*;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;

import java.awt.Point;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 *
 * Bomberman graphics singleton to handle the rendering of the bomberman game.
 *
 *
 * Original Bomb Party sprite sheet by Matt Hackett of Lost Decade Games, expanded by Cem Kalyoncu and /usr/share.
 *
 * Contributors: Daniel Gooden
 *               Lixang Li
 */
public class BombermanGraphics {
    private static final String IMAGE_SOURCE = "images/bomberman/bombpartyv4.png";
    private static BombermanGraphics instance;
    private final SpriteSheet spriteSheet;

    // Cache to store ImageViews for different TileTypes to avoid recreating them
    private final Map<TileType, ImageView> spriteCache = new HashMap<>();
    private static final Map<TileType, Integer> tileNumbers = new LinkedHashMap<>(); // Preserve insertion order
    private static final Set<TileType> mirroredTiles = new HashSet<>(); // List of tiles to trigger a mirror

    // Constants for sprite locations
    static {
        tileNumbers.put(TileType.EMPTY, 1);
        tileNumbers.put(TileType.INDESTRUCTIBLE_WALL, 70);
        tileNumbers.put(TileType.DESTRUCTIBLE_WALL, 9);
        tileNumbers.put(TileType.PLAYER_NORTH, 60);
        tileNumbers.put(TileType.PLAYER_SOUTH, 61);
        tileNumbers.put(TileType.PLAYER_WALK_SOUTH1, 62);
        tileNumbers.put(TileType.PLAYER_WALK_SOUTH2, 63);
        tileNumbers.put(TileType.PLAYER_EAST, 64);
        tileNumbers.put(TileType.PLAYER_WEST, 64);
        tileNumbers.put(TileType.PLAYER_WALK_EAST1, 65);
        tileNumbers.put(TileType.PLAYER_WALK_EAST2, 66);
        tileNumbers.put(TileType.PLAYER_WALK_EAST3, 67);
        tileNumbers.put(TileType.PLAYER_WALK_WEST1, 65);
        tileNumbers.put(TileType.PLAYER_WALK_WEST2, 66);
        tileNumbers.put(TileType.PLAYER_WALK_WEST3, 67);
        tileNumbers.put(TileType.PLAYER_WALK_NORTH1, 68);
        tileNumbers.put(TileType.PLAYER_WALK_NORTH2, 69);
        tileNumbers.put(TileType.ENEMY, 16);
        tileNumbers.put(TileType.ENEMY_ARMOUR, 31);
        tileNumbers.put(TileType.ENEMY_WIZARD,46);
        tileNumbers.put(TileType.BOMB1, 79);
        tileNumbers.put(TileType.BOMB2, 80);
        tileNumbers.put(TileType.BOMB3, 81);
        tileNumbers.put(TileType.BOMB4, 82);
        tileNumbers.put(TileType.BOMB5, 83);
        tileNumbers.put(TileType.BOMB6, 84);
        tileNumbers.put(TileType.EXPLODE_CENTRE, 77);
        tileNumbers.put(TileType.EXPLODE_HORIZONTAL, 76);
        tileNumbers.put(TileType.EXPLODE_VERTICAL, 29);
        tileNumbers.put(TileType.EXPLODE_NORTH, 14);
        tileNumbers.put(TileType.EXPLODE_EAST, 78);
        tileNumbers.put(TileType.EXPLODE_SOUTH, 44);
        tileNumbers.put(TileType.EXPLODE_WEST, 75);
        tileNumbers.put(TileType.EXPLODE_FADE1, 89);
        tileNumbers.put(TileType.EXPLODE_FADE2, 74);
        tileNumbers.put(TileType.EXPLODE_FADE3, 59);

        mirroredTiles.add(TileType.PLAYER_WEST);
        mirroredTiles.add(TileType.PLAYER_WALK_WEST1);
        mirroredTiles.add(TileType.PLAYER_WALK_WEST2);
        mirroredTiles.add(TileType.PLAYER_WALK_WEST3);
    }

    // Private constructor to prevent instantiation
    private BombermanGraphics() {
        spriteSheet = new SpriteSheet(IMAGE_SOURCE, 16, 16, 208, 0, 0,15);
    }

    // Method to get the singleton instance
    public static BombermanGraphics getInstance() {
        if (instance == null) {
            instance = new BombermanGraphics();
        }
        return instance;
    }


    /**
     * Renders the entire map on the given root pane.
     *
     * @param root  The Pane where the map should be rendered.
     * @param level The Level object containing the map data.
     */
    public void renderMap(Pane root, Level level) {
        for (Map.Entry<Point, Tile> entry : level.getTileMap().entrySet()) {
            Point point = entry.getKey();
            Tile tile = entry.getValue();

            // Get the sprite for the tile
            ImageView tileView = tile.graphic();
            tileView.setX(point.x * GameConstants.TILE_SIZE);
            tileView.setY(point.y * GameConstants.TILE_SIZE);

            // Add to the root pane
            root.getChildren().add(tileView);
        }
    }


    /**
     * Converts a TileType into an ImageView and caches it
     *
     * @param tileType
     * @return ImageView
     */
    public ImageView getTileSprite(TileType tileType) {
        try {
            if (!spriteCache.containsKey(tileType)) {
                ImageView sprite = new ImageView();
                Integer tileNumber = tileNumbers.get(tileType);

                if (tileNumber != null) {
                    sprite = spriteSheet.createTile(tileNumber,spriteSheet.getImageView());
                } else {
                    DebugManager.getInstance().logError("Tile type: " + tileType + " not found");
                }

                // Convert ImageView to Image
                Image tileImage = imageViewToImage(sprite);

                // Resize
                Image scaledSprite = scaleImage(tileImage, GameConstants.TILE_SIZE, GameConstants.TILE_SIZE);

                // Mirror if reused
                if (mirroredTiles.contains(tileType)) {
                    scaledSprite = mirrorImageHorizontally(scaledSprite);
                }

                ImageView imageView = new ImageView(scaledSprite);

                imageView.setSmooth(false); // Disable smooth scaling for nearest-neighbor effect
                imageView.setCache(true);
                imageView.setCacheHint(CacheHint.SPEED); // Add acceleration

                spriteCache.put(tileType, imageView);
            }

            // Return a clone of the cached ImageView to avoid modifying the original
            ImageView original = spriteCache.get(tileType);
            ImageView clone = new ImageView(original.getImage());
            clone.setSmooth(original.isSmooth());
            clone.setCache(original.isCache());

            return clone;
        } catch (Exception e) {
            // Log the exception (you can use a logging framework or print the stack trace)
            System.err.println("Error while getting tile sprite for " + tileType + ": " + e.getMessage());
            e.printStackTrace(); // Optional: print the stack trace
            return null; // Return null or handle the error as needed
        }
    }

    // Helper methods (scaleImage, imageViewToImage) remain the same...

    private static Image scaleImage(Image image, int newWidth, int newHeight) {
        WritableImage scaledImage = new WritableImage(newWidth, newHeight);
        PixelReader pixelReader = image.getPixelReader();
        PixelWriter pixelWriter = scaledImage.getPixelWriter();

        int originalWidth = (int) image.getWidth();
        int originalHeight = (int) image.getHeight();

        for (int y = 0; y < newHeight; y++) {
            for (int x = 0; x < newWidth; x++) {
                int originalX = x * originalWidth / newWidth;
                int originalY = y * originalHeight / newHeight;
                pixelWriter.setArgb(x, y, pixelReader.getArgb(originalX, originalY));
            }
        }

        return scaledImage;
    }

    // Flips image horizontally (along Y-axis)
    private static Image mirrorImageHorizontally(Image image) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        WritableImage mirroredImage = new WritableImage(width, height);
        PixelReader pixelReader = image.getPixelReader();
        PixelWriter pixelWriter = mirroredImage.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int mirroredX = width - x - 1; // Calculate the mirrored x position
                pixelWriter.setArgb(mirroredX, y, pixelReader.getArgb(x, y));
            }
        }

        return mirroredImage;
    }

    private static Image imageViewToImage(ImageView imageView) {
        Rectangle2D viewport = imageView.getViewport();
        if (viewport == null) {
            return imageView.getImage();
        }

        PixelReader pixelReader = imageView.getImage().getPixelReader();
        int width = (int) viewport.getWidth();
        int height = (int) viewport.getHeight();
        int viewportX = (int) viewport.getMinX();
        int viewportY = (int) viewport.getMinY();

        WritableImage writableImage = new WritableImage(width, height);
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Correctly read the color from the viewport region
                pixelWriter.setColor(x, y, pixelReader.getColor(viewportX + x, viewportY + y));
            }
        }

        return writableImage;
    }

    public ArrayList<ImageView> getBombFrames() {
        return IntStream.rangeClosed(1, 6)
                .mapToObj(i -> getInstance().getTileSprite(TileType.valueOf("BOMB" + i)))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<ImageView> getExplosionFrames() {
        return EnumSet.allOf(TileType.class).stream()
                .filter(type -> type.name().contains("EXPLODE"))
                .map(this::getTileSprite)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Vertically mirrors the given ImageView.
     *
     * @param imageView the ImageView to be mirrored
     */
    private static void mirrorVertically(ImageView imageView) {
        imageView.setScaleY(-1);
    }

}
