package minigames.client.bomberman;

import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;


/**
 * Class for building and controlling the menu system
 * 
 * Contributors: Jacob Derbyshire - jderbysh@myune.edu.au
 *               Daniel Gooden - dgooden@myune.edu.au
 */
public class MenuController {
    private final Stage stage;
    private final Game game;
    private int selectedIndex = 0;
    private VBox menuLayout;
    private Text[] menuOptions;
    private Scene menuScene;
    private SequentialTransition sequentialTransition;

    /**
     * Constructor
     * @param stage - The JavaFX stage
     * @param game - The Game object
     */
    public MenuController(Stage stage, Game game) {
        this.stage = stage;
        this.game = game;
        Font.loadFont(getClass().getResourceAsStream("/fonts/PressStart2P-Regular.ttf"), 20); // get the custom font from resources

        initialize();
    }

    /**
     * Builds the menu object
     */
    private void initialize() {
        menuLayout = new VBox(10);


        // set the background to black
        menuLayout.setStyle("-fx-background-color: black;");
        menuLayout.setAlignment(javafx.geometry.Pos.CENTER); // center the menu for aesthetic reasons

        // title image with the bomberman graphic
        Image titleLogo = new Image(getClass().getResourceAsStream("/images/bomberman/bombermantitle.png")); // Fixed location for merge
        ImageView titleLogoView = new ImageView(titleLogo);
        titleLogoView.setFitWidth(640);
        titleLogoView.setPreserveRatio(true);
        menuLayout.getChildren().add(titleLogoView);

        // create menu options
        Text startGame = new Text("Start Game");
        Text leaderBoard = new Text("Leaderboard");
        Text password = new Text("Password");
        Text controls = new Text("Controls");
        Text exit = new Text("Exit");

        // TODO: this is a bit sloppy, consider refactoring this code
        menuOptions = new Text[]{startGame, leaderBoard, password, controls, exit};
        menuLayout.getChildren().addAll(startGame, leaderBoard, password, controls, exit);

        // cycle through menu using the arrow keys, enter to select
        menuLayout.setOnKeyPressed(event -> {
            //Sound
            Sound.getInstance().playSFX(Sound.Type.BUMP);

            switch (event.getCode()) {
                case UP:
                    moveSelection(-1);
                    break;
                case DOWN:
                    moveSelection(1);
                    break;
                case ENTER:{
                    Sound.getInstance().playSFX(Sound.Type.EXPLOSION);
                    executeSelection();
                }
                    break;
                default:
                    break;
            }
        });

        // Initially select the first option
        updateSelection();

        // create the menu scene
        menuScene = new Scene(menuLayout, 624, 566);
    }

    /**
     * shows the menu on the stage
     */
    public void show() {
        menuLayout.layout();
        stage.setScene(menuScene);
        stage.setWidth(624);
        stage.setHeight(566);
        menuLayout.requestFocus();
    }

    /**
     * Helper function for moving around in the menu
     * @param direction
     */
    private void moveSelection(int direction) {
        // Update the selected index based on direction
        sequentialTransition.stop(); // stop the animation for the old selected item
        menuOptions[selectedIndex].setOpacity(1); // set the opacity of the old selected item back to normal in case it's mid transition
        selectedIndex = (selectedIndex + direction + menuOptions.length) % menuOptions.length; // Wrap around
        updateSelection();
    }

    /**
     * Helper function to highlight the selected text
     */
    private void updateSelection() {
        // highlight the selected option by utilising styles
        for (int i = 0; i < menuOptions.length; i++) {
            Text text = menuOptions[i];
            if (i == selectedIndex) {
                text.setStyle("-fx-font-family: 'Press Start 2p'; -fx-font-size: 20px; -fx-fill: yellow;"); // Highlight
                sequentialTransition = animateSelection(menuOptions[selectedIndex]); // animate the newly selected menu item
            } else {
                text.setStyle("-fx-font-family: 'Press Start 2p'; -fx-font-size: 20px; -fx-fill: white;"); // Ensure everything else is not highlighted
            }
        }
    }

    /**
     * Helper function to run the selected option
     */
    private void executeSelection() {
        switch (selectedIndex) {
            case 0:
                game.setState(GameState.PLAYING); // calling the setState method in game also runs the show method in the selected game state / class
                break;
            case 1:
                game.setState(GameState.LEADERBOARD);
                break;
            case 2:
                game.setState(GameState.PASSWORD);
                break;
            case 3:
                game.setState(GameState.CONTROLS);
                break;
            case 4:
                // look at changing this to exit to the main game menu at some point
                Sound.getInstance().releaseAllClips(); // Release memory for clips
                Platform.exit();
                break;
        }
    }

    public static void transition(Node javaFXNode, double duration, float from, float to){
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setDuration(Duration.millis(duration));
        fadeTransition.setNode(javaFXNode);
        fadeTransition.setFromValue(from); // Fully opaque
        fadeTransition.setToValue(to); 
        fadeTransition.play();
    }

    public SequentialTransition animateSelection(Node javaFXNode){
        // Create a FadeTransition for fading in
        FadeTransition fadeIn = new FadeTransition();
        fadeIn.setDuration(Duration.millis(250));
        fadeIn.setNode(javaFXNode);
        fadeIn.setFromValue(0.0); // Fully transparent
        fadeIn.setToValue(1.0);   // Fully opaque

        // Create a FadeTransition for fading out
        FadeTransition fadeOut = new FadeTransition();
        fadeOut.setDuration(Duration.millis(250));
        fadeOut.setNode(javaFXNode);
        fadeOut.setFromValue(1.0); // Fully opaque
        fadeOut.setToValue(0.0);   // Fully transparent

        SequentialTransition sequentialTransition = new SequentialTransition();
        sequentialTransition.getChildren().addAll(fadeIn, fadeOut);

        // Set the SequentialTransition to repeat indefinitely
        sequentialTransition.setCycleCount(SequentialTransition.INDEFINITE);

        // Start the transition
        sequentialTransition.play();
        return sequentialTransition;
    }
}
