package minigames.server.connectfour;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.json.JsonObject;
import minigames.commands.CommandPackage;
import minigames.rendering.*;
import minigames.rendering.NativeCommands.LoadClient;
import minigames.connectfour.WinState;

/*
 * The actual Connect Four game state
 * */
public class ConnectFourGame {
    /* Logging */
    private static final Logger logger = LogManager.getLogger(ConnectFourGame.class);

    /**
     * @param name Player provided name of type String
     */
    public record ConnectFourPlayer(String name) {}

    /* Mapping of player names to their player records */
    public HashMap<String, ConnectFourPlayer> players = new HashMap<>();

    /* activePlayer gets updated with the name of the currently active player, which can then be used with the
     * players map */
    public String activePlayer;

    /* Describes whether the current game is still ongoing, has been won, or is a draw. */
    WinState winState;

    /* Unique ID for this game instance */
    String name;

    /* This grid is used to represent the Connect Four game grid.
     It is structured from the bottom left of the grid upwards: i.e.,
     grid[0][0] is the bottom left corner; grid[6][7] is the top right corner. */
    public String[][] grid;

    /**
     * @param name Unique ID for this game instance.
     */
    public ConnectFourGame(String name) {
        this.name = name;
        this.activePlayer = "";
        this.winState = WinState.ONGOING;
        this.grid = initialiseGrid(6, 7);
    }

    /**
     * Initialises a grid of size [rows][columns] with each
     * element being the empty String ("").
     *
     * @param rows Number of rows in grid (how tall the grid is)
     * @param columns Number of columns in grid (how long the grid is)
     * @return the initialised grid
     */
    public String[][] initialiseGrid(int rows, int columns) {
        String[][] grid = new String[rows][columns];

        for (String[] row : grid) {
            Arrays.fill(row, "");
        }

        return grid;
    }

    /**
     * @return String[] of current players in this game.
     */
    public String[] getPlayerNames() {
        return players.keySet().toArray(String[]::new);
    }

    /**
     * @return GameMetadata object containing the current metadata for this game.
     */
    public GameMetadata gameMetadata() {
        if (getPlayerNames().length >= 2) {
            return new GameMetadata("ConnectFour", name, getPlayerNames(), false);
        } else {
            return new GameMetadata("ConnectFour", name, getPlayerNames(), true);
        }
    }

    /**
     * @param p Valid ConnectFourPlayer record object.
     * @return Stringified version of current game state.
     */
    private String describeState(ConnectFourPlayer p) {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("Name: %s", p.name));

        return sb.toString();
    }

    /**
     * @return Optional String containing the player who is not currently active. i.e., the one waiting for their turn.
     */
    public Optional<String> getNonActivePlayer() {
        return players.keySet().stream()
                .filter(name -> !(name.equals(activePlayer)))
                .findFirst();
    }

    /**
     * Checks whether the chosen column is already full or not.
     * @param column Index of column in this.grid to check
     * @return true if column is full, else false
     */
    private boolean isColumnFull(int column) {
        return !(grid[grid.length - 1][column].isEmpty());
    }

    /**
     * Tries to find the first free slot within the given column. This should be used after
     * the isColumnFull() method, otherwise it could throw an IllegalStateException on what turns out to be a full
     * column.
     * @param column Index of column in this.grid to check
     * @return int value of first free slot in column (i.e., the first empty row index).
     */
    private int findFirstFreeSlot(int column) {
        for (int i = 0; i < grid.length; i++) {
            if (grid[i][column].isEmpty()) {
                return i;
            }
        }
        // If there isn't a free slot found (which shouldn't be possible given we already check that)
        throw new IllegalStateException("Chosen column is full.");
    }

    /**
     * Tries to put a disk into the first free slot within the column the player has chosen.
     * @param player Valid ConnectFourPlayer record object
     * @param column Optional<JsonObject> object that should contain the index of the column the user would like to
     *               place a disk in
     * @return JsonObject containing the error value of the operation. All are in the form '{"error": value}'. Value
     * has three different concrete values: "None", "Invalid index" and "Column full".
     */
    private JsonObject putDisk(ConnectFourPlayer player, Optional<JsonObject> column) {
        JsonObject response = new JsonObject();

        try {
            int columnNumber = column.orElseThrow(NoSuchElementException::new).getInteger("column");
            if (isColumnFull(columnNumber)) {
                throw new IllegalStateException("Chosen column is full.");
            } else {
                int row = findFirstFreeSlot(columnNumber);
                grid[row][columnNumber] = player.name;
                activePlayer = getNonActivePlayer().orElseGet(() -> activePlayer);
                response.put("error", "None");
            }
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException | NoSuchElementException e) {
            response.put("error", "Invalid index");
        } catch (IllegalStateException e) {
            response.put("error", "Column full");
        }

        return response;
    }

    /**
     * Checks along each row of this.grid to find if there are any matches.
     * @return true if match found, otherwise false.
     */
    public boolean checkRowMatch() {
        int inARow = 0;

        for (String[] rows : grid) {
            for (int j = 0; j < rows.length; j++) {
                logger.info("current value: {}", rows[j]);
                if (rows[j].isEmpty()) {
                    inARow = 0;
                    continue;
                } else if (!(j - 1 < 0) && rows[j].equals(rows[j - 1])) {
                    inARow++;
                }

                /* This is checking that inARow == 3 because the first element of the 4
                 * will have something not equal to it to its left.
                 * E.g. grid = [player1, player1, player1, player1]
                 *  grid[0] == player1, but grid[0-1] doesn't. This means that if
                 * you want n in a row, you need to check for n-1 in a row. */
                if (inARow == 3) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Checks up each column of this.grid to find if there are any matches.
     * @return true if match found, otherwise false.
     */
    public boolean checkColumnMatch() {
        int inARow = 0;

        for (int j = 0; j < grid[0].length; j++) {
            for (int i = 0; i < grid.length; i++) {
                if (grid[i][j].isEmpty()) {
                    inARow = 0;
                    continue;
                }

                if (!(i - 1 < 0) && grid[i][j].equals(grid[i - 1][j])) {
                    inARow++;
                }

                /*  This is checking that inARow == 3 because the first element of the 4
                 * will have something not equal to it to its left.
                 * E.g. grid = [player1, player1, player1, player1]
                 *  grid[0] == player1, but grid[0-1] doesn't. This means that if
                 * you want n in a row, you need to check for n-1 in a row.*/
                if (inARow == 3) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Checks along each diagonal of this.grid to find if there are any matches.
     * @return true if match found, otherwise false.
     */
    public boolean checkDiagonalMatch() {
        //  Right diagonal
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j].isEmpty()) {
                    continue;
                }

                if (i+3 > grid.length || j+3 > grid[i].length) {
                    continue;
                }

                String match = grid[i][j];
                if (grid[i+1][j+1].equals(match) &&
                grid[i+2][j+2].equals(match) &&
                grid[i+3][j+3].equals(match)) {
                    return true;
                }
            }
        }

        // Left diagonal
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j].isEmpty()) {
                    continue;
                }

                if (i+3 > grid.length || j-3 < 0) {
                    continue;
                }

                String match = grid[i][j];
                if (grid[i+1][j-1].equals(match) &&
                grid[i+2][j-2].equals(match) &&
                grid[i+3][j-3].equals(match)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Metamethod for the check*Match() methods. Just runs all of them.
     * @return true if a match is found, otherwise false.
     */
    public boolean isFourInARow() {
        /* loop over grid and find if there are any matches */
        logger.info("Working on current grid {}", Arrays.deepToString(grid));

            if (checkRowMatch()) {
                return true;
            } else if (checkColumnMatch()) {
                return true;
            } else return checkDiagonalMatch();
    }

    /**
     * Checks if this.grid is entirely full (each element != "") or not.
     * @return true if grid is full, otherwise false.
     */
    public boolean isGridFull() {
        return Arrays.stream(grid[grid.length - 1]).noneMatch(""::equals);
    }

    /**
     * @return JsonObject containing the grid state, current active player, win state, and an optional fourth field
     * announcing the winner if a winner has been found.
     */
    private JsonObject gameState() {
        JsonObject result = new JsonObject();

        result.put("grid", grid);
        result.put("activePlayer", activePlayer);
        result.put("winState", winState);

        if (winState.equals(WinState.WIN)) {
            result.put("winner", activePlayer);
        }

        logger.info("Returning JsonObject {}", result);

        return result;
    }

    /**
     * Effectively the main loop for this class. Takes in a CommandPackage from the client and then tries to
     * run the commands within that package. getState and getPlayerName are to be run automatically by the
     * Web Sockets runner, not so much be the client code. Though the client could also run them directly if
     * there's a need to.
     * @param cp CommandPackage containing the commands the client would like the server to run. putDisk requires
     *           another command to be sent with it of the form {"column": columnIndex} where columnIndex is the
     *           column the player would like to place a disk into.
     * @return RenderingPackage containing the info associated with the input command.
     * getState returns the current game state;
     * getPlayerName returns a List containing the current player names;
     * putDisk returns either the error "Not active player" if the player sending the command package is not the
     * currently active player, or the value returned from the putDisk() method;
     */
    public RenderingPackage runCommands(CommandPackage cp) {
        logger.info("Received command package {}", cp);
        ConnectFourPlayer p = players.get(cp.player());

        ArrayList<JsonObject> renderingCommands = new ArrayList<>();

        for (JsonObject command : cp.commands()) {
            switch (command.getString("command")) {
                case "getState" -> renderingCommands.add(gameState());
                case "getPlayerName" -> renderingCommands.add(new JsonObject().put("playerName", getPlayerNames()));
                case "putDisk" -> {
                    if (!(cp.player().equals(activePlayer))) {
                        renderingCommands.add(new JsonObject().put("error", "Not active player"));
                    } else {
                        if (!(winState.equals(WinState.WIN) || !winState.equals(WinState.DRAW))) {
                            renderingCommands.add(putDisk(p, cp.commands().stream()
                                    .filter(comm -> comm.containsKey("column"))
                                    .findFirst()));
                        }
                        if (isFourInARow()) {
                            winState = WinState.WIN;
                        } else if (isGridFull()) {
                            winState = WinState.DRAW;
                        }
                    }
                }
                /* TODO: I need to figure out exactly how the QuitToMenu record works */
                //case "quit" -> renderingCommands.add(new JsonObject().put("quit", "Still needs to be implemented"));
            }
        }

        return new RenderingPackage(this.gameMetadata(), renderingCommands);
    }

    /**
     * Attempts to join game with provided player name.
     * @param playerName Name the player would like to use for game.
     * @return RenderingPackage containing the results of trying to join game. If player's chosen name
     * is already taken, RenderingPackage contains NativeCommands.ShowMenuError("That name's not available");
     * if game already has 2 people playing, RenderingPackage contains NativeCommands.ShowMenuError("There are already
     * two players in this match");
     * otherwise, the RenderingPackage contains a LoadClient() object.
     */
    public RenderingPackage joinGame(String playerName) {
        if (players.containsKey(playerName)) {
            return new RenderingPackage(
                    gameMetadata(),
                    Arrays.stream(new RenderingCommand[]{
                            new NativeCommands.ShowMenuError("That name's not available")
                    }).map(RenderingCommand::toJson).toList()
            );
        } else if (getPlayerNames().length >= 2) {
            return new RenderingPackage(
                    gameMetadata(),
                    Arrays.stream(new RenderingCommand[]{
                            new NativeCommands.ShowMenuError("There are already two players in this match.")
                    }).map(RenderingCommand::toJson).toList()
            );
        } else {
            ConnectFourPlayer p = new ConnectFourPlayer(playerName);
            players.put(playerName, p);

            if (activePlayer.isEmpty()) {
                activePlayer = playerName;
            }

            ArrayList<JsonObject> renderingCommands = new ArrayList<>();
            renderingCommands.add(new LoadClient("ConnectFour", "ConnectFour", name, playerName).toJson());

            return new RenderingPackage(gameMetadata(), renderingCommands);
        }
    }
}