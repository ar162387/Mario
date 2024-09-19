package minigames.client.bomberman;

import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.image.ImageView;

import java.util.ArrayList;

/**
 * The Bomb class represents a bomb in the game.
 * Contributors: Daniel Gooden - dgooden@myune.edu.au
 */

public class Bomb {
    private final double x;
    private final double y;
    private final int power;
    private final ImageView graphic = new ImageView();
    private final ArrayList<ImageView> bombFrames;
    private boolean exploded = false;
    private Timeline countdown;
    private int frameIndex = 0;
    long lastFrameTime = 0;

    /**
     * Constructor
     * @param x
     * @param y
     */
    public Bomb(double x, double y, int power) {
        this.x = x;
        this.y = y;
        this.power = power;
        this.bombFrames = BombermanGraphics.getInstance().getBombFrames();
        //Sound
        Sound.getInstance().playSFX(Sound.Type.TICKING);

        updatePosition();
        updateGraphic();
    }

    /**
     * Update the position of the bomb
     */
    public void updatePosition() {
        graphic.setX(x * GameConstants.TILE_SIZE);
        graphic.setY(y * GameConstants.TILE_SIZE);
    }

    /**
     * Get the x position of the bomb
     * @return x
     */
    public double getX() {
        return x;
    }

    /**
     * Get the y position of the bomb
     * @return y
     */
    public double getY() {
        return y;
    }

    public Node getGraphic() {
        return graphic;
    }

    /**
     * Start the countdown for the bomb
     */
    public void updateCountdown(long now) {
        if (lastFrameTime == 0 || (now - lastFrameTime) >= 500_000_000) { // 500 milliseconds in nanoseconds


            lastFrameTime = now;
            frameIndex++;
            updateGraphic();
            if (frameIndex >= bombFrames.size()) {
                explode();
            }
        }
    }

    /**
     * Update the bomb graphic based on the timer
     */
    private void updateGraphic() {
        if (!exploded && frameIndex < bombFrames.size()) {
            graphic.setImage(bombFrames.get(frameIndex).getImage());
        }
    }

    /**
     * Trigger the explosion of the bomb
     */
    private void explode() {
        //  Sound
        Sound.getInstance().playSFX(Sound.Type.EXPLOSION);

        exploded = true;
    }

    public boolean hasExploded() {
        return exploded;
    }

    /**
     * Get the power of the bomb
     */
    public int getPower() {
        return power;
    }
}
