package minigames.server.geowars;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import minigames.commands.CommandPackage;
import minigames.rendering.*;
import minigames.rendering.NativeCommands.LoadClient;
import minigames.rendering.NativeCommands.QuitToMenu;

/**
 * Represents an actual GeoWars game in progress
 * THIS IS JUST A BASIC SETUP TO GET US SETUP USING MUDDLE AS A TEMPLATE
 */
public class GeoWarsGame {

    /** A logger for logging output */
    private static final Logger logger = LogManager.getLogger(GeoWarsGame.class);

    record GeoWarsPlayer(
            String name) {
    }

    /** Uniquely identifies this game */
    String name;

    public GeoWarsGame(String name) {
        this.name = name;
    }

    HashMap<String, GeoWarsPlayer> players = new HashMap<>();

    /** The players currently playing this game */
    public String[] getPlayerNames() {
        return players.keySet().toArray(String[]::new);
    }

    /** Metadata for this game */
    public GameMetadata gameMetadata() {
        return new GameMetadata("GeoWars", name, getPlayerNames(), true);
    }

    public RenderingPackage runCommands(CommandPackage cp) {
        logger.info("Received command package {}", cp);
        GameMetadata metadata = gameMetadata();
        ArrayList<JsonObject> renderingCommands = new ArrayList<>();

        for (JsonObject command : cp.commands()) {
            // Handle a qtm command
            Optional<QuitToMenu> qtm = QuitToMenu.tryParsing(command);
            if (qtm.isPresent()) {
                renderingCommands.add(qtm.get().toJson());
            }
        }

        return new RenderingPackage(metadata, renderingCommands);
    }

    /** Joins this game */
    public RenderingPackage joinGame(String playerName) {
        if (players.containsKey(playerName)) {
            return new RenderingPackage(
                    gameMetadata(),
                    Arrays.stream(new RenderingCommand[] {
                            new NativeCommands.ShowMenuError("That name's not available")
                    }).map((r) -> r.toJson()).toList());
        } else {
            GeoWarsPlayer p = new GeoWarsPlayer(playerName);
            players.put(playerName, p);
            ArrayList<JsonObject> renderingCommands = new ArrayList<>();
            renderingCommands.add(new LoadClient("GeoWars", "GWServer", name, playerName).toJson());
            return new RenderingPackage(gameMetadata(), renderingCommands);
        }
    }
}
