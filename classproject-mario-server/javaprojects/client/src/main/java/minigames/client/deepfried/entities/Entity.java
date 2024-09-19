package minigames.client.deepfried.entities;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Timer;


public abstract class Entity {

    protected int x, y; // position of the entity
    protected int imageWidth, imageHeight; // for getting the center of the image
    protected int centerX, centerY;  // X and Y coordinates of the centre of the image
    protected JLabel entityImage; // image of the entity for displaying
    // these fields are for controlling entity timers (for pausing the game)
    protected Timer timer;
    protected boolean isPaused; 
    protected int totalTime;        
    protected long startTime;          
    protected int remainingTime;

    public Entity(int x, int y) {
        this.x = x;
        this.y = y;
    }
    // start timer, subclasses should provide own implementation if required
    public void startTimer(int remainingTime){};

    // method to pause the entity timer
    public void pauseTimer() {
        if (!isPaused && timer != null) {
            timer.stop();
            long elapsedTime = System.currentTimeMillis() - startTime;  // Calculate elapsed time
            remainingTime -= elapsedTime;  // Subtract elapsed time from remaining
            isPaused = true;
            // System.out.println("Entity paused with " + remainingTime / 1000.0 + " seconds left.");
        }
    }
    // method to resume the entity timer
    public void resumeTimer() {
        if (isPaused && remainingTime > 0) {
            startTimer(remainingTime);  // Restart the timer with the remaining time
            isPaused = false;
            // System.out.println("Entity resumed with " + remainingTime / 1000.0 + " seconds left.");
        }
    } 
    // Get the X coordinate of the centre of the entity image
    public int getCenterX() {
        imageWidth = getEntityImage().getIcon().getIconWidth();
        centerX = x + (imageWidth / 2);
        return centerX;
    }
    // Get the y coordinate of the centre of the entity image
    public int getCenterY() {
        imageHeight = getEntityImage().getIcon().getIconHeight();
        centerY = y + (imageHeight / 2);
        return centerY;
    }    
       //method to get player image and access in other files
    public JLabel getEntityImage() {
        return entityImage;
    }
    //load image of entity from location in resource folder
    protected void loadImage(String imagePath){
        try {
            BufferedImage image = ImageIO.read(getClass().getResource(imagePath));
            ImageIcon imageIcon = new ImageIcon(image);

            this.entityImage = new JLabel(imageIcon);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Change the entity image  
    public void updateImage(String imagePath) {
        getEntityImage().setIcon(new ImageIcon(getClass().getResource(imagePath)));
    }
    // set location of entityImage on screen
    public void updateLocation(){
        this.entityImage.setLocation(this.x, this.y);
    }

    public int getX(){
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY(){
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
    


    // Any other common methods
}
