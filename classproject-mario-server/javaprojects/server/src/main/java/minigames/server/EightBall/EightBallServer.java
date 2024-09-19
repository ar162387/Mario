package minigames.server.EightBall;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import minigames.server.ClientType;
import minigames.server.GameServer;
import io.vertx.core.Future;
import minigames.commands.CommandPackage;
import minigames.rendering.GameMetadata;
import minigames.rendering.GameServerDetails;
import minigames.rendering.RenderingPackage;

/**
 * EightBallServer is the "backend" for the server-side implementation of the EightBall game.
 * 
 * It is exclusively called through the minigame network and is responsible for:
 * - Creating new games
 * - Joining games
 * - Passing commands to a game server
 * 
 * It is also responsible for keeping track of all games in progress.
 */
public class EightBallServer implements GameServer{
    // Logging
    Logger logger = LogManager.getLogger(EightBallServer.class);
    //Stores games in progress
    HashMap<String, EightBallGame> games = new HashMap<>();

    /**
     * Just used for the Network menu description
     */
    @Override
    public GameServerDetails getDetails(){
        return new GameServerDetails("EightBall", "A simple game of Eight Ball pool.");
    }

    /**
     * Returns the supported clients for this game server.
     * Assuming we only support Swing as we are only using the Swing client.
     */
    @Override
    public ClientType[] getSupportedClients(){
        return new ClientType[] { ClientType.Swing};
    }

    /**
     * Returns the games in progress as an array of GameMetadata objects.
     * This is used to display the games in progress in the Network menu.
     */
    @Override
    public GameMetadata[] getGamesInProgress(){
        // returns all games currently stored in the hashmap
        return games.keySet().stream().map((name) -> {
            return games.get(name).getGameMetaData();
        }).toArray(GameMetadata[]::new);
    }

    /**
     * Creates a new game with the given player name.
     * Attempts to join a game if the player name is already 
     * in use (i.e. player left game but other person still there)
     */
    @Override
    public Future<RenderingPackage> newGame(String playerName){
        if (games.containsKey(playerName)) {
            logger.info("Player " + playerName + " is rejoining game " + playerName);
            return Future.succeededFuture(games.get(playerName).joinGame(playerName));
        } else {
            logger.info("Player " + playerName + " is creating new game " + playerName);
            EightBallGame game = new EightBallGame(playerName);
            games.put(game.getServerName(), game);
            return Future.succeededFuture(game.joinGame(playerName));
        }
    };

    /**
     * Joins a game with the given player name.
     */
    @Override
    public Future<RenderingPackage> joinGame(String game, String playerName){
        logger.info("Player " + playerName + " is joining game " + game);
        EightBallGame server = games.get(game);
        return Future.succeededFuture(server.joinGame(playerName));
    }

    /**
     * Passes a commandpackage to the server. Game server 
     * is pulled from the CommandPackage.
     */
    @Override
    public Future<RenderingPackage> callGame(CommandPackage commands){
        logger.info("Received commands for game " + commands.gameId());
        EightBallGame game = games.get(commands.gameId());
        return Future.succeededFuture(game.runCommands(commands));
    }
}
