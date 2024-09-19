package minigames.client.gwent;

import io.vertx.core.json.JsonObject;
import minigames.client.GameClient;
import minigames.client.MinigameNetworkClient;
import minigames.commands.CommandPackage;
import minigames.rendering.GameMetadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.SwingUtilities;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Gwent is the entry point for the game.
 */
public class Gwent implements GameClient {

    private static final Logger logger = LogManager.getLogger(Gwent.class);
    private final Map<String, Consumer<JsonObject>> commandHandlers = new HashMap<>();

    private GameController gameController;

    // Client and server information
    private MinigameNetworkClient client;
    private GameMetadata gameMetadata;

    // Userdata
    private String username;
    /**
     * Constructor for Gwent.
     */
    public Gwent() {
        commandHandlers.put("cardPlayed", this::cardPlayed);
        commandHandlers.put("addCardToHand", this::addCardToHand);
        commandHandlers.put("addCardToDeck", this::addCardToDeck);
        commandHandlers.put("forfeit", this::playerForfeited);
        commandHandlers.put("message", this::messageReceived);
    }

    /**
     * @param mnClient
     * @param game
     * @param player
     */
    @Override
    public void load(MinigameNetworkClient mnClient, GameMetadata game, String player) {
        this.client = mnClient;
        this.gameMetadata = game;
        this.username = player;

        // invoke the main menu
        logger.info("Launching Gwent Main Menu...");

        // Initialise the game controller
        SwingUtilities.invokeLater(() -> {
            gameController = new GameController(mnClient);
        });
    }

    /**
     * Closes game
     */
    @Override
    public void closeGame() {
        logger.info("Closing Gwent game");
        forfeit();
    }

    /**
     * Handles responses from server.
     * @param game game metadata
     * @param response response from server
     */
    public void execute(GameMetadata game, JsonObject response) {
        this.gameMetadata = game;
        String commandString = response.getString("command");

        Consumer<JsonObject> commandAction = commandHandlers.get(commandString);
        if (commandAction != null) commandAction.accept(response);
        else logger.warn("Unknown command \"" + commandString + "\" received from server!");
    }


    //<editor-fold desc="Client requests">
    /**
     * Sends request to play card in players hand
     * @param cardGUID Hand GUID for card
     */
    public void playCard(int cardGUID) {

        // Construct command packet
        JsonObject command = new JsonObject();
        command.put("command", "playCard");
        command.put("guid", cardGUID);
        // command.put("targets", targets); // TODO: Implement once card abilities are sorted on server

        client.send(new CommandPackage(gameMetadata.gameServer(), gameMetadata.name(), username, Collections.singletonList(command)));
    }

    /**
     * Sends request to server to pass current turn.
     */
    public void passTurn() {

        // Construct command packet
        JsonObject command = new JsonObject();
        command.put("command", "passTurn");

        client.send(new CommandPackage(gameMetadata.gameServer(), gameMetadata.name(), username, Collections.singletonList(command)));
    }

    /**
     * Notifies the server that player has left/forfeited game
     */
    public void forfeit() {
        // Construct command packet
        JsonObject command = new JsonObject();
        command.put("command", "forfeit");

        client.send(new CommandPackage(gameMetadata.gameServer(), gameMetadata.name(), username, Collections.singletonList(command)));
    }

    /**
     * Notifies the server that player has left/forfeited game
     */
    public void sendMessage(String message) {
        // Construct command packet
        JsonObject command = new JsonObject();
        command.put("command", "message");
        command.put("message", message);

        client.send(new CommandPackage(gameMetadata.gameServer(), gameMetadata.name(), username, Collections.singletonList(command)));
    }
    //</editor-fold>

    //<editor-fold desc="Server responses">
    /**
     * RPC from server, notifies client of cardPlayed
     * @param response response from server
     */
    private void cardPlayed(JsonObject response) {
        // TODO: Handle response
    }

    /**
     * RPC from server, notifies client to add card to hand
     * @param response response from server
     */
    private void addCardToHand(JsonObject response) {
        // TODO: Handle response
    }

    /**
     * RPC from server, notifies client to add card to deck
     * @param response response from server
     */
    private void addCardToDeck(JsonObject response) {
        // TODO: Handle response
    }

    /**
     * RPC from server, notifies client that player forfeited
     * @param response response from server
     */
    private void playerForfeited(JsonObject response) {
        // TODO: Handle response
    }

    /**
     * RPC from server, notifies client that player forfeited
     * @param response response from server
     */
    private void messageReceived(JsonObject response) {
        // TODO: Handle response
        logger.info("Message received: " + response.getString("message"));
    }
    //</editor-fold>
}
