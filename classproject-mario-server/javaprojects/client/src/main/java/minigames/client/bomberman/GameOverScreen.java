package minigames.client.bomberman;

import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.text.Text;

public class GameOverScreen {
    private final Stage stage;
    private final Game game;
    private VBox gameOverScreenLayout;
    private Scene gameOverScene;
    private Text gameText;
    private Text overText;

    /**
     * Constructor
     * @param stage - the JavaFX stage
     * @param game - Game object
     */
    public GameOverScreen(Stage stage, Game game) {
        this.stage = stage;
        this.game = game;
        buildGameOverScreen();
    }

    /**
     * builder function to create the elements of the UI page
     */
    private void buildGameOverScreen(){
        gameOverScreenLayout = new VBox(20);
        gameOverScreenLayout.setStyle("-fx-background-color: black;");

        gameText = new Text("Game");
        gameText.setStyle("-fx-font-family: 'Press Start 2p'; -fx-font-size: 40px; -fx-fill: white;");

        overText = new Text("Over");
        overText.setStyle("-fx-font-family: 'Press Start 2p'; -fx-font-size: 40px; -fx-fill: white;");

        gameOverScreenLayout.getChildren().addAll(gameText, overText);
        gameOverScene = new Scene(gameOverScreenLayout, 624, 566); // same height as game window
    }

    /**
     * Shows the game over screen
     */
    public void show() {
        stage.setScene(gameOverScene);
        PauseTransition pauseTransition = new PauseTransition(Duration.millis(2000));
        pauseTransition.setOnFinished(e -> game.setState(GameState.MAIN_MENU));

        // Create TranslateTransitions for the Text elements intros
        TranslateTransition topTransition = new TranslateTransition(Duration.millis(750), gameText);
        topTransition.setFromY(200); // Center
        topTransition.setFromX(-200); // starting position off screen
        topTransition.setToX(250); // ending position following animation (lots of trial and error here)

        TranslateTransition bottomTransition = new TranslateTransition(Duration.millis(750), overText);
        bottomTransition.setFromY(200); // Center
        bottomTransition.setFromX(700); // starting position
        bottomTransition.setToX(253); // ending position following animation

        // Create TranslateTransitions for the Text elements outros
        TranslateTransition topTransition1 = new TranslateTransition(Duration.millis(250), gameText);
        topTransition1.setFromY(200); 
        topTransition1.setFromX(250);
        topTransition1.setToX(700);

        TranslateTransition bottomTransition1 = new TranslateTransition(Duration.millis(250), overText);
        bottomTransition1.setFromY(200); 
        bottomTransition1.setFromX(253);
        bottomTransition1.setToX(-200);

        // combine the transitions running together into parallel transitions
        ParallelTransition firstPhase = new ParallelTransition(topTransition, bottomTransition); 
        ParallelTransition secondPhase = new ParallelTransition(topTransition1, bottomTransition1);
        PauseTransition pauseTransition1 = new PauseTransition(Duration.millis(500)); // add a pause for effect

        // sequential transition to tie it all together
        SequentialTransition sequentialTransition = new SequentialTransition();
        sequentialTransition.getChildren().addAll(firstPhase, pauseTransition1, secondPhase);
        sequentialTransition.setOnFinished(e -> game.setState(GameState.MAIN_MENU)); // upon finishing transitions exit to main menu

        // Play the transitions
        sequentialTransition.play();
    }
}
