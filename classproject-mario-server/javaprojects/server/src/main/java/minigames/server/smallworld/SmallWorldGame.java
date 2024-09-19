package minigames.server.smallworld;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.json.JsonObject;
import minigames.commands.CommandPackage;
import minigames.rendering.GameMetadata;
import minigames.rendering.NativeCommands;
import minigames.rendering.NativeCommands.LoadClient;
import minigames.rendering.RenderingCommand;
import minigames.rendering.RenderingPackage;
import minigames.smallworld.WorldMap;

/**
 * Represents an actual SmallWorld game in progress
 */
public class SmallWorldGame {

    private MapGenerator mapGenerator;
    private WorldMap world;

    /** A logger for logging output */
    private static final Logger logger = LogManager.getLogger(SmallWorldGame.class);

    record SmallWorldPlayer(
        String name,
        int x, int y
    ) {    
    }

    /** Uniquely identifies this game */
    String name;

    public SmallWorldGame(String name) {
        this.name = name;
        //char[][] generatedMap = mapGenerator.generateMap(); // Generate the map as char[][]
        this.world = new WorldMap(250,30); // Initialize the world
        this.mapGenerator = new MapGenerator(this.world); // Example dimensions
        this.world = mapGenerator.generateMap();
    }

    HashMap<String, SmallWorldPlayer> players = new HashMap<>();

    /** The players currently playing this game */
    public String[] getPlayerNames() {
        return players.keySet().toArray(String[]::new);
    }

    /** Metadata for this game */
    public GameMetadata gameMetadata() {
        return new GameMetadata("SmallWorld", name, getPlayerNames(), true);
    }


    /**
     * Modify the world map when receiving an "updateTile" command from the client
     * 
     * @param jason
     */
    private void processTileUpdate(JsonObject jason) {
        //FIXME: error handling
        
        int x = jason.getInteger("xPos");
        int y = jason.getInteger("yPos");
        char ascii = jason.getString("tileAscii").charAt(0);
        
        world.placeTileChar(x, y, ascii);
    }

    private void processItemCollect(JsonObject jason) {
        //FIXME: error handling
        
        int x = jason.getInteger("xPos");
        int y = jason.getInteger("yPos");
        
        world.itemSpotUpdate(x, y);
    }

    private JsonObject getWorldState() {
        return new JsonObject()
            .put("command", "getWorld")
            .put("worldMapTiles", world.getTileStrings())
            .put("worldMapItems", world.getItemStrings());
    }

    public RenderingPackage runCommands(CommandPackage cp) {
        logger.info("Received command package {}", cp);
    
        ArrayList<JsonObject> renderingCommands = new ArrayList<>();
        
        for (JsonObject command : cp.commands()) {

            if(command.getString("nativeCommand") != null) {
                logger.info("Handling QuitToMenu command");
                renderingCommands.add((new NativeCommands.QuitToMenu()).toJson());                
            }
            String cmdType = Objects.requireNonNullElse(command.getString("command"), "no valid key");
            
            switch (cmdType) {
                case "client.quitToMGNMenu":
                    logger.info("Handling QuitToMenu command");
                    renderingCommands.add((new NativeCommands.QuitToMenu()).toJson());
                    break;
                case "getWorld":
                    logger.info("Handling getWorld command");
                    renderingCommands.add(getWorldState());
                    break;
                case "updateTile": {
                    processTileUpdate(command);
                    renderingCommands.add(getWorldState());
                    break;
                }
                case "collectItem": {
                    processItemCollect(command);
                    renderingCommands.add(getWorldState());
                    break;
                }
                default: logger.warn("Received unknown command \"" + cmdType + "\" from " + cp.player());
            }
        }
    
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

            ArrayList<JsonObject> renderingCommands = new ArrayList<>();
            renderingCommands.add(new LoadClient("SmallWorld", "SmallWorld", name, playerName).toJson());

            return new RenderingPackage(gameMetadata(), renderingCommands);
        }

    }

    
    
}


