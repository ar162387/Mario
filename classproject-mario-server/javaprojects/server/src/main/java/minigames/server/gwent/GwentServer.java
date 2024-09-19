package minigames.server.gwent;

import io.vertx.core.Future;
import minigames.commands.CommandPackage;
import minigames.rendering.GameMetadata;
import minigames.rendering.GameServerDetails;
import minigames.rendering.RenderingPackage;
import minigames.server.ClientType;
import minigames.server.GameServer;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

/**
 * Our GwentServer holds GwentGames.
 * When it receives a CommandPackage, it finds the GwentGame and calls it.
 */
public class GwentServer implements GameServer {

    /** Holds the games in progress in memory (no db) */
    HashMap<String, GwentGame> games = new HashMap<>();

    @Override
    public GameServerDetails getDetails() {
        return new GameServerDetails("Gwent", "A strategic turn-based card game where players control different kingdoms.");
    }

    @Override
    public ClientType[] getSupportedClients() {
        return new ClientType[] { ClientType.Swing, ClientType.Scalajs, ClientType.Scalafx };
    }

    @Override
    public GameMetadata[] getGamesInProgress() {
        return games.keySet().stream().map((name) -> {
            return new GameMetadata("Gwent", name, games.get(name).getPlayerNames(), true);
        }).toArray(GameMetadata[]::new);
    }

    @Override
    public Future<RenderingPackage> newGame(String playerName) {

        // Create UUID to serve as unique lobby identifier
        UUID uuid = UUID.randomUUID();
        GwentGame g = new GwentGame(uuid.toString());
        games.put(g.getServerName(), g);

        return Future.succeededFuture(g.joinGame(playerName));
    }

    @Override
    public Future<RenderingPackage> joinGame(String game, String playerName) {
        GwentGame g = games.get(game);
        return Future.succeededFuture(g.joinGame(playerName));
    }

    @Override
    public Future<RenderingPackage> callGame(CommandPackage cp) {
        GwentGame g = games.get(cp.gameId());
        return Future.succeededFuture(g.runCommands(cp));
    }
    
}
