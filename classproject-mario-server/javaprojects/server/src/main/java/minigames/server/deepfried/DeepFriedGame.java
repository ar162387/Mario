package minigames.server.deepfried;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.json.JsonObject;
import minigames.commands.CommandPackage;
import minigames.rendering.GameMetadata;
import minigames.rendering.NativeCommands;
import minigames.rendering.NativeCommands.LoadClient;
import minigames.rendering.RenderingCommand;
import minigames.rendering.RenderingPackage;


public class DeepFriedGame {
     /** A logger for logging output */
    private static final Logger logger = LogManager.getLogger(DeepFriedGame.class);

    record DeepFriedPlayer(
        String name, // identifies the player
        int x, int y, // the player's current position
        int score // the player's current score
        ) {
    }

    /** Uniquely identifies this game */
    String name;
    

    public DeepFriedGame(String name) {
        this.name = name;
               
    }

    // HashMap<String, DeepFriedPlayer> players = new HashMap<>();
    HashMap<String, String> players = new HashMap<>();

    /** The players currently playing this game */
    public String[] getPlayerNames() {
        return players.keySet().toArray(String[]::new);
    }

    /** Metadata for this game */
    public GameMetadata gameMetadata() {
        return new GameMetadata("DeepFried", name, getPlayerNames(), true);
    }


    public RenderingPackage runCommands(CommandPackage cp) {   
        logger.info("Received command package {}", cp);     
        // FIXME: Need to actually run the commands!
        // i.e. this is where the logic goes to turn commands from the client into game instructions/state change        
        ArrayList<JsonObject> renderingCommands = new ArrayList<>();
        System.out.println();
        // Return empty canvas
        // for (JsonObject command : cp.commands()) {
        //     renderingCommands.add(command);
        // }
        renderingCommands.add(cp.commands().getFirst());
        
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
            // DeepFriedPlayer p = new DeepFriedPlayer(playerName, 0, 0, 0);
            players.put(playerName, playerName);

            ArrayList<JsonObject> renderingCommands = new ArrayList<>();
            renderingCommands.add(new LoadClient("DeepFried", "DeepFried", name, playerName).toJson());

            return new RenderingPackage(gameMetadata(), renderingCommands);
        }

    }
}
