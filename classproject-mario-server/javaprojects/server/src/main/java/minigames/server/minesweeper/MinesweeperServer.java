package minigames.server.minesweeper;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import minigames.commands.CommandPackage;
import minigames.rendering.GameMetadata;
import minigames.rendering.GameServerDetails;
import minigames.rendering.RenderingPackage;
import minigames.server.ClientType;
import minigames.server.GameServer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Random;

/**
 * Our MinesweeperServer holds MinesweeperGames. 
 * When it receives a CommandPackage, it finds the MinesweeperGame and calls it.
 */
public class MinesweeperServer implements GameServer {

    static String createName() {
        // Define the format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");
        return "Save-" + LocalDateTime.now().format(formatter);
    }

    /** Holds the saved games in memory (no db) */
    HashMap<String, MinesweeperGame> games = new HashMap<>();

    @Override
    public GameServerDetails getDetails() {
        return new GameServerDetails("Minesweeper", "Be careful, one mistake will cost you life");
    }

    @Override
    public ClientType[] getSupportedClients() {
        return new ClientType[] { ClientType.Swing, ClientType.Scalajs, ClientType.Scalafx };
    }

    @Override
    public GameMetadata[] getGamesInProgress() {
        return games.keySet().stream().map((name) -> {
            return new GameMetadata("Minesweeper", name, games.get(name).getPlayerNames(), true);
        }).toArray(GameMetadata[]::new);
    }

    @Override
    public Future<RenderingPackage> newGame(String playerName) {
        MinesweeperGame g = new MinesweeperGame("NEWGAME", playerName, null);
        // When new game is created a record is not added to games list.
        // It is done on "saveGame" command       
        return Future.succeededFuture(g.joinGame(playerName));
    }

    @Override
    public Future<RenderingPackage> joinGame(String game, String playerName) {
        MinesweeperGame g = games.get(game);
        // After game is loaded it has to be removed from the list
        games.remove(game);
        return Future.succeededFuture(g.joinGame(playerName));
    }

    @Override
    public Future<RenderingPackage> callGame(CommandPackage cp) {
        String gameId = cp.gameId();
        for (JsonObject command : cp.commands()) {
            switch (command.getString("command")) {
                case "loadGame":
                    break;
                case "saveGame":
                    gameId = createName();
                    MinesweeperGame gs = new MinesweeperGame(gameId, cp.player(), command.getJsonObject("payload"));
                    games.put(gameId, gs);
                    break;
            }
        }

        MinesweeperGame g = games.get(gameId);
        return Future.succeededFuture(g.runCommands(cp));
    }
    
}
