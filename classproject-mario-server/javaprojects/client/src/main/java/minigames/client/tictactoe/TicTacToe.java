package minigames.client.tictactoe;
import io.vertx.core.json.JsonObject;
import minigames.client.GameClient;
import minigames.client.MinigameNetworkClient;
import minigames.rendering.GameMetadata;
import java.awt.EventQueue;



public class TicTacToe implements GameClient {

    private MinigameNetworkClient mnClient;
    private GameMetadata gm;
    private String player;
    private TicTacToeUI ui;


    public TicTacToe() {
        // Constructor logic if needed
    }

    @Override
    public void load(MinigameNetworkClient mnClient, GameMetadata game, String player) {
        this.mnClient = mnClient;
        this.gm = game;
        this.player = player;

        EventQueue.invokeLater(() -> {
            // Initialize and show the game UI
            ui = new TicTacToeUI(this);
            ui.setVisible(true);
        });
    }


    @Override
    public void execute(GameMetadata game, JsonObject command) {

    }


    public void closeGame() {
      
    }



}
