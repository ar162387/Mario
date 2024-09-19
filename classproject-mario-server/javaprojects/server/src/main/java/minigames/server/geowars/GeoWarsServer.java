package minigames.server.geowars;

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
 * Our GeoWarsServer holds GeoWarsGame.
 * When it receives a CommandPackage, it finds the GeoWarsGame and calls it.
 * THIS IS JUST A BASIC SETUP TO GET US SETUP USING MUDDLE AS A TEMPLATE
 */
public class GeoWarsServer implements GameServer {

    static final String chars = "abcdefghijklmopqrstuvwxyz";

    /** A random name. We could do with something more memorable, like Docker has */
    static String randomName() {
        Random r = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 12; i++) {
            sb.append(chars.charAt(r.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /** Holds the games in progress in memory (no db) */
    HashMap<String, GeoWarsGame> games = new HashMap<>();

    @Override
    public GameServerDetails getDetails() {
        return new GameServerDetails("GeoWars", "Geometry Wars aka GeoWars is a top down arcade style shooter game");
    }

    @Override
    public ClientType[] getSupportedClients() {
        return new ClientType[] { ClientType.Swing, ClientType.Scalajs, ClientType.Scalafx };
    }

    @Override
    public GameMetadata[] getGamesInProgress() {
        return new GameMetadata[0];
        // return games.keySet().stream().map((name) -> {
        // return new GameMetadata("GeoWars", name, games.get(name).getPlayerNames(),
        // true);
        // }).toArray(GameMetadata[]::new);
    }

    @Override
    public Future<RenderingPackage> newGame(String playerName) {
        GeoWarsGame g = new GeoWarsGame(randomName());
        games.put(g.name, g);
        return Future.succeededFuture(g.joinGame(playerName));
    }

    @Override
    public Future<RenderingPackage> joinGame(String game, String playerName) {
        GeoWarsGame g = games.get(game);
        return Future.succeededFuture(g.joinGame(playerName));
    }

    @Override
    public Future<RenderingPackage> callGame(CommandPackage cp) {
        GeoWarsGame g = games.get(cp.gameId());
        return Future.succeededFuture(g.runCommands(cp));
    }

}
