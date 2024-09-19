package minigames.server.bomberman;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.json.JsonObject;
import minigames.commands.CommandPackage;
import minigames.rendering.*;
import minigames.rendering.NativeCommands.LoadClient;

/**
 * Represents an actual Bomberman game in progress
 * starter code for now based on muddle to get us up and running
 */
public class BombermanGame {

    /** A logger for logging output */
    private static final Logger logger = LogManager.getLogger(BombermanGame.class);

    /** Uniquely identifies this game */
    String name;
    String playerName;

    public BombermanGame(String name, String playerName) {
        this.name = name;
        this.playerName = playerName;
    }
    
    HashMap<String, String> players = new HashMap<>();

    /** The players currently playing this game */
    public String[] getPlayerNames() {
        return players.keySet().toArray(String[]::new);
    }

    /** Metadata for this game */
    public GameMetadata gameMetadata() {
        return new GameMetadata("Bomberman", name, getPlayerNames(), true);
    }

    public RenderingPackage runCommands(CommandPackage cp) {   
        logger.info("Received command package {}", cp);
        ArrayList<JsonObject> renderingCommands = new ArrayList<>();
        return new RenderingPackage(this.gameMetadata(), renderingCommands);
    }

    /** Joins this game */
    public RenderingPackage joinGame(String playerName) {
        if (players.containsKey(playerName)) {
            return new RenderingPackage(
                gameMetadata(),
                Arrays.stream(new RenderingCommand[] {
                    new NativeCommands.ShowMenuError("That name's not available")
                }).map((r) -> r.toJson()).toList()
            );
        } else {
            players.put(name, playerName);
            ArrayList<JsonObject> renderingCommands = new ArrayList<>();
            renderingCommands.add(new LoadClient("Bomberman", "Bomberman", name, playerName).toJson());
            return new RenderingPackage(gameMetadata(), renderingCommands);
        }
    }
}