package minigames.client.bomberman;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * The Game class represents the game and is the overall controller for the
 * application. It contains the game board, game controller, and menu controller.
 * It is responsible for starting the game and changing the game state.
 *
 * Contributors: Daniel Gooden - dgooden@myune.edu.au
 */
public class Game {
    private final Pane menuRoot;
    private final Pane gameRoot;
    private final Stage stage;
    private final GameController gameController;
    private final MenuController menuController;
    private final Leaderboard leaderboard;
    private final ControlsPage controlsPage;
    private final Password password;
    private final Pause pause;
    private final GameOverScreen gameOver;
    private GameState state;
    private int paused = 0;

    /**
     * Constructor
     * @param stage
     */
    public Game(Stage stage) {
        this.stage = stage;
        menuRoot = new Pane();
        gameRoot = new Pane();

        // Initialize controllers
        menuController = new MenuController(stage, this);
        gameController = new GameController(stage, this);
        leaderboard = new Leaderboard(stage, this);
        controlsPage = new ControlsPage(stage, this);
        password = new Password(stage, this);
        pause = new Pause(stage, this);
        gameOver = new GameOverScreen(stage, this);

        state = GameState.MAIN_MENU;

       stage.setMaxWidth(624); //fixed sizing for aesthetic reasons
       stage.setMaxHeight(566); 
       stage.setMinWidth(624);
       stage.setMinHeight(566);

       handleOnClose(stage);

    }

    /**
     * Start the game
     */
    public void start() {
        System.out.println("Starting game with state: " + state);
        setState(state); // Ensure the initial state is handled
        // stage.show(); // Make sure the stage is shown
        // Sound methods - added by music team - Arabella Wain
//        Sound.getInstance().testSound();
        //sound.playMusic(0); // Added by music team - Arabella Wain
    }

    /**
     * Add when user closes the game window, sound and game is closed. - Li
     */

    public void handleOnClose(Stage stage) {
        // uses a lambda function

        stage.setOnCloseRequest((closeEvent) -> {
            Sound.getInstance().releaseAllClips(); // Release memory for clips
            Platform.exit();
        });
    }

    /**
     * Setter for paused value, allows the setState function to distinguish which
     * gameController function to call when resuming the game or starting a new game
     * @param value
     */
    public void setPaused(int value) {
        this.paused = value;
    }

    /**
     * Getter for paused value, allows Leaderboard, Password and Controls page to distinguish 
     * which state to set when returning from those pages (either return to game or main menu)
     * @return
     */
    public int getPaused() {
        return paused;
    }

    /**
     * Get the game state
     * @return state
     */
    public GameState getState() {
        return state;
    }

    /**
     * Set the game state
     * @param state
     */
    public void setState(GameState state) {

        // Check if transitioning from PAUSE to MAIN MENU
        if (this.state == GameState.PAUSE && state == GameState.MAIN_MENU) {
            int score = gameController.getCurrentScore();
            String name = "TestUser"; // TODO implement from game client
            gameController.submitScore(name, score, "bomberman");
        }

        this.state = state;

        switch (state) {
            case PLAYING -> {
                // play sound
                Sound.getInstance().playMusic(Sound.Type.BGM);
                if (paused == 0) {
                    gameController.start();
                }
                if (paused == 1) {
                    paused = 0;
                    gameController.resume();
                }
            }
            case MAIN_MENU -> {
                // reset the UI upon entry to the menu screen so the score and level doesn't carry over
                gameController.resetUI();
                //Play Music
                Sound.getInstance().playMusic(Sound.Type.MENU);
                menuController.show();              
            }
            case LEADERBOARD -> leaderboard.show();
            case CONTROLS -> controlsPage.show();
            case PASSWORD -> password.show();
            case PAUSE -> pause.show();
            case GAME_OVER -> gameOver.show(); // TODO change this once implemented
        }
    }

    /**
     * added this method to create a cheat code for super speed
     * @param speed
     */
    public void setSpeed(double speed) {
        if (paused == 0) {
            // if the game is not paused, set the value through the game controller class
            gameController.setSpeed(speed);
        }
        // if the game is paused, this means the game is active
        // therefore, set the value directly with the instantiated player belonging to gameController
        else {
            gameController.setSpeed(speed); // also set with gameController so it levels up correctly
            gameController.getPlayer().setSpeed(speed);
        }
        
    }

    /**
     * This method sets the player power, utilised as a cheat code
     * @param power
     */
    public void setPower(int power) {
        // if the game is not paused, set the value through the game controller class
        if (paused == 0) {
            gameController.setPower(power);
        }
        // if the game is paused, this means the game is active
        // therefore, set the value directly with the instantiated player belonging to gameController
        else {
            gameController.setPower(power); // also set with gameController so it levels up correctly
            gameController.getPlayer().setPower(power);
        }   
    }
}
