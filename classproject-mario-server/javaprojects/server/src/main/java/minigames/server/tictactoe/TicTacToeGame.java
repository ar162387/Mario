package minigames.server.tictactoe;

import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.vertx.core.json.JsonObject;
import minigames.commands.CommandPackage;
import minigames.rendering.*;
import minigames.rendering.NativeCommands.LoadClient;

public class TicTacToeGame {

    private static final Logger logger = LogManager.getLogger(TicTacToeGame.class);

    String name;
    String playerName;
    private char[][] board = new char[3][3];
    private char currentPlayer = 'X';
    String gameMode;

    public TicTacToeGame(String name, String playerName, String gameMode) {
        this.name = name;
        this.playerName = playerName;
        this.gameMode = gameMode;
        for (int i = 0; i < 3; i++) {
            Arrays.fill(board[i], ' ');
        }
    }

    HashMap<String, String> players = new HashMap<>();

    public String[] getPlayerNames() {
        return players.keySet().toArray(String[]::new);
    }

    public GameMetadata gameMetadata() {
        return new GameMetadata("TicTacToe", name, getPlayerNames(), true);
    }

    public RenderingPackage runCommands(CommandPackage cp) {
        logger.info("Received command package {}", cp);
        return new RenderingPackage(this.gameMetadata(), Collections.emptyList());
    }

    public RenderingPackage joinGame(String playerName) {
        if (players.containsKey(playerName)) {
            return new RenderingPackage(
                    gameMetadata(),
                    Arrays.stream(new RenderingCommand[]{
                            new NativeCommands.ShowMenuError("That name's not available")
                    }).map(RenderingCommand::toJson).toList()
            );
        } else {
            players.put(playerName, playerName);

            ArrayList<JsonObject> renderingCommands = new ArrayList<>();
            renderingCommands.add(new LoadClient("TicTacToe", "TicTacToe", name, playerName).toJson());

            return new RenderingPackage(gameMetadata(), renderingCommands);
        }
    }
}