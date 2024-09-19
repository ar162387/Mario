package minigames.server.minesweeper;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.json.JsonObject;
import minigames.commands.CommandPackage;
import minigames.rendering.*;
import minigames.rendering.NativeCommands.LoadClient;

/**
 * Represents an actual Minesweeper game in progress
 */
public class MinesweeperGame {

    /** A logger for logging output */
    private static final Logger logger = LogManager.getLogger(MinesweeperGame.class);

    String playerName;
    public JsonObject data;

    /** Uniquely identifies this game */
    String name;
 
    public MinesweeperGame(String name, String playerName, JsonObject data) {
        this.name = name;
        this.playerName = playerName;
        if (data != null) {
            // Create an independent copy
            this.data = new JsonObject(data.toString());
        }
    }

    public String[] getPlayerNames() {
        return new String[] { playerName };
    }

    /** Metadata for this game */
    public GameMetadata gameMetadata() {
        return new GameMetadata("Minesweeper", name, getPlayerNames(), true);
    }

    public RenderingPackage runCommands(CommandPackage cp) {   
        logger.info("Received command package {}", cp);     
        // MinesweeperPlayer p = gameData.get(cp.player());
        ArrayList<JsonObject> renderingCommands = new ArrayList<>();

        for (JsonObject command : cp.commands()) {
            switch (command.getString("command")) {
                case "loadGame":
                    renderingCommands.add(new JsonObject().put("command", "loadGameResponse"));
                    break;
                case "saveGame":
                    renderingCommands.add(new JsonObject().put("command", "saveGameResponse"));
                    break;
            }
        }

        return new RenderingPackage(this.gameMetadata(), renderingCommands);
    }

    /** Joins this game */
    public RenderingPackage joinGame(String playerName) {
        this.playerName = playerName;

        ArrayList<JsonObject> renderingCommands = new ArrayList<>();
        renderingCommands.add(new LoadClient("Minesweeper", "Minesweeper", name, playerName).toJson());
        if (data != null) {
            JsonObject obj = new JsonObject();
            obj.put("command", "loadGame");
            obj.put("payload", data);
            renderingCommands.add(obj);
            logger.info("@@@@@@@@@ joinGame", obj);
        }

        return new RenderingPackage(gameMetadata(), renderingCommands);
    }
    
}
