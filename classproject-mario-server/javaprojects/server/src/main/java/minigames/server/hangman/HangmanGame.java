package minigames.server.hangman;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import io.vertx.core.json.JsonObject;
import minigames.commands.CommandPackage;
import minigames.rendering.GameMetadata;
import minigames.rendering.NativeCommands;
import minigames.rendering.RenderingPackage;


public class HangmanGame {
    /**
     * The unique identifier for the game
     */
    private final String gameId;

    /**
     * Flag indicating whether the game is over or not
     */
    private boolean gameOver;

    /**
     * Map of player names to player IDs
     */
    private Map<String, String> players;

    public HangmanGame(String gameId) {
        this.gameId = gameId;
    }

    /**
     * Get the names of all players in the game
     *
     * @return an array of player names
     */
    public String[] getPlayerNames() {
        return players.keySet().toArray(new String[0]);
    }

    /**
     * Get the metadata of the game
     *
     * @return the game metadata
     */
    public GameMetadata gameMetadata() {
        return new GameMetadata("HangMan", gameId, getPlayerNames(), !gameOver);
    }

    /**
     * Initialize the game with initial game state
     *
     * @return the initial game state as a JsonObject
     */
    private JsonObject initializeGame() {
        return new JsonObject()
            .put("action", "INITIALIZE_GAME")
            .put("gameOver", gameOver);
    }

    /**
     * Join the game with a player name
     *
     * @param playerName the name of the player joining the game
     * @return the rendering package for the joined game
     */
    public RenderingPackage joinGame(String playerName) {
        players.put(playerName, playerName);

        List<JsonObject> renderingCommands = new ArrayList<>();
        renderingCommands.add(new NativeCommands.LoadClient("HangMan", "HangMan", gameId, playerName).toJson());
        renderingCommands.add(initializeGame());

        return new RenderingPackage(gameMetadata(), renderingCommands);
    }

    /**
     * Run commands in the game
     *
     * @param commands the commands to be executed in the game
     * @return the rendering package after executing the commands
     */
    public RenderingPackage runCommands(CommandPackage commands) {
        List<JsonObject> renderingCommands = new ArrayList<>();
        
        // Add logic to process the commands and update the game state
        
        return new RenderingPackage(gameMetadata(), renderingCommands);
    }
}
