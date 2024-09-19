package minigames.client.bomberman;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        Game game = new Game(stage);
        game.start();
        stage.setTitle("Bomberman Game");

        Scene scene = stage.getScene();

        // Global toggle for Debugging
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.F12) {
                DebugManager.getInstance().toggleDebugMode();
            }
        });

        stage.show();


    }

    public static void main() {
        launch();
    }
}
