package minigames.client.bomberman;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

import javax.swing.text.html.Option;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

/**
 * Explosion frame guid
 * 0 - Centre
 * 1 - Horizontal
 * 2 - Vertical
 * 3 - North cap
 * 4 - East cap
 * 5 - South cap
 * 6 - West cap
 * 7 - Fade out 1
 * 8 - Fade out 2
 * 9 - Fade out 3
 */

public class Explosion {
    private final double x;
    private final double y;
    private final int power;
    private final ArrayList<ImageView> explosionFrames;
    private final ArrayList<ImageView> explosionNodes = new ArrayList<>(); // ImageView list to hold nodes
    private int frameIndex = 0;
    private long lastFrameTime = 0;
    private boolean finished = false;
    private final Map<Point, Tile> tileMap;

    /**
     * Constructor
     * @param x
     * @param y
     * @param tileMap
     */
    public Explosion(double x, double y, int power, Map<Point, Tile> tileMap) {
        this.x = x;
        this.y = y;
        this.power = power;
        this.tileMap = tileMap;
        this.explosionFrames = BombermanGraphics.getInstance().getExplosionFrames();

        buildExplosion();
        updateGraphic(); // Initialize with the first frame
    }

    /**
     * Build the explosion nodes based on available spaces on the board
     */
    private void buildExplosion() {
        // Centre explosion
        addExplosionNode(x, y, 0);

        // Directions: Left, Right, Up, Down
        buildDirectionalExplosion(-1, 0); // Left
        buildDirectionalExplosion(1, 0); // Right
        buildDirectionalExplosion(0, -1); // Up
        buildDirectionalExplosion(0, 1); // Down
    }

    /**
     * Builds the explosion in a given direction until it hits an impassable tile or the edge of the board
     * @param dx
     * @param dy
     */
    private void buildDirectionalExplosion(int dx, int dy) {
        int distance = 1;
        boolean canExpand = true;

        while (canExpand && distance <= power) {
            double newX = x + (dx * distance);
            double newY = y + (dy * distance);

            Point newPoint = new Point((int) newX, (int) newY);
            Tile tile = tileMap.get(newPoint);

            if (tile == null || tile.type() == TileType.EMPTY) {
                // Continue if the space is empty
                boolean isCap = (distance == power); // It's a cap if we've reached the max strength
                int frameIndex = getDirectionalFrameIndex(dx, dy, distance, isCap);
                addExplosionNode(newX, newY, frameIndex);
            } else if (tile.type() == TileType.INDESTRUCTIBLE_WALL) {
                // Stop before an indestructible wall
                canExpand = false;
            } else if (tile.type() == TileType.DESTRUCTIBLE_WALL) {
                // End on a destructible wall
                int frameIndex = getDirectionalFrameIndex(dx, dy, distance, true);
                addExplosionNode(newX, newY, frameIndex);
                canExpand = false;
            }

            distance++;
        }
    }

    /**
     * Adds an explosion node at the specified location with the appropriate frame
     * @param x
     * @param y
     * @param frameIndex
     */
    private void addExplosionNode(double x, double y, int frameIndex) {
        //EDIT Li use optionals to encompass nullable for testing
        Optional<ImageView> explosionFrame = Optional.ofNullable(explosionFrames.get(frameIndex));
        Optional<Image> image = explosionFrame.map(ImageView::getImage);

        //Make a 48 x 48 pixel white fallback image
        int tileSize = GameConstants.TILE_SIZE;
        Image fallbackImage = new WritableImage(tileSize, tileSize);
        ImageView explosionNode = new ImageView(image.orElse(fallbackImage));
        // EDIT COMPLETE

        explosionNode.setX(x * GameConstants.TILE_SIZE);
        explosionNode.setY(y * GameConstants.TILE_SIZE);
        explosionNodes.add(explosionNode);
    }

    /**
     * Determines the correct frame index based on direction and distance
     * @param dx
     * @param dy
     * @param distance
     * @param isCap Determines if it's a cap (i.e., end of the explosion due to destructible wall)
     * @return frameIndex
     */
    private int getDirectionalFrameIndex(int dx, int dy, int distance, boolean isCap) {
        if (dy == -1) return isCap ? 3 : 2; // North or Vertical
        if (dx == 1) return isCap ? 4 : 1; // East or Horizontal
        if (dy == 1) return isCap ? 5 : 2; // South or Vertical
        if (dx == -1) return isCap ? 6 : 1; // West or Horizontal

        return 0; // Centre
    }

    public ArrayList<ImageView> getExplosionNodes() {
        return explosionNodes;
    }

    /**
     * Update the explosion animation
     */
    public void update(long now) {
        if (lastFrameTime == 0) {
            lastFrameTime = now;
        }

        long elapsedNanos = now - lastFrameTime;

        if (frameIndex == 0 && elapsedNanos >= 300_000_000) { // 300 milliseconds
            // Move to the next frame (start fading)
            frameIndex++;
            lastFrameTime = now;
            updateGraphic();
        } else if (frameIndex > 0 && elapsedNanos >= 100_000_000) { // 100 milliseconds per frame after the first
            lastFrameTime = now;

            if (frameIndex < 4) { // We have 4 frames to show: 1 for the explosion, 3 for the fades
                updateGraphic();
                frameIndex++;
            } else {
                finishExplosion();
            }
        }
    }

    /**
     * Update the explosion graphic based on the current frame index
     */
    private void updateGraphic() {
        if (!finished) {
            if (frameIndex > 0 && frameIndex < 4) {
                // Only apply the fade to the origin (center of the explosion)
                ImageView originNode = explosionNodes.get(0);  // Assuming the first node in explosionNodes is the center
                originNode.setImage(explosionFrames.get(7 + frameIndex - 1).getImage()); // Show fades

                // Clear out the other nodes
                for (int i = 1; i < explosionNodes.size(); i++) {
                    explosionNodes.get(i).setImage(null); // Clear other nodes
                }
            }
        }
    }


    /**
     * Mark the explosion as finished
     */
    private void finishExplosion() {
        finished = true;
        // TODO: Trigger post-explosion effects, if any
    }

    public boolean isFinished() {
        return finished;
    }
}
