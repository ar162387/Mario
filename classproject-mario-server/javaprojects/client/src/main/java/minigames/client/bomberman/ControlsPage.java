// imports
package minigames.client.bomberman;

import javafx.animation.PauseTransition;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.text.Text;
import javafx.scene.layout.Region;
import javafx.scene.input.KeyCode;

/**
 * The controls page class is responsible for displaying game controls.
 * It allows the user to quickly reference how to play our game.
 * 
 * Contributors: Jacob Derbyshire - jderbysh@myune.edu.au
 */

public class ControlsPage {
    private final Stage stage;
    private final Game game;
    private VBox controlsLayout;
    private Scene controlsScene;
    private Node[] pageNodes;

    /**
     * Constructor
     * @param stage - the JavaFX stage
     * @param game - Game object
     */
    public ControlsPage(Stage stage, Game game){
        this.stage = stage;
        this.game = game;
        buildControlsPage();
    }

    /**
     * private function that creates the controls page
     */
    private void buildControlsPage() {
        controlsLayout = new VBox(20);
        controlsLayout.setAlignment(javafx.geometry.Pos.CENTER); // center the layout for aesthetic reasons
        controlsLayout.setStyle("-fx-background-color: black;");

        // Create the scene with the controls layout here so that I can access scene width and height for the image
        controlsScene = new Scene(controlsLayout, 640, 480);

        // set title for controls page
        Text title = new Text("Controls");
        title.setStyle("-fx-font-family: 'Press Start 2p'; -fx-font-size: 40px; -fx-fill: white;");
        controlsLayout.getChildren().add(title);

        // Spacer between title and controls entries
        Region titleSpacer = new Region();
        titleSpacer.setMinHeight(10); // Minimum height for the spacer
        controlsLayout.getChildren().add(titleSpacer);

        Image controlsImage = new Image(getClass().getResourceAsStream("/images/bomberman/keyboardlayout.png"));
        ImageView controlsImageView = new ImageView(controlsImage);
        controlsImageView.setFitWidth(controlsScene.getWidth() * 0.6); // Set to 60% of scene width
        controlsImageView.setFitHeight(controlsScene.getHeight() * 0.6); // Set to 60% of scene height
        controlsLayout.getChildren().add(controlsImageView);

        pageNodes = new Node[]{title, controlsImageView}; // add page nodes to a set so that I can loop through them

        // escape to go back to the main menu
        controlsLayout.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                // Create a FadeTransition
                for (int i=0; i<pageNodes.length; i++){
                    MenuController.transition(pageNodes[i], 250, 1, 0); // loop through the page nodes to produce fade out effect
                }
                // Create a PauseTransition to wait for the animation to complete
                PauseTransition pauseTransition = new PauseTransition(Duration.millis(250));
                pauseTransition.setOnFinished(e -> {
                    // Update the game state after the pause
                    if (game.getPaused() == 0) {
                        game.setState(GameState.MAIN_MENU);
                    }
                    if (game.getPaused() == 1) {
                        game.setState(GameState.PAUSE);
                    }
                });
                // Play the pause transition after the translation
                pauseTransition.play();
            }
        });
    }

    /**
     * shows the controls page on the stage
     */
    public void show(){
        for (int i=0; i<pageNodes.length; i++){
            pageNodes[i].setOpacity(0); // set the opacity to 0 for the fade in effect
        }
        stage.setScene(controlsScene);
        for (int i=0; i<pageNodes.length; i++){
            MenuController.transition(pageNodes[i], 250, 0, 1); // loop through the page nodes to produce fade in effect
        }
        controlsLayout.requestFocus(); // Ensure the controls screen can capture key events
    }
}
