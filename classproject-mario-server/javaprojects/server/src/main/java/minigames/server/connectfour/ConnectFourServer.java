package minigames.server.connectfour;

import io.vertx.core.Future;
import minigames.commands.CommandPackage;
import minigames.rendering.GameMetadata;
import minigames.rendering.GameServerDetails;
import minigames.rendering.RenderingPackage;
import minigames.server.ClientType;
import minigames.server.GameServer;

import java.util.HashMap;
import java.util.Random;

public class ConnectFourServer implements GameServer {

    /** String of character the randomName() method can pick from. */
    static final String chars = "abcdefghijklmopqrstuvwxyz";

    /**
     * @return A random name built from the characters contained in this.chars
     */
    static String randomName() {
        Random r = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 12; i++) {
            sb.append(chars.charAt(r.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /**
     * HashMap of current Connect Four games being played.
     */
    private HashMap<String, ConnectFourGame> games = new HashMap<>();

    /**
     * @return Name and description of game
     */
    @Override
    public GameServerDetails getDetails() {
        return new GameServerDetails("ConnectFour", "Two player Connect Four minigame.");
    }

    /**
     * @return Array of supported client types.
     */
    /*TODO: See if this game will work with the Scala clients. */
    @Override
    public ClientType[] getSupportedClients() {
        return new ClientType[] {ClientType.Swing};
    }

    /**
     * @return Array of current Connect Four games in progress.
     */
    @Override
    public GameMetadata[] getGamesInProgress() {
        return games.keySet().stream().map((name) -> {
            return new GameMetadata("ConnectFour", name, games.get(name).getPlayerNames(), true);
        }).toArray(GameMetadata[]::new);
    }

    /**
     * Attempts to create a new Connect Four game and add player to that game.
     * @param playerName Name the player would like to use.
     * @return Future containing the attempt to join the new game.
     */
    @Override
    public Future<RenderingPackage> newGame(String playerName) {
        ConnectFourGame g = new ConnectFourGame(randomName());
        games.put(g.name, g);
        return Future.succeededFuture(g.joinGame(playerName));
    }

    /**
     * @param game Name of specific match player would like to join.
     * @param playerName Name the player would like to use.
     * @return Future containing attempt to join the chosen match.
     */
    @Override
    public Future<RenderingPackage> joinGame(String game, String playerName) {
        ConnectFourGame g = games.get(game);
        return Future.succeededFuture(g.joinGame(playerName));
    }

    /**
     * @param cp CommandPackage containing commands a client would like the server to run.
     * @return Future containing the attempt of the server to run commands.
     */
    @Override
    public Future<RenderingPackage> callGame(CommandPackage cp) {
        ConnectFourGame g = games.get(cp.gameId());
        return Future.succeededFuture(g.runCommands(cp));
    }
}