package minigames.server.hangman;


import java.util.HashMap;
import io.vertx.core.Future;
import minigames.commands.CommandPackage;
import minigames.rendering.GameMetadata;
import minigames.rendering.GameServerDetails;
import minigames.rendering.RenderingPackage;
import minigames.server.ClientType;
import minigames.server.GameServer;


public class HangmanServer implements GameServer {
    
    /**Hash map to store the games. */
    private HashMap<String, HangmanGame> games = new HashMap<>(); 

    public HangmanServer() {
        // Here we will initialize the server with any necessary data.
    }

    /**
     * Returns details about the game server.
     *
     * @return The game server details.
     */
    @Override
    public GameServerDetails getDetails() {
        return new GameServerDetails("HangMan", "Hangman is a popular word guessing game where the player attempts to build a missing word by guessing one letter at a time.");
    }

    /**
     * Returns the supported client types for this game server.
     *
     * @return An array of supported client types.
     */
    @Override
    public ClientType[] getSupportedClients() {
        return new ClientType[] { ClientType.Swing, ClientType.Scalajs, ClientType.Scalafx };
    }

    /**
     * Returns the metadata of all games in progress.
     *
     * @return An array of game metadata.
     */
    @Override
    public GameMetadata[] getGamesInProgress() {
        return games.values().stream()
                .map(HangmanGame::gameMetadata)
                .toArray(GameMetadata[]::new);
    }

    /**
     * Creates a new game and returns the rendering package for the initial game state.
     *
     * @param playerName The name of the player.
     * @return A future containing the rendering package for the initial game state.
     */
    @Override
    public Future<RenderingPackage> newGame(String playerName) {
        // Generate a unique game ID 
        String gameId = "Hangman-Game-" + (games.size() + 1); //This should be unique
        
        // Create a new HangmanGame instance with the generated game ID
        HangmanGame game = new HangmanGame(gameId);
        
        // Add the game to the HashMap of games
        games.put(gameId, game);
        
        // Join the game with the specified player name
        return Future.succeededFuture(game.joinGame(playerName));
    }

    /**
     * Joins an existing game and returns the rendering package for the current game state.
     *
     * @param gameId     The ID of the game to join.
     * @param playerName The name of the player.
     * @return A future containing the rendering package for the current game state.
     */
    @Override
    public Future<RenderingPackage> joinGame(String gameId, String playerName) {
        // Get the HangmanGame instance associated with the given gameId
        HangmanGame game = games.get(gameId);
        
        // If the game exists, join the game with the specified player name
        if (game != null) {
            return Future.succeededFuture(game.joinGame(playerName));
        }
        
        // If the game does not exist, return a failed future with an error message
        return Future.failedFuture("Game not found");
    }

    /**
     * Executes game commands and returns the rendering package for the updated game state.
     *
     * @param commands The game commands to execute.
     * @return A future containing the rendering package for the updated game state.
     */
    @Override
    public Future<RenderingPackage> callGame(CommandPackage commands) {
        HangmanGame game = games.get(commands.gameId());
        if (game != null) {
            return Future.succeededFuture(game.runCommands(commands));
        }
        return Future.failedFuture("Game not found");
    }
}
