package minigames.client.minesweeper;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.core.json.JsonArray;
import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * This class is used to handle the user interface associated with debugging the Minesweeper game.
 * Author: Matt Hayes
 */

public class userInterface implements GridAndUserInterfaceListener {
    // Initialize variables
    private String difficulty;
    private int minesRemaining;
    private int time;
    private user user;
    private int score = 0;
    private boolean isReset = false;
    private boolean isPaused = false;

    private JPanel dockTop;
    private Grid mineArea;
    private JLabel usernameLabel;
    private JLabel scoreLabel;
    private JLabel timeLabel;
    private JLabel difficultyLabel;
    private JLabel minesRemainingLabel;
    private JButton resetButton;
    private GameController gameController;
    private Menu menu;

    private Timer timer;
    
    //background image variables
    private BufferedImage background;
    private static String dir = "/images/minesweeper/";
    private static String background_img = dir + "mainmenu-background.png";

    // UI constructor
    public userInterface(user user, GameController gameController) {
        this.user = user;
        this.gameController = gameController;
        
        //Loads in the background image
        try {
            background = ImageIO.read(getClass().getResource(background_img));
            if (background == null) {
					System.out.println("Image not found");            
            }
        } catch (IOException e) {
            System.out.println("Error loading background image. \nError message: " + e.getMessage());
        }
    }
    // Setter for the menu
    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    // Getters
    public String getDifficulty() {
        return difficulty;
    }
    public Menu getMenu() {
        return this.menu;
    }

    public int getTime() {
        return time;
    }

    public int getMinesRemaining() {
        return minesRemaining;
    }

    public boolean getIsReset() {
        return isReset;
    }

    public int getScore() {
        return score;
    }
    public user getUser(){
        return this.user;
    } 
    public boolean isPaused() {
        return isPaused;
    } 

    /**
     * Wrapper to get data from tiles in the grid.
     * 
     * @returns tile data as int[][] array
     */
    public Grid getGrid() {
        return mineArea;
    }

    // Setters  
    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
        if (difficultyLabel != null) {
            SwingUtilities.invokeLater(() -> difficultyLabel.setText("Difficulty: " + difficulty));
        }
    }

    public void setTime(int time) {
        this.time = time;
        if (timeLabel != null) {
            SwingUtilities.invokeLater(() -> timeLabel.setText("Time: " + formatTime(time)));
        }
    }

    public void setUser(user u) {
        this.user = u;
        if (usernameLabel != null) {
            SwingUtilities.invokeLater(() -> usernameLabel.setText("User: " + user.getUserName()));
        }
    }

    public void setMinesRemaining(int minesRemaining) {
        this.minesRemaining = minesRemaining;
        if (minesRemainingLabel != null) {
            SwingUtilities.invokeLater(() -> minesRemainingLabel.setText("Mines: " + minesRemaining));
        }
    }

    public void setScore(int score) {
        this.score = score;
        if (scoreLabel != null) {
            SwingUtilities.invokeLater(() -> scoreLabel.setText("Score: " + score));
        }
    }

    // Create the information panel docked at the top of the screen
    public JPanel getDockTop() {
        // Initialize the JPanel and JLabels
        dockTop = new JPanel(new GridLayout(1, 7, 0, 0)); 
        JButton optionsMenu = new JButton("Options");
        optionsMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pauseTimer();
                menu.showOptions(() -> resumeTimer()); // Pass the resumeTimer() as a callback
            }
        });
    
        usernameLabel = new JLabel("User: " + user.getUserName());
        scoreLabel = new JLabel("Score: " + user.getScore());
        timeLabel = new JLabel("Time: " + formatTime(time));
        difficultyLabel = new JLabel("Difficulty: " + difficulty);
        minesRemainingLabel = new JLabel("Mines: " + minesRemaining);
        resetButton = new JButton("Reset");
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetGame();
            }
        });
    
        // Create little panels to allow padding to space elements better
        JPanel mainMenuPanel = new JPanel(new BorderLayout());
        mainMenuPanel.add(optionsMenu);
    
        JPanel userNamePanel = new JPanel(new BorderLayout());
        userNamePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        userNamePanel.add(usernameLabel);
    
        JPanel scorePanel = new JPanel(new BorderLayout());
        scorePanel.add(scoreLabel);
    
        JPanel timePanel = new JPanel(new BorderLayout());
        timePanel.add(timeLabel);
    
        JPanel difficultyPanel = new JPanel(new BorderLayout());
        difficultyPanel.add(difficultyLabel);
    
        JPanel minesRemainingPanel = new JPanel(new BorderLayout());
        minesRemainingPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        minesRemainingPanel.add(minesRemainingLabel);
    
        JPanel resetButtonPanel = new JPanel(new BorderLayout());
        resetButtonPanel.add(resetButton);
    
        // Ordered from left to right across the top of the screen
        dockTop.add(mainMenuPanel);
        dockTop.add(userNamePanel);
        dockTop.add(scorePanel);
        dockTop.add(timePanel);
        dockTop.add(difficultyPanel);
        dockTop.add(minesRemainingPanel);
        dockTop.add(resetButtonPanel);
    
        return dockTop;
    }
    

    // Create a space for the mine grid
    public JPanel getMineArea(String difficulty, JsonObject data) {
        // Initialize the mine area panel
        int[][] tilesdata = null;

        if (data != null) {
            difficulty = data.getString("difficulty");
            setScore(data.getInteger("score"));
            setTime(data.getInteger("time"));

            JsonArray arr = data.getJsonArray("tilesdata");
            tilesdata = new int[arr.size()][];

            for (int i = 0; i < arr.size(); i++) {
                JsonArray innerArray = arr.getJsonArray(i);

                // Initialize the inner int array with the size of the inner JsonArray
                tilesdata[i] = new int[innerArray.size()];

                // Iterate over the inner JsonArray and populate the inner int array
                for (int j = 0; j < innerArray.size(); j++) {
                    tilesdata[i][j] = innerArray.getInteger(j);
                }
            }
        }

        mineArea = new Grid(difficulty, gameController, tilesdata);
        // Synchronize score in Grid and UI.
        // NOTE: it is bad to have score in different places. Should be either
        //       in Grid or UI.
        mineArea.setScore(getScore());
        mineArea.gridAndUserInterfaceListener(this);
        setMinesRemaining(mineArea.getNumMines());
        setDifficulty(mineArea.getDifficulty());
        startTimer();
        return mineArea;
    }

    // Create a panel that has the information and mine grid together
    public JPanel createMainPanel(String difficulty, JsonObject data) {
        setDifficulty(difficulty);

        // Sets time depending on difficulty
        if (difficulty.equalsIgnoreCase("easy")) {
            setTime(120);
        } else if (difficulty.equalsIgnoreCase("medium")) {
            setTime(240);
        } else if (difficulty.equalsIgnoreCase("hard")) {
            setTime(360);
        }else{
            setTime(360);
        }
        
        // Initialize the main panel
		  JPanel mainPanel = new JPanel(new BorderLayout());        
        
        // Initialize the background panel
        JLayeredPane backgroundPanel = new JLayeredPane() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (background != null) {
                    int panelWidth = getWidth();
                    int panelHeight = getHeight();
                    int imageWidth = background.getWidth();
                    int imageHeight = background.getHeight();
    
                    // Calculate the scaling factor to maintain the aspect ratio
                    double aspectRatio = (double) imageWidth / imageHeight;
                    int newWidth = panelWidth;
                    int newHeight = (int) (panelWidth / aspectRatio);
    
                    if (newHeight > panelHeight) {
                        newHeight = panelHeight;
                        newWidth = (int) (panelHeight * aspectRatio);
                    }
    
                    // Calculate the top-left corner for centering the image
                    int x = (panelWidth - newWidth) / 2;
                    int y = (panelHeight - newHeight) / 2;
    
                    // Draw the image with the calculated dimensions
                    g.drawImage(background, x, y, newWidth, newHeight, this);
                }
            }
        };
        backgroundPanel.setPreferredSize(new Dimension(800, 600));
		  backgroundPanel.setLayout(new BorderLayout());
        
        // Get the dock panel
        JPanel dockPanel = getDockTop();
        dockPanel.setOpaque(false);
        backgroundPanel.add(dockPanel, BorderLayout.NORTH);
		
        // Get the mine panel
        JPanel minePanel = getMineArea(difficulty, data);
        minePanel.setOpaque(false);
        backgroundPanel.add(minePanel, BorderLayout.CENTER);
       
        mainPanel.add(backgroundPanel, BorderLayout.CENTER);
        
        return mainPanel;
    }

    // Used to turn a time in seconds to mm:ss format
    public String formatTime(int time) {
        int minutes = time / 60;
        int sec = time % 60;
        return String.format("%02d:%02d", minutes, sec);
    }

    // Timer to count down how long the user has left    
    private void startTimer() {
        if (timer != null) {
            return;  // If there's already an active timer, don't start another
        }
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (time > 0) {
                    time--;
                    SwingUtilities.invokeLater(() -> timeLabel.setText("Time: " + formatTime(time)));
                } else {
                    stopTimer();
                    gameController.playerLoses();
                }
            }
        };
        // Timer runs every second
        timer.scheduleAtFixedRate(task, 0, 1000);
    }
    /**
    * Stops and nullifies the current timer if it is running.
    * <p>Cancels the timer and sets it to null if it is not already null.
    */
    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null; // Nullify the timer after stopping
        }
    }

    private void pauseTimer() {
        if (timer != null) {
            timer.cancel(); // Stop the current timer
            timer = null; // Nullify the timer after stopping
            isPaused = true;
        }
    }
    
    private void resumeTimerFrom(int remainingTime) {
        if (timer == null && isPaused) { // Only create a new timer if it's paused and no timer exists
            this.time = remainingTime;  // Update the class-level `time` variable
            timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    if (time > 0) {
                        time--;  // Decrement the class-level `time` variable
                        SwingUtilities.invokeLater(() -> timeLabel.setText("Time: " + formatTime(time)));
                    } else {
                        stopTimer();
                        // Handle game over logic here
                    }
                }
            };
            timer.scheduleAtFixedRate(task, 0, 1000); // Timer runs every second
            isPaused = false; // Reset the pause state
        }
    }

    private void resumeTimer() {
        if (isPaused) {
            resumeTimerFrom(time); // Resume the timer with the remaining time
        }
    }

    public void resetGame() {
        stopTimer();
        isReset = true;
        gameController.resetGame();
    }

    /**
     * This method is called when the number of flagged mines is updated 
     * @param flagsPlaced
     */
    @Override
    public void onMinesRemainingChanged(int flagsPlaced) {
        int minesRemaining = this.minesRemaining - flagsPlaced;
        setMinesRemaining(minesRemaining);
        //If all flags have been placed, check if the mine tiles are flagged, if not player loses
        if (minesRemaining == 0) {
            boolean minesFlagged = true;
            for(int r=0; r<mineArea.getRows(); r++){
                for(int c=0; c<mineArea.getCols(); c++){
                    if(mineArea.getTile(r, c).isMine()){
                        if(mineArea.getTile(r, c).getState().equals(State.FLAGGED)){
                            continue;
                        }else{
                            minesFlagged = false;
                            break;
                        }
                    }
                }
            }
            if(minesFlagged){
                scoreMultiplier();
                gameController.playerWins();
            }else{
                gameController.playerLoses();
            }
        }
    }

    @Override
    public void onScoreChange(int score) {
        // Update score
        setScore(score);
    }

    /**
     * Adds a multiplier onto the score depending on how much time is remaining
     */
    public void scoreMultiplier() {
        int time = getTime();
        double multiplier = 1.0;

        if (this.difficulty.equalsIgnoreCase("easy")) {
            switch (time / 10) { // Divide time by 10 to group by 10-second intervals
                case 10:
                case 11: // 100% bonus for 100 - 119 seconds remaining
                    multiplier += 1.0;
                    break;
                case 8:
                case 9: // 50% bonus for 80 - 99 seconds remaining
                    multiplier += 0.5;
                    break;
                case 5:
                case 6:
                case 7: // 30% bonus for 50 - 79 seconds remaining
                    multiplier += 0.3;
                    break;
                case 3:
                case 4: // 15% bonus for 30 - 49 seconds remaining 
                    multiplier += 0.15;
                    break;
                case 1:
                case 2: // 5% bonus for 10 - 29 seconds remaining
                    multiplier += 0.05;
                    break;
                default: // 2% bonus for under 10 seconds remaining
                    multiplier += 0.02;
                    break;
            }
        } else if (this.difficulty.equalsIgnoreCase("medium")) {
            switch (time / 10) {
                case 21:
                case 22:
                case 23: // 100% bonus for 210 - 239 seconds remaining
                    multiplier += 1.0;
                    break;
                case 18:
                case 19:
                case 20: // 50% bonus for 180 - 209 seconds remaining
                    multiplier += 0.5;
                    break;
                case 12:
                case 13:
                case 14:
                case 15:
                case 16:
                case 17: // 30% bonus for 120 - 179 seconds remaining
                    multiplier += 0.3;
                    break;
                case 7:
                case 8:
                case 9:
                case 10:
                case 11: // 15% bonus for 70 - 119 seconds remaining 
                    multiplier += 0.15;
                    break;
                case 2:
                case 3:
                case 4:
                case 5:
                case 6: // 5% bonus for 20 - 69 seconds remaining
                    multiplier += 0.05;
                    break;
                default: // 2% bonus for under 20 seconds remaining
                    multiplier += 0.02;
                    break;
            }

        } else if (this.difficulty.equalsIgnoreCase("hard")) {
            switch (time / 10) {
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                case 35: // 100% bonus for 300 - 359 seconds remaining
                    multiplier += 1.0;
                    break;
                case 24:
                case 25:
                case 26:
                case 27:
                case 28:
                case 29: // 50% bonus for 240 - 299 seconds remaining
                    multiplier += 0.5;
                    break;
                case 18:
                case 19:
                case 20:
                case 21:
                case 22:
                case 23: // 30% bonus for 180 - 239 seconds remaining
                    multiplier += 0.3;
                    break;
                case 10:
                case 11:
                case 12:
                case 13:
                case 14:
                case 15:
                case 16:
                case 17: // 15% bonus for 100 - 179 seconds remaining 
                    multiplier += 0.15;
                    break;
                case 6:
                case 7:
                case 8:
                case 9: // 5% bonus for 60 - 99 seconds remaining
                    multiplier += 0.05;
                    break;
                case 3:
                case 4:
                case 5: // 4% bonus for 30 - 59 seconds remaining
                    multiplier += 0.04;
                    break;
                default: // 2% bonus for under 30 seconds remaining
                    multiplier += 0.02;
                    break;
            }
        }

        // Apply the multiplier to the current score
        score = (int) (score * multiplier);
        setScore(score);
    }
}
