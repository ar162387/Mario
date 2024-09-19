package minigames.client.bomberman;

import javafx.application.Platform;
import minigames.client.GameClient;
import minigames.client.MinigameNetworkClient;
import io.vertx.core.json.JsonObject;

import minigames.rendering.GameMetadata;

public class Bomberman implements GameClient {
    private MinigameNetworkClient mnClient;
    private GameMetadata game;
    private String player;

    public Bomberman(){}

    @Override
    public void load(MinigameNetworkClient mnClient, GameMetadata game, String player) {
        this.mnClient = mnClient;
        this.game = game;
        this.player = player;

        // Ensure JavaFX is initialized
        if (!Platform.isFxApplicationThread()) {
            // Launch JavaFX application if not already running
            new Thread(() -> {
                javafx.application.Application.launch(Main.class);
            }).start();
        } else {
            // If JavaFX is already initialized, run the game UI code
            Platform.runLater(() -> Main.main());
        }
    }

    // TODO: starter code
    @Override
    public void execute(GameMetadata game, JsonObject command){};



    // TODO: starter code
    @Override
    public void closeGame(){};
    
}
