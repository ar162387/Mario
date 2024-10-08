package minigames.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

import minigames.server.deepfried.DeepFriedServer;
import minigames.server.gwent.GwentServer;
import minigames.server.hangman.HangmanServer;
import minigames.server.geowars.GeoWarsServer;
import minigames.server.muddle.MuddleServer;
import minigames.server.RodentsRevenge.RodentsRevengeServer;
import minigames.server.bomberman.BombermanServer;
import minigames.server.EightBall.EightBallServer;
import minigames.server.connectfour.ConnectFourServer;
//import minigames.server.snake.SnakeServer;
import minigames.server.smallworld.SmallWorldServer;
import minigames.server.minesweeper.MinesweeperServer;
import minigames.server.mario.MarioServer;
import io.vertx.core.Launcher;
import minigames.server.tictactoe.TicTacToeServer;

import minigames.server.email.MailSender;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A class for starting up the game server.
 * 
 * This is shaped a little by things that Vertx needs to start itself up
 */
public class Main extends AbstractVerticle {

    /** A logger for logging output */
    private static final Logger logger = LogManager.getLogger(Main.class);

    /** 
     * Port to start the server on.
     * This is a little hacky, but when we run the server, we just put the port argument into this static field
     * so that the GameServer's start method can pick it up later
     */
    public static int port = 8080;

    /** 
     * The games that are available for each client. Static so that game servers and Main gan register them
     * without needing to worry about whether the server has started yet.
     */
    public static final GameRegistry gameRegistry = new GameRegistry();

    /**
     * A place for groups to put code that registers their GameServer with the GameRegistry, etc.
     */
    private static void doWiring() {
        // Register our first demo game
        gameRegistry.registerGameServer("Bomberman",new BombermanServer());
        gameRegistry.registerGameServer("EightBall", new EightBallServer());
        gameRegistry.registerGameServer("Muddle", new MuddleServer());
        gameRegistry.registerGameServer("DeepFried", new DeepFriedServer());
        gameRegistry.registerGameServer("GeoWars", new GeoWarsServer());
        gameRegistry.registerGameServer("RodentsRevenge", new RodentsRevengeServer());
        //gameRegistry.registerGameServer("Snake", new SnakeServer());
        gameRegistry.registerGameServer("SmallWorld", new SmallWorldServer());
        gameRegistry.registerGameServer("Gwent", new GwentServer());
        gameRegistry.registerGameServer("Minesweeper", new MinesweeperServer());
        gameRegistry.registerGameServer("ConnectFour", new ConnectFourServer());
        gameRegistry.registerGameServer("Mario", new MarioServer());

    }

    public static void main(String... args) {
        if (args.length > 0) {
            try {
                int p = Integer.parseInt(args[0]);                
                if (p >= 1024 && p <= 49151) {
                    port = p;
                } else {
                    logger.error("Port {} is outside the user port range of 1024 to 49151", args[0]);    
                }
            } catch (NumberFormatException ex) {
                logger.error("Port {} could not be parsed as a number", args[0]);
            }
        }

        // Register games and services
        doWiring();

        // Ask the Vertx launcher to launch our "Verticle".
        // This will cause Vert.x to start itself up, and then create a Main object and call our Main::start method
        logger.info("About to launch the server");
        Launcher.executeCommand("run", "minigames.server.Main");
    }

    MinigameNetworkServer gameServer;

    /**
     * The start method is called by vertx to initialise this Verticle.
     */
    @Override
    public void start(Promise<Void> promise) {
        logger.info("Our Verticle is being started by Vert.x");

        // Create and start the MinigameNetworkServer
        gameServer = new MinigameNetworkServer(vertx);
        gameServer.start(port);


        // Start mail server
        vertx.deployVerticle(new MailSender(), res -> {
            if (res.succeeded()) {
                logger.info("MailSender verticle deployed successfully");
            } else {
                logger.error("MailSender verticle deployment failed: " + res.cause());
            }
        });

        promise.complete();
    }

}