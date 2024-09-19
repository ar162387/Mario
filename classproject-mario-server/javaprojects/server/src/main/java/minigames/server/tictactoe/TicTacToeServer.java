package minigames.server.tictactoe;

import io.vertx.core.Future;
import minigames.commands.CommandPackage;
import minigames.rendering.GameMetadata;
import minigames.rendering.GameServerDetails;
import minigames.rendering.RenderingPackage;
import minigames.server.ClientType;
import minigames.server.GameServer;
import java.util.HashMap;
import java.util.Random;
/**
 * The TicTacToeServer holds TicTacToe games
 * When it receives a CommandPackage, it finds the TicTacToe game and calls it. 
 */
public class TicTacToeServer implements GameServer {
    static final String chars = "abcdefghijklmopqrstuvwxyz";
    /** A random name generator */
    static String randomName() {
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            sb.append(chars.charAt(r.nextInt(chars.length())));
        }
        return sb.toString();
    }
    /** Holds the games in progress in memory (no db) */
    HashMap<String, TicTacToeGame> games = new HashMap<>();
    @Override
    public GameServerDetails getDetails() {
        return new GameServerDetails("TicTacToe", "TicTacToe is a classic game where players take turns marking spaces in a 3x3 grid.");
    }
    @Override
    public ClientType[] getSupportedClients() {
        return new ClientType[] { ClientType.Swing, ClientType.Scalajs, ClientType.Scalafx };
    }
    @Override
    public GameMetadata[] getGamesInProgress() {
        return games.keySet().stream().map(name -> 
            new GameMetadata("TicTacToe", name, games.get(name).getPlayerNames(), true)
        ).toArray(GameMetadata[]::new);
    }
    @Override
    public Future<RenderingPackage> newGame(String playerName) {
        String gameMode = "1P vs 2P";
        TicTacToeGame game = new TicTacToeGame(randomName(), playerName, gameMode);
        games.put(game.name, game);
        return Future.succeededFuture(game.joinGame(playerName));
    }
    @Override
    public Future<RenderingPackage> joinGame(String gameName, String playerName) {
        TicTacToeGame game = games.get(gameName);
        if (game != null) {
            return Future.succeededFuture(game.joinGame(playerName));
        } else {
            // Handle case where game does not exist
            return Future.failedFuture(new Exception("Game not found"));
        }
    }
    @Override
    public Future<RenderingPackage> callGame(CommandPackage cp) {
        TicTacToeGame game = games.get(cp.gameId());
        if (game != null) {
            // Process the CommandPackage and run game commands
            return Future.succeededFuture(game.runCommands(cp));
        } else {
            // Handle case where game does not exist
            return Future.failedFuture(new Exception("Game not found"));
        }
    }
}