package minigames.smallworld;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

public class Player {
    // position in pixels
    int x, y;
    BufferedImage sprite;

    static int TILE_SIZE = Tile.SIZE;    
    int width =  TILE_SIZE;
    int height = TILE_SIZE;

    int speed = 5;
    float verticalVelocity = 0;
    boolean airborne = true;
    int JUMP_STRENGTH = -10;

    int itemsCollected = 0;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
        this.sprite = loadSprite("dotsy.png");
    }

    /**
     * Velocity is the player's vertical speed.
     * 
     * If it's positive the player is falling,
     *  if it's negative the player is jumping
     * @param dy
     */
    public void increaseVelocity(float dy) {
        this.verticalVelocity += dy;
    }

    public void jump() {
        if(!airborne) {
            verticalVelocity = JUMP_STRENGTH;
            this.airborne = true;
        }
    }

    public void collect(){
        this.itemsCollected++;
    }

    public int getItemCount(){
        return this.itemsCollected;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isAirborne() {
        return this.airborne;
    }

    public void setAirborne(boolean b) {
        this.airborne = b;
    }

    public void resetAirborne() {
        this.airborne = false;
        this.verticalVelocity = 0;
    }

    public int getVelocity() {
        return (int) this.verticalVelocity;
    }

    public void setVelocity(int v) {
        this.verticalVelocity = v;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getSpeed() {
        return this.speed;
    }

    public void moveLeft() {
        this.x -= this.speed;
    }

    public void moveUp() {
        this.y -= this.speed;
    }

    public void moveRight() {
        this.x += this.speed;
    }

    public void moveDown() {
        this.y += this.speed;
    }

    /**
     * 
     * @param dx positive = right, negative = left
     */
    public void moveHorizontal(int dx) {
        this.x += dx;
    }

    /**
     * @param dy positive = down, negative = up
     */
    public void moveVertical(int dy) {
        this.y += dy;
    }

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

    public void render(Graphics g) {
        g.drawImage(this.sprite, this.x, this.y, Tile.SIZE, Tile.SIZE, null);
    }



    /**
     * Used for rendering when the "camera" has moved
     * 
     * @param g
     * @param xOffset
     * @param yOffset
     */
    public void render(Graphics g, int xOffset, int yOffset) {
        g.drawImage(this.sprite, this.x - xOffset, this.y - yOffset, Tile.SIZE, Tile.SIZE, null);
    }

}
