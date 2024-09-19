package minigames.client.minesweeper;

import java.awt.GridLayout;
import java.util.Collections;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import minigames.client.GameClient;
import minigames.client.MinigameNetworkClient;
import minigames.client.snake.AchievementHandler;
import minigames.commands.CommandPackage;
import minigames.rendering.GameMetadata;

public class Minesweeper implements GameClient {

    private MinigameNetworkClient mnClient;
    private GameMetadata gm;

    // Temporary data storage to enable loading of the saved game
    private JsonObject gameTempData;

    private userInterface ui;
    private Menu menu;
    private user testUser;
    private GameState gameState;
    private String difficulty;
    private GameController gameController;
    private JPanel mainPanel;
    private Leaderboard leaderboard;
    private Integer savesLeft;

    public Minesweeper() {
        // Create a dummy User object for the player
        this.testUser = new user("Player", 0);
        this.gameController = new GameController(this);
        this.ui = new userInterface(testUser, gameController);
        this.menu = new Menu(ui,this,gameController);
        ui.setMenu(menu);
        // Initialize leaderboard with dummy data
        user[] users = {
            new user("AAA", 100),
            new user("BBB", 90),
            new user("CCC", 80),
            new user("DDD", 70),
            new user("EEE", 60),
            new user("FFF", 50),
            new user("GGG", 40),
            new user("HHH", 30),
            new user("III", 20),
            new user("JJJ", 10)
        };
        this.leaderboard = new Leaderboard(users, users.length, gameController);

    }

    /**
     * Asks users what difficulty they would like
     * @return a string wither 'easy', 'medium', 'hard'
     */
    public String askForDifficulty() {
        String[] options = {"Easy", "Medium", "Hard", "Custom"};
        int choice = JOptionPane.showOptionDialog(
            null,
            "Select Difficulty Level:",
            "Difficulty Selection",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );

        if (choice >= 0) {
            String usersChoice =  options[choice].toLowerCase(); // Convert choice to lowercase to match the method's expected input
            if (usersChoice.equalsIgnoreCase("custom")){
                return customDifficulty();
            }else{
                return usersChoice;
            }
        } else {
            // Default to medium if the user closes the dialog or does not make a selection
            return "medium";
        }
    }

    /**
     * This function is called when the user selects 'custom' difficulty, it asks the user for their desired number of rows, cols, and number of mines.
     * @return String in format (rows).(cols).(numOfMines).
     */
    public String customDifficulty(){
        String difficulty = "";

        while (true) {
            // Create input fields
            JTextField rowsField = new JTextField(5);
            JTextField columnsField = new JTextField(5);
            JTextField minesField = new JTextField(5);

            // Create a panel with GridLayout and add the fields to it
            JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
            panel.add(new JLabel("Number of Rows (Min: 4 - max: 30):"));
            panel.add(rowsField);
            panel.add(new JLabel("Number of Columns (Min: 4 - max: 30):"));
            panel.add(columnsField);
            panel.add(new JLabel("Number of Mines: (Min: 1 - max: rows * cols)"));
            panel.add(minesField);

            // Show the panel in a JOptionPane dialog
            int result = JOptionPane.showConfirmDialog(null, panel, "Grid Settings",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            // If the user presses OK, retrieve the values and validate them
            if (result == JOptionPane.OK_OPTION) {
                try {
                    int rows = Integer.parseInt(rowsField.getText());
                    int columns = Integer.parseInt(columnsField.getText());
                    int mines = Integer.parseInt(minesField.getText());

                    // Validate the values
                    if (rows <= 30 && columns <= 30 && mines <= rows * columns -1) {
                        difficulty = rows + "." + columns + "." + mines + ".";
                        break;  // Valid input, exit the loop
                    } else {
                        String errorMessage = "";
                        if (rows < 4 || rows > 30) errorMessage += "Rows cannot be less than 4 and exceed 30.\n";
                        if (columns < 4 || columns > 30) errorMessage += "Columns cannot be less than 4 and exceed 30.\n";
                        if (mines < 1 || mines >= rows * columns) errorMessage += "Mines cannot be less than 1 and exceed rows * columns -1.\n";
                        JOptionPane.showMessageDialog(null, errorMessage, "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Please enter valid numbers.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                }
            } else if (result == JOptionPane.CANCEL_OPTION){
                break;  // User canceled, exit the loop
            }
        }

        return difficulty;
    }

    /**
     * Sets the difficulty level for the game.
     *
     * @param difficulty The selected difficulty level (e.g., "Easy", "Medium", "Hard").
     */
    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    /**
     * @return difficulty string
     */
    public String  getDifficulty() { return this.difficulty;}

    /**
     * Starts a new game by creating the main game panel based on the selected difficulty
     * and initializing the game state. This method prepares the UI and transitions the game
     * into the playing state.
     */
    public void startNewGame() {

        if (ui != null) {
            ui.stopTimer();  // Stop any running timer from a previous game
        }

        // Check if the difficulty is already set; if not, prompt the user
        if (difficulty == null || difficulty.isEmpty()) {
            difficulty = askForDifficulty(); // Only ask if the difficulty wasn't set by the button
        }

        ui.createMainPanel(difficulty, null);
        gameController.startGame();
        changeGameState(GameState.PLAYING);
    }

    /**
     * Called to load a game
     */
    public void load(MinigameNetworkClient mnClient, GameMetadata game, String player) {
        this.mnClient = mnClient;
        this.gm = game;

        // Update User object using game metadata.
        this.testUser = new user(player, 0);
        this.ui.setUser(testUser);

        // Go to main menu only for new game.
        // else: see execute()/loadGame.
        if (game.name().equals("NEWGAME")) {
            gameController.navMainMenu();
        }
    }

     /**
     * Public method to transition game state from other classes
     */
    public void changeGameState(GameState newState) {
    	gameState = newState;
        transitionTo(newState);
    }

    /**
     * Initialize the number of allowed saves depending on difficulty
     */
    private void setSavesNumber() {
        if (difficulty == null)
            return;

        switch (difficulty) {
            case "easy":
                savesLeft = 2;
                break;
            case "medium":
                savesLeft = 4;
                break;
            case "hard":
                savesLeft = 6;
                break;
            default:
                savesLeft = 8;
                break;
        }
    }

    /**
    * Handles the transition to a new game state
    */
    public void transitionTo(GameState newState) {
        switch (newState) {
            case MENU:
                if(mnClient != null) {
                    if (ui != null) {
                        ui.stopTimer();  // Stop any running timer
                    }
                    JPanel mainMenuPanel = menu.createMainMenuPanel(gameController);
                    menu.setNetworkClient(mnClient);
                    mnClient.getMainWindow().clearAll();
                    mnClient.getMainWindow().addCenter(mainMenuPanel);
                    mnClient.getMainWindow().pack();
                }
                break;

            case PLAYING:
                if(mnClient != null) {
                    if (ui != null) {
                        ui.stopTimer();  // Stop any running timer
                    }
                    //TODO Play music during gameplay
                    //Sound methods - added by music team - Charles Cavanagh
                    //Sound.getInstance().testSound();
                    //Sound.playMusic(0); // Added by music team - Charles Cavanagh

                    setSavesNumber();

                    if (gameTempData != null) {
                        setDifficulty(gameTempData.getString("difficulty"));
                        savesLeft = gameTempData.getInteger("saves");
                    }

                    if (difficulty != null) {  // Ensure difficulty is set
                        mnClient.getMainWindow().clearAll();
                        mainPanel = ui.createMainPanel(difficulty, gameTempData);
                    if (difficulty == "easy") { //Play background music
                    	Sound.getInstance().playMusic(Sound.Type.BGMLOW);
                    } else if (difficulty == "hard") {
                    	Sound.getInstance().playMusic(Sound.Type.BGMHI);
                    } else {
                    	Sound.getInstance().playMusic(Sound.Type.BGMMID);
                    }
                        gameTempData = null; // Clear temp data after setting
                        mnClient.getMainWindow().addCenter(mainPanel);
                        mnClient.getMainWindow().pack();
                    } else {
                        JOptionPane.showMessageDialog(null, "Difficulty level is not set!");
                    }
                }
                break;
            case WIN:
                if(mnClient != null) {
                    // Handle win condition
                    Sound.getInstance().stopMusic();
                    Sound.getInstance().play(Sound.Type.WIN);
                    //TODO: change from jane_smith hard codef when logged in players are working
                    AchievementHandler achievementHandler = new AchievementHandler(mnClient, this.testUser.getUserName(), null);

                    if (difficulty != null) {
                        String mode = "Defeated" + difficulty.substring(0, 1).toUpperCase() + difficulty.substring(1) + "Mode";
                        achievementHandler.awardAchievement(mode);
                    }

                    JOptionPane.showMessageDialog(null, "Congratulations! You won!");
                    gameController.showLeaderboard();
                }
                break;
            case LOSE:
                if(mnClient != null) {
                    // Handle lose condition
            	Sound.getInstance().stopMusic();
            	Sound.getInstance().play(Sound.Type.LOSE);
                    JOptionPane.showMessageDialog(null, "Sorry! You lost. Try again!");
                    gameController.showLeaderboard();
                }
                break;
            case LEADERBOARD:
                if(mnClient != null) {
                    //Set Leaderboard position
                    leaderboard.getRank(new user(testUser.getUserName(), ui.getScore()));
                    // Display the leaderboard
                    mnClient.getMainWindow().clearAll();
                    mainPanel = leaderboard.createLeaderboard();
                    mnClient.getMainWindow().addCenter(mainPanel);
                    mnClient.getMainWindow().pack();
                }
                break;
        }
    }

    /**
     * @return current game state
     */
    public GameState getGameState(){return gameState;}

    // Method to replace the current UI with a new one
    public void replaceUI(user newUser, GameController gameController) {
        mnClient.getMainWindow().clearAll();
        if (ui != null) {
            ui.stopTimer();  // Ensure any running timer is stopped
            ui = null;
        }

        // Initialize new UI
        this.ui = new userInterface(newUser, this.gameController);
        // Optionally, update the UI in your application
    }

    /**
     * Called to execute a command that has been sent by the server
     */
    public void execute(GameMetadata game, JsonObject command) {
        switch (command.getString("command")) {
            case "loadGame": {
                setGameData(command.getJsonObject("payload"));
                changeGameState(GameState.PLAYING);
            }
        }
    }


    private JsonObject toJson() {
        int [][]data = ui.getGrid().getTilesData();

        JsonArray jsonArray = new JsonArray();
        for (int[] row : data) {
            JsonArray rowArray = new JsonArray();
            for (int val : row) {
                rowArray.add(val);
            }
            jsonArray.add(rowArray);
        }

        // Create a JsonObject and add the JsonArray
        JsonObject jsonObject = new JsonObject();
        jsonObject.put("difficulty", ui.getDifficulty());
        jsonObject.put("score", ui.getScore());
        jsonObject.put("time", ui.getTime());
        jsonObject.put("saves", savesLeft);

        // With custom difficulty we might need this information
        jsonObject.put("rows", ui.getGrid().getRows());
        jsonObject.put("cols", ui.getGrid().getCols());

        jsonObject.put("tilesdata", jsonArray);
        return jsonObject;
    }

    public void setGameData(JsonObject data) {
        this.gameTempData = data;
    }

    /**
     * Usually this is at the end of the game
     */
    public void closeGame() {
        // On game close save the game data
        saveGame();
    }

    public int getSavesLeft() { return savesLeft; }

    /**
     * Save the game
     */
    public boolean saveGame() {
        if (savesLeft == 0)
            return false;

        savesLeft--;
        sendClientCommand("saveGame", toJson());
        return true;
    }

    /**
     * Send a command to the server with the optional payload.
     * The server is supposed to respond.
     */
    public void sendClientCommand(String command, JsonObject payload) {
        JsonObject json = new JsonObject();
        json.put("command", command);

        if (payload != null) {
            json.put("payload", payload);
        }
        sendClientJson(json);
    }

    /**
     * Send a command to the server.
     *
     * @param json the json containing data for the server.
     */
    public void sendClientJson(JsonObject json) {
        // Collections.singletonList() is a quick way of getting a "list of one item"
        mnClient.send(
            new CommandPackage(
                gm.gameServer(), gm.name(),
                this.testUser.getUserName(),
                Collections.singletonList(json)
            )
        );
    }

    public void setNetworkClient(MinigameNetworkClient mockNetworkClient) {
    }
}
