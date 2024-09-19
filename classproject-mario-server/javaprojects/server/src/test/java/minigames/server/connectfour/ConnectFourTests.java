package minigames.server.connectfour;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import minigames.server.connectfour.*;
import minigames.connectfour.*;
import minigames.rendering.*;
import minigames.commands.*;
import minigames.connectfour.WinState;

public class ConnectFourTests {

    @Test
    public void joinGameTest() {
        ConnectFourGame game = new ConnectFourGame("Testing");

        HashMap<String, ConnectFourGame.ConnectFourPlayer> expectedMap = new HashMap<>();
        assertEquals(game.players, expectedMap);

        String playerName = "User";
        RenderingPackage rpac = game.joinGame(playerName);
        expectedMap.put(playerName, new ConnectFourGame.ConnectFourPlayer(playerName));
        assertEquals(game.players, expectedMap);

        assertEquals(game.activePlayer, playerName);
        assertNotEquals(game.activePlayer, "Player 2");
    }

    @Test
    public void getPlayerNamesTest() {
        ConnectFourGame game = new ConnectFourGame("Testing");

        String[] emptyArray = new String[0];
        assertArrayEquals(game.getPlayerNames(), emptyArray);

        String[] oneName = new String[] {"Player 1"};
        game.joinGame("Player 1");
        assertArrayEquals(game.getPlayerNames(), oneName);

        String[] twoNames = new String[] {"Player 2", "Player 1"};
        game.joinGame("Player 2");
        assertArrayEquals(game.getPlayerNames(), twoNames);
    }

    @Test
    public void runCommandsValidDiskLocationTest() {
        ConnectFourGame game = new ConnectFourGame("Testing");
        game.joinGame("Player 1");
        game.joinGame("Player 2");

        GameMetadata metadata = game.gameMetadata();
        JsonObject json = new JsonObject();
        json.put("command", "putDisk");
        json.put("column", 0);
        CommandPackage compac = new CommandPackage(metadata.gameServer(), metadata.name(),
                "Player 1", Collections.singletonList(json));
        RenderingPackage rpac = game.runCommands(compac);
        List<JsonObject> response = rpac.renderingCommands();


        for (JsonObject resp : response) {
                assertNotEquals(resp.getString("error"), "Not active player");
                assertNotEquals(resp.getString("error"), "Invalid index");
                assertNotEquals(resp.getString("error"), "Column full");
                assertEquals(resp.getString("error"), "None");
        }
    }

    @Test
    public void runCommandsInvalidDiskLocationTest() {
        ConnectFourGame game = new ConnectFourGame("Testing");
        game.joinGame("Player 1");
        game.joinGame("Player 2");

        GameMetadata metadata = game.gameMetadata();
        JsonObject json = new JsonObject();
        json.put("command", "putDisk");
        json.put("column", 9);
        CommandPackage compac = new CommandPackage(metadata.gameServer(), metadata.name(),
                "Player 1", Collections.singletonList(json));
        RenderingPackage rpac = game.runCommands(compac);
        List<JsonObject> response = rpac.renderingCommands();


        for (JsonObject resp : response) {
            assertEquals(resp.getString("error"), "Invalid index");
            assertNotEquals(resp.getString("error"), "None");
        }
    }

    @Test
    public void runCommandsGetPlayerNameTest() {
        ConnectFourGame game = new ConnectFourGame("Testing");
        game.joinGame("Player 1");
        game.joinGame("Player 2");

        GameMetadata metadata = game.gameMetadata();
        JsonObject json = new JsonObject();
        json.put("command", "getPlayerName");
        CommandPackage compac = new CommandPackage(metadata.gameServer(), metadata.name(),
                "Player 1", Collections.singletonList(json));
        RenderingPackage rpac = game.runCommands(compac);
        List<JsonObject> response = rpac.renderingCommands();

        String[] nameArray = new String[]{"Player 2", "Player 1"};

        for (JsonObject resp : response) {
            assertArrayEquals(nameArray, (String[]) resp.getValue("playerName"));
        }
    }

    @Test
    public void gameMetadataTest() {
        ConnectFourGame game = new ConnectFourGame("Testing");
        game.joinGame("Player 1");
        assertEquals(game.gameMetadata().joinable(), true);

        game.joinGame("Player 2");
        assertEquals(game.gameMetadata().joinable(), false);
    }

    @Test
    public void runCommandsGetStateTest() {
        ConnectFourGame game = new ConnectFourGame("Testing");
        game.joinGame("Player 1");
        game.joinGame("Player 2");

        GameMetadata metadata = game.gameMetadata();
        JsonObject json = new JsonObject();
        json.put("command", "getState");
        CommandPackage compac = new CommandPackage(metadata.gameServer(), metadata.name(),
                "Player 1", Collections.singletonList(json));
        RenderingPackage rpac = game.runCommands(compac);
        List<JsonObject> response = rpac.renderingCommands();

        String[] testArray = new String[7];
        for (int i = 0; i < testArray.length; i++) {
            testArray[i] = "";
        }

        for (JsonObject resp : response) {
            String[][] grid = (String[][]) resp.getValue("grid");
            for (String[] row : grid) {
                assertArrayEquals(testArray, row);
            }

            assertEquals(resp.getValue("activePlayer"), "Player 1");
            assertEquals(WinState.valueOf((String) resp.getValue("winState")), WinState.ONGOING);
        }
    }

    @Test
    public void initialiseGridTest() {
        ConnectFourGame game = new ConnectFourGame("Testing");
        String[][] initialisedGrid = game.initialiseGrid(6, 7);

        assertEquals(initialisedGrid.length, 6);
        assertEquals(initialisedGrid[0].length, 7);

        String[] testArray = new String[7];
        for (int i = 0; i < testArray.length; i++) {
            testArray[i] = "";
        }

        for (String[] row : initialisedGrid) {
            assertArrayEquals(testArray, row);
        }
    }

    @Test
    public void getNonActivePlayerTest() {
        ConnectFourGame game = new ConnectFourGame("Testing");

        game.joinGame("Player1");
        game.joinGame("Player2");

        String nonActivePlayer = game.getNonActivePlayer().orElse("No Players");

        assertEquals(nonActivePlayer, "Player2");
    }

    /* This is testing whether isFourInARow will correctly check for a match along a row. */
    @Test
    public void isFourInARowRowTest() {
        ConnectFourGame game = new ConnectFourGame("Testing");

        for (int j = 0; j < 4; j++) {
            game.grid[0][j] = "Player1";
        }
        assertTrue(game.isFourInARow());

        game.grid = game.initialiseGrid(6, 7);
        for (int j = 2; j < 6; j++) {
            game.grid[0][j] = "Player1";
        }
        assertTrue(game.isFourInARow());
    }

    /* This is testing whether isFourInARow will correctly check for a match up a column. */
    @Test
    public void isFourInARowColumnTest() {
        ConnectFourGame game = new ConnectFourGame("Testing");

        for (int i = 0; i < 4; i++) {
            game.grid[i][0] = "Player1";
        }
        assertTrue(game.isFourInARow());
    }

    /* This is testing whether isFourInARow will correctly check for a match along the
    * right diagonal. */
    @Test
    public void isFourInARowRightDiagonalTest() {
        ConnectFourGame game = new ConnectFourGame("Testing");

        for (int i = 0, j = 0; i < 4 && j < 4; i++, j++) {
            game.grid[i][j] = "Player1";
        }
        assertTrue(game.isFourInARow());
    }

    /* This is testing whether isFourInARow will correctly check for a match along the
    * left diagonal. */
    @Test
    public void isFourInARowLeftDiagonalTest() {
        ConnectFourGame game = new ConnectFourGame("Testing");

        for (int i = 0, j = 4; i < 4 && j >= 0; i++, j--) {
            game.grid[i][j] = "Player1";
        }
        assertTrue(game.isFourInARow());
    }

    /* Testing isGridFull() on an empty (only contains "") grid. */
    @Test
    public void isGridFullEmptyTest() {
        ConnectFourGame game = new ConnectFourGame("Testing");

        assertFalse(game.isGridFull());
    }

    /* Testing isGridFull() on a full grid. */
    @Test
    public void isGridFullFullTest() {
        ConnectFourGame game = new ConnectFourGame("Testing");

        for (int i = 0; i < game.grid.length; i++) {
            for (int j = 0; j < game.grid[i].length; j++) {
                game.grid[i][j] = "Player1";
            }
        }

        assertTrue(game.isGridFull());
    }
}