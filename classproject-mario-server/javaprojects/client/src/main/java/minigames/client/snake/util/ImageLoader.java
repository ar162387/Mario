package minigames.client.snake.util;

import minigames.client.snake.ui.SnakeGame;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;

public class ImageLoader {

    /**
     * Loads an image from the specified path.
     *
     * @param path The file path to the image.
     * @return The loaded Image object, or null if loading fails.
     */
    public static Image loadImage(String path) {
        try (InputStream resourceStream = ImageLoader.class.getClassLoader().getResourceAsStream(path)) {
            if (resourceStream == null) {
                throw new RuntimeException("File not found: " + path);
            }
            return ImageIO.read(resourceStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This loads an image from a file and resizes it to fit within the cell size.
     * note: use transparent images to not cover gridlines and also allow a background change in
     * future versions
     *
     * @param filename the file to load the image from
     * @return Image that is resized to match the cell size
     */

    public static Image loadAndResizeImage(String filename, int cellSize) {
        Image image = null;
        try {
            // Load and resize the image in one step
            image = ImageIO.read(SnakeGame.class.getResource(filename))
                    .getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle case where image isn't found or doesn't load
        }
        return image;
    }
}