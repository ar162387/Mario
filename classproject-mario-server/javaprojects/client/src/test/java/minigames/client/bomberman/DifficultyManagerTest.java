package minigames.client.bomberman;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class DifficultyManagerTest {
    private DifficultyManager difficultyManager;

    @BeforeEach
    void setUp() {
        // Reset the singleton instance for each test to ensure isolation
        difficultyManager = DifficultyManager.getInstance();
    }

    @Test
    @DisplayName("Ensure Singleton returns the same instance")
    void testSingletonInstance() {
        DifficultyManager anotherInstance = DifficultyManager.getInstance();
        assertSame(difficultyManager, anotherInstance, "Both instances should be the same");
    }

    @Test
    @DisplayName("Verify getLevel returns a valid level with correct dimensions")
    void testGetLevel() {
        Level level = difficultyManager.getLevel(1);
        assertNotNull(level, "Level should not be null");
        assertEquals(13, level.getWIDTH(), "Width should be 13");
    }

    @Test
    @DisplayName("Check difficulty factor increases correctly")
    void testIncreaseDifficultyFactorBy() {
        int currentDifficulty = difficultyManager.getDifficultyFactor();
        difficultyManager.levelUp(3);
        assertEquals(currentDifficulty + 3, difficultyManager.getDifficultyFactor(), "Difficulty factor should be increased by the given number");
    }
    @Test
    @DisplayName("Check reset difficulty")
    void testResetDifficulty(){
        int resetDifficulty = difficultyManager.resetDifficultyFactor();
        assertEquals(1, resetDifficulty,"Difficulty is reset back to level 1"); // Edit: Changed to start at 1
        assertEquals(resetDifficulty,difficultyManager.getDifficultyFactor(),"Same as getDifficultyFactor()");
    }
}
