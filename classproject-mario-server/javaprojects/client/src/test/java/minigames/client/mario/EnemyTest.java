package minigames.client.mario;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EnemyTest {
    private Enemy enemy;

    @BeforeEach
    public void SetUp() {
        enemy = new Enemy(10, 20, 50, 50, true);
    }

    @Test
    public void testUpdatePosition() {
        enemy.updatePosition(30, 40);
        assertEquals(30, enemy.getX());
        assertEquals(40, enemy.getY());
    }

    @Test
    public void testSetActive() {
        enemy.setActive(false);
        assertFalse(enemy.isActive());
    }

    @Test
    public void testLoadEnemyImages() {
        try {
            BufferedImage goombaL = ImageIO.read(getClass().getResourceAsStream("/images/mario/goomba-1.png"));
            BufferedImage goombaR = ImageIO.read(getClass().getResourceAsStream("/images/mario/goomba-r.png"));
            assertNotNull(goombaL);
            assertNotNull(goombaR);
        } catch (IOException e) {
            fail("Image loading failed");
        }
    }
}
