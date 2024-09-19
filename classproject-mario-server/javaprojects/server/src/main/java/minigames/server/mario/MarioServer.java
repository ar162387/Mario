package minigames.server.mario;

import io.vertx.core.Future;
import minigames.commands.CommandPackage;
import minigames.rendering.GameMetadata;
import minigames.rendering.GameServerDetails;
import minigames.rendering.RenderingPackage;
import minigames.server.ClientType;
import minigames.server.GameServer;
import java.util.List; 
import io.vertx.core.json.JsonObject;
import java.util.HashMap;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Our MarioServer holds MarioGames.
 * When it receives a CommandPackage, it finds the MarioGame and calls it.
 */
public class MarioServer implements GameServer {

    private static final Logger logger = LogManager.getLogger(MarioServer.class);
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
    HashMap<String, MarioGame> games = new HashMap<>();

    @Override
    public GameServerDetails getDetails() {
        return new GameServerDetails("Mario", "The ultimate platform adventure..");
    }

    @Override
    public ClientType[] getSupportedClients() {
        return new ClientType[] { ClientType.Swing, ClientType.Scalajs, ClientType.Scalafx };
    }

    @Override
    public GameMetadata[] getGamesInProgress() {
        return games.keySet().stream().map((name) -> {
            return new GameMetadata("Mario", name, games.get(name).getPlayerNames(), true);
        }).toArray(GameMetadata[]::new);
    }

    @Override
    public Future<RenderingPackage> newGame(String playerName) {
        MarioGame g = new MarioGame(randomName());
        games.put(g.getName(), g);
        System.out.println("New game created with name: " + g.getName());
        return Future.succeededFuture(g.joinGame(playerName));
    }

    @Override
    public Future<RenderingPackage> joinGame(String game, String playerName) {
        MarioGame g = games.get(game);
        System.out.println("Player " + playerName + " joined game: " + game);
        return Future.succeededFuture(g.joinGame(playerName));
    }

//    @Override
//    public Future<RenderingPackage> callGame(CommandPackage cp) {
//        MarioGame g = games.get(cp.gameId());
//        System.out.println("Processing command package for game: " + cp.gameId() + " from player: " + cp.player());
//        return Future.succeededFuture(g.runCommands(cp));
//    }

    @Override
    public Future<RenderingPackage> callGame(CommandPackage commands) {
        String playerName = commands.player();
        List<JsonObject> commandList = commands.commands();

        // Log the received player name and command list
        logger.debug("Received callGame request from player: {}", playerName);
        logger.debug("Command list received: {}", commandList);

        MarioGame g = games.get(commands.gameId());  // Get the game instance

        if (g == null) {
            logger.warn("Game with ID: {} not found for player: {}", commands.gameId(), playerName);
            return Future.failedFuture("Game not found");
        }

        for (JsonObject command : commandList) {
            String commandType = command.getString("type");

            // Log each command type processed
            logger.debug("Processing command of type: {}", commandType);

            switch (commandType) {
                case "move":
                    logger.debug("Processing move command for player: {}", playerName);
                    return Future.succeededFuture(g.runCommands(commands));

                case "UPDATE":
                    logger.debug("Processing update command for player: {}", playerName);
                    return Future.succeededFuture(g.handleUpdate(playerName));

                default:
                    logger.warn("Unknown command type: {}", commandType);
            }
        }

        // Log when no valid command was processed
        logger.warn("No valid commands were processed for player: {}", playerName);
        return Future.failedFuture("No valid command received");
    }

}
