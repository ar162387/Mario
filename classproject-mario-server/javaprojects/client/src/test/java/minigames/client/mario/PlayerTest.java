package minigames.client.mario;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class PlayerTest {
    private Player player;

    @BeforeEach
    public void setUp() {
        player = new Player(50, 50, 32, 64);
    }

    @Test
    public void testUpdatePosition() {
        player.updatePosition(100, 150, false, "left");
        assertEquals(100, player.getX());
        assertEquals(150, player.getY());
        assertFalse(player.isOnGround());
    }

    @Test
    public void testDraw() {
        Graphics graphics = Mockito.mock(Graphics.class);
        player.draw(graphics);
        // Verify drawing methods, need to use a mocking library
    }

    @Test
    public void testLoadPlayerImages() {
        // Ensure that images are loaded correctly
        try {
            BufferedImage marioIdleL = ImageIO.read(getClass().getResourceAsStream("/images/mario/marioIdleL.png"));
            BufferedImage marioIdleR = ImageIO.read(getClass().getResourceAsStream("/images/mario/marioIdleR.png"));
            assertNotNull(marioIdleL);
            assertNotNull(marioIdleR);
        } catch (IOException e) {
            fail("Image loading failed");
        }
    }
}