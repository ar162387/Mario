package minigames.server.gwent;

import io.vertx.core.json.JsonObject;
import minigames.commands.CommandPackage;
import minigames.rendering.GameMetadata;
import minigames.rendering.NativeCommands;
import minigames.rendering.NativeCommands.LoadClient;
import minigames.rendering.RenderingCommand;
import minigames.rendering.RenderingPackage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * Represents an actual Gwent game in progress
 */
public class GwentGame {

    /** A logger for logging output */
    private static final Logger logger = LogManager.getLogger(GwentGame.class);
    private final Map<String, BiFunction<Player, JsonObject, JsonObject>> commandHandlers = new HashMap<>();

    private record Player(
            UUID id,
            String username
            // TODO: Deck Data
            // TODO: Hand Data
            // TODO: Field Data?
    ) {
    }

    /** Lobby data */
    private final String name;
    private boolean isJoinable;
    private static int maxLobbySize = 2;

    /** Lists players within game **/
    private List<Player> players = new ArrayList<>();

    public GwentGame(String name) {
        this(name, true);
    }

    public GwentGame(String name, boolean isJoinable) {
        this.name = name;
        this.isJoinable = isJoinable;

        commandHandlers.put("playCard", this::handlePlayCard);
        commandHandlers.put("passTurn", this::handlePassTurn);
        commandHandlers.put("forfeit", this::handleForfeit);
        commandHandlers.put("message", this::handleMessage);
    }

    public RenderingPackage runCommands(CommandPackage cp) {   
        logger.info("Received command package {}", cp);

        // Get player
        Player player = players.stream()
                .filter(p -> p.username().equals(cp.player()))
                .findFirst()
                .orElse(null);

        // Ensure valid player
        if (player == null) {
            return new RenderingPackage(
                    gameMetadata(),
                    List.of(new NativeCommands.ShowMenuError("Unable to find username " + cp.player()).toJson())
            );
        }

        ArrayList<JsonObject> serverResponses = new ArrayList<>();

        // Handle commands
        for (JsonObject command : cp.commands()) {
            String commandType = command.getString("command");
            BiFunction<Player, JsonObject, JsonObject> handler = commandHandlers.get(commandType);

            if (handler != null) {
                serverResponses.add(handler.apply(player, command));
            } else {
                serverResponses.add(new JsonObject().put("error", "Unknown command: " + commandType));
                logger.warn("Unknown command \"" + commandType + "\" received from: " + cp.player());
            }
        }

        return new RenderingPackage(this.gameMetadata(), serverResponses);
    }

    /** Joins this game */
    public RenderingPackage joinGame(String username) {
        // Is game joinable
        if (!isJoinable) {
            return new RenderingPackage(
                    gameMetadata(),
                    Arrays.stream(new RenderingCommand[] {
                            new NativeCommands.ShowMenuError("This game is not join able!")
                    }).map((r) -> r.toJson()).toList()
            );
        // Check if game already has player with the same name.
        } else if (players.stream().anyMatch(player -> player.username().equals(username))) {
            return new RenderingPackage(
                    gameMetadata(),
                    Arrays.stream(new RenderingCommand[] {
                            new NativeCommands.ShowMenuError("That name is already taken!")
                    }).map((r) -> r.toJson()).toList()
            );
        } else {
            players.add(new Player(UUID.randomUUID(), username));

            // Update isJoinable status when lobby full.
            if (players.size() >= maxLobbySize) isJoinable = false;

            ArrayList<JsonObject> renderingCommands = new ArrayList<>();
            renderingCommands.add(new LoadClient("Gwent", "GwentServer", name, username).toJson());
            renderingCommands.add(new JsonObject().put("command", "clearText"));
            renderingCommands.add(new JsonObject().put("command", "message").put("message", "Hello World!"));

            return new RenderingPackage(gameMetadata(), renderingCommands);
        }
    }

    //<editor-fold desc="Client request handling">
    /**
     * @param player
     * @param command
     * @return
     */
    private JsonObject handlePlayCard(Player player, JsonObject command) {

        // Do validation for move here

        // Let clients know of move
        JsonObject response = new JsonObject();
        response.put("command", "cardPlayed");
        response.put("cardUUID", "TODO"); // TODO: Card GUIDS
        response.put("player", player.username);
        response.put("position", "attack");

        return response;
    }

    /**
     *
     */
    private JsonObject handlePassTurn(Player player, JsonObject command) {

        // TODO: Validation
        // TODO: Logic

        // Format response
        JsonObject response = new JsonObject();
        response.put("command", "cardPlayed");

        return response;
    }

    /**
     *
     */
    private JsonObject handleForfeit(Player player, JsonObject command) {

        // TODO: Validation
        // TODO: Logic

        // Format response
        JsonObject response = new JsonObject();
        response.put("command", "cardPlayed");

        return response;
    }

    /**
     *
     */
    private JsonObject handleMessage(Player player, JsonObject command) {

        // TODO: Validation
        // TODO: Logic

        // Format response
        JsonObject response = new JsonObject();
        response.put("command", "cardPlayed");
        response.put("message", "message received");

        return response;
    }

    //</editor-fold>

    //<editor-fold desc="Getters">
    /** The name of this game */
    public String getServerName() {
        return name;
    }

    /** The players currently playing this game */
    public String[] getPlayerNames() {
        return players.stream()
                .map(Player::username)
                .toArray(String[]::new);
    }

    /** Metadata for this game */
    public GameMetadata gameMetadata() {
        return new GameMetadata("Gwent", name, getPlayerNames(), isJoinable);
    }
    //</editor-fold>
}
