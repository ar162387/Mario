package minigames.client.bomberman;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DirectionTest {
    @Test
    void testEnumValues() {
        assertEquals(4, Direction.values().length);
        assertArrayEquals(new Direction[]{Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT}, Direction.values());
    }
}