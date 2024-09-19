package minigames.client.bomberman;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class SpriteSheetTest {

    private static final String IMAGE_PATH = "images/bomberman/bombpartyv4.png";
    private SpriteSheet spriteSheet; // use for a spy

    // Create a mock Image
    private Image mockImage = mock(Image.class);

    //        mockImage = mock(Image.class);
    private ImageView testImageView;

//    private WritableImage mockImage;
    @BeforeEach
    void setUp() {
        // Create a mock ImageView

        // Create a single-pixel image
        WritableImage writableImage = new WritableImage(500, 500);
        PixelWriter pixelWriter = writableImage.getPixelWriter();
        pixelWriter.setColor(0, 0, Color.RED); // Set the single pixel to red

        testImageView = new ImageView(writableImage);

        // Create a mock using spy method
        spriteSheet = spy(new SpriteSheet("Test path", 32, 32, 0, 0, 0, 8));

//        // Use the spy method to mock
//        doReturn(testImageView).when(spriteSheet).setImageView(anyString());

//        //mutate the value (Figured it out that the variable was final so couldnt mutate
//        spriteSheet.setImageView("Test path");
//        System.out.println(spriteSheet.getImageView());

        // Create a SpriteSheet with the mock image using Mockito Mocked Construction requires inline mockito
//
//        try (MockedConstruction<SpriteSheet> mocked = mockConstruction(SpriteSheet.class,
//                (mock, context) -> {
//                    when(mock.getImageView(anyString())).thenReturn(testImageView);
//                })) {
//            spriteSheet = new SpriteSheet("mock_path.png", 32, 32, 0, 0, 0, 8);
//            // spriteSheet is now mocked with parameters
//        }

    }
    @Test
    @DisplayName("Bomberman png should exist in JAR resource folder")
    void testSpriteSheetFileInTheJAR() {
        String resourcePath = "/" + IMAGE_PATH; // Need to add a / infront for getResourceAsStream


        // Use InputStream because it is more versatile
        InputStream inputStream = getClass().getResourceAsStream(resourcePath);

        //Check

        assertNotNull(inputStream, "Bomberman PNG should exist in JAR resource folder");
    }
    @Test
    @DisplayName("SpriteSheet should create a tile based on the location index")
    void testCreateTileForIndex() {
        ImageView tileView = spriteSheet.createTile(10,testImageView);  // 10th tile (2nd row, 3rd column)

        // Assert
        assertNotNull(tileView, "Tile ImageView should not be null");
        Rectangle2D viewport = tileView.getViewport();
        assertNotNull(viewport, "Tile viewport should not be null");
        assertEquals(64, viewport.getMinX(), "X offset should be 64 (2 tiles width)");
        assertEquals(32, viewport.getMinY(), "Y offset should be 32 (1 tile height + header)");
        assertEquals(32, viewport.getWidth(), "Tile width should be 32");
        assertEquals(32, viewport.getHeight(), "Tile height should be 32");
    }

    @Test
    @DisplayName("SpriteSheet should create an JavaFX Image from filePath String")
    void testGetJavaFXImage() {
        ImageView tileView = spriteSheet.createTile(0,testImageView);  // First tile

        assertNotNull(tileView, "Tile ImageView should not be null");
        assertSame(testImageView.getImage(), tileView.getImage(), "ImageView should use the mock Image");
    }

}