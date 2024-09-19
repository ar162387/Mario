package minigames.client.bomberman;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class GameConstantsTest {
    int columns = GameConstants.BOARD_NO_OF_COLUMNS;
    int rows = GameConstants.BOARD_NO_OF_ROWS;
    @Test
    @DisplayName("Check that the rows and columns are symmetrical when adding pillars to the board (odd)")
    void testGameConstantBoardRowsAndColumnsAreOdd() {
        assertEquals(1,columns % 2,"Columns for the board should be odd");
        assertEquals(1,rows % 2, "Rows for the board should be odd");
    }
}