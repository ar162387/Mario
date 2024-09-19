package minigames.client.deepfried.states;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent; 
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import minigames.client.MinigameNetworkClient;
import minigames.client.deepfried.GameStateManager;
import minigames.client.deepfried.UI.GamePlayUI;
import minigames.client.deepfried.entities.BenchSurface;
import minigames.client.deepfried.entities.DishStack;
import minigames.client.deepfried.entities.Entity;
import minigames.client.deepfried.entities.Fryer;
import minigames.client.deepfried.entities.IngredientBox;
import minigames.client.deepfried.entities.Player;
import minigames.client.deepfried.entities.PrepStation;
import minigames.client.deepfried.entities.SendOrderWindow;
import minigames.client.deepfried.input.Input;



public class GameplayState extends GameState {
    // logger for debugging
    private static final Logger logger = LogManager.getLogger(GameplayState.class);
    /**
     * Class handles the running of the game
     * 
     */
    private  GameStateManager gameStateManager;
    private MinigameNetworkClient mnClient;
    public GamePlayUI gamePlayPanel;
    private JPanel hudPanel;
    private JLabel scoreLabel;
    private Input input;
    private int score = 0;
    public int count;
    private int delay = 1000;
    public static Timer countDown;
    public boolean isPause = false;


    // A List to store entities
    private static ArrayList<Entity> entities;
    // the Player entity
    public Player chef;

    public GameplayState(GameStateManager gameStateManager, MinigameNetworkClient mnClient){

        this.gameStateManager = gameStateManager;
        this.mnClient = mnClient; 
        this.input = gameStateManager.getInput();   //needs to be given from the gameStateManager()
        entities = new ArrayList<>();
        initialiseEntities();
        
        // call the UI as a Jpanel as it now extends JPanel
        gamePlayPanel = new GamePlayUI(entities);
        hudPanel = setupGameUI();

        // setup listteners for pause Menu Buttons
        setupPauseMenuButtonListeners();

        // Add the Input object as a KeyListener
        gamePlayPanel.addKeyListener(input);
        gamePlayPanel.setFocusable(true); 
        gamePlayPanel.requestFocusInWindow();

        // add gameplay screen to main Client screen
        mnClient.getMainWindow().clearAll();
        mnClient.getMainWindow().addCenter(hudPanel);
        mnClient.getMainWindow().addCenter(gamePlayPanel);
        mnClient.getMainWindow().pack();

        JFrame frame = mnClient.getMainWindow().getFrame();
        frame.setTitle("DeepFried");
        frame.setSize(1400,900);
        frame.setResizable(true);
        frame.setVisible(true);
    }

        @Override
    public void enter() {
        // Code to execute when the Gameplay state is entered
            // Reset input states to ensure fresh input handling
        if (input != null) {
            input.clear(); // Clear any previous input states
        }
        if (gamePlayPanel != null){
            gamePlayPanel.showPausePanel();
            } else {
                logger.info("pausePanel is null in enter(). Please check initialization.");
            }
        if (gamePlayPanel != null){
            gamePlayPanel.showGamePanel();
            } else {
                logger.info("gamePlayPanel is null in enter(). Please check initialization.");
            }
    }

    @Override
    public void exit() {
        // Code to execute when exiting the Gameplay state
        if (countDown != null){
            countDown.stop();
        }
        
        // Remove KeyListener and clear input state
        if (gamePlayPanel != null && input != null) {
            gamePlayPanel.removeKeyListener(input);
            input.clear(); // Clear the input states
            gamePlayPanel.setFocusable(false); // No longer needs to be focused
            gamePlayPanel.setVisible(false); // Hide the panel
            gamePlayPanel.removeAll(); // Remove all components
        }
    }

    @Override
    public void update() {
        //update score
        updateScore();
        gamePlayPanel.requestFocusInWindow(); 
        // Handle user input for the Gameplay
        if(!isPause){
            handleInput(input);
        }
    }

    @Override
    public void render(Graphics g) {
        // Draw the Gameplay components on the screen
        if (g != null && gamePlayPanel != null) {
            gamePlayPanel.paint(g);
        }
    }

    private JPanel setupGameUI() {
        //create HUD JPanel
        JPanel hudPanel = new JPanel(null);
        hudPanel.setPreferredSize(new Dimension(1140, 50));
        hudPanel.setBackground(Color.WHITE);

        //Create Score JLabel
        scoreLabel = new JLabel ("Score: " + score);
        scoreLabel.setOpaque(true);
        scoreLabel.setBackground(Color.RED);
        scoreLabel.setFont(new Font("Helvetica", Font.PLAIN, 20));
        scoreLabel.setBounds(10,15,150,30);

        //Create Timer JLabel
        JLabel timerLabel = new JLabel();
        timerLabel.setOpaque(true);
        timerLabel.setBackground(Color.GREEN);
        timerLabel.setFont(new Font("Helvetica", Font.PLAIN, 20));
        timerLabel.setBounds(480,15,250,30);
        //Call Game Timer
        startTimer(120, timerLabel);

        //Create JButton and load image
        JButton pauseButton = new JButton();
        try {
            BufferedImage image = ImageIO.read(getClass().getResource("/deepFried/gear.png"));
            pauseButton.setIcon(new ImageIcon(image));            
            } catch (IOException e) {
                e.printStackTrace();
            }
        pauseButton.setBackground(Color.WHITE);
        pauseButton.setBounds(1080,2,46,46);
        pauseButton.setBorderPainted(false);

        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pauseGame();
            }

        });
        hudPanel.add(scoreLabel);
        hudPanel.add(timerLabel);
        hudPanel.add(pauseButton);

        return hudPanel;
    }


    //method to update score
    private void updateScore(){
        //check if score has changed        
        if (score < chef.getScore()){
            //get score
            score = chef.getScore();
            //update score label
            scoreLabel.setText("Score: "+ score);
            //add 10 seconds to timer
            count += 10;
        }   
    }

    // method to pause timers for the entities
    private void pauseEntities (ArrayList<Entity> entities) {
        for  (Entity entity : entities) {
                entity.pauseTimer();
        }
    }

    // method to resume timers for the entities
    private void resumeEntities (ArrayList<Entity> entities) {
        for  (Entity entity : entities) {
                entity.resumeTimer();
        }
    }

    /*
     * Timer to create gameplay countdown 
     * 
     */
    public void startTimer( int countPassed, JLabel time){
        ActionListener action = new ActionListener() {
            // @Override
            public void actionPerformed(ActionEvent e){
                //stop timer if reaches 0
                if ( count == 0){
                    countDown.stop();
                    System.out.println("Timer has reached 0");
                    // send to gameover state here
                    gameStateManager.pushState(new GameCompleteState(gameStateManager, mnClient, chef.getScore()));
                }
                else{
                    //convert seconds to time format
                    int minutes = count / 60;
                    int seconds = count % 60;
                    System.out.println(count + " seconds remaining");
                    //lower count every second
                    count--;
                    //updated JLabel in HUD
                    time.setText(String.format("Time Remaining: %02d:%02d", minutes, seconds));
                }
            }
        };
        //create and start timer
        countDown = new Timer(delay, action);
        countDown.setInitialDelay(0);
        countDown.start();
        count = countPassed;
    }


       // Instatiate the entities in their starting positions
    private void initialiseEntities(){
    
        // Initialize and add all the Fryers
        entities.add(new Fryer(250, 250)); // Fryer 1
        entities.add(new Fryer(390, 250)); // Fryer 2
        entities.add(new Fryer(710, 250)); // Fryer 3
        entities.add(new Fryer(845, 250)); // Fryer 4

        // Initialize and add all the PrepStations
        entities.add(new PrepStation(1090, 140)); // PrepStation 1
        entities.add(new PrepStation(1090, 390)); // PrepStation 2
        entities.add(new PrepStation(5, 140)); // PrepStation 3
        entities.add(new PrepStation(5, 390)); // PrepStation 4

        // Initialize and add all the IngredientBoxes
        entities.add(new IngredientBox(125, -30)); // IngredientBox 1
        entities.add(new IngredientBox(135, 545)); // IngredientBox 2
        entities.add(new IngredientBox(950, -30)); // IngredientBox 3
        entities.add(new IngredientBox(940, 545)); // IngredientBox 4

        entities.add(new DishStack(540, -7));

        // Initialize the bench surfaces
        entities.add(new BenchSurface(425, 0));//top left
        entities.add(new BenchSurface(675, 0)); //top right
        entities.add(new BenchSurface(275, 575)); // bottom left
        entities.add(new BenchSurface(830, 575)); // bottom right
        entities.add(new BenchSurface(5, 290)); // left
        entities.add(new BenchSurface(1090, 290)); //  right

        // Initialize the send order windows
        entities.add(new SendOrderWindow(475, 585));
        entities.add(new SendOrderWindow(549, 585));
        entities.add(new SendOrderWindow(620, 585));
        entities.add(new SendOrderWindow(689, 585));

        chef = new Player(525,325);
        entities.add(chef);
    }
    
    private void handleInput(Input input) {
        input.update();
        // Movement keys (WASD)
        if (input.isKeyPressed(KeyEvent.VK_W)) {
            // Move player up
            movePlayerUp();
        }
        if (input.isKeyPressed(KeyEvent.VK_S)) {
            // Move player down
            movePlayerDown();
        }
        if (input.isKeyPressed(KeyEvent.VK_A)) {
            // Move player left
            movePlayerLeft();
        }
        if (input.isKeyPressed(KeyEvent.VK_D)) {
            // Move player right
            movePlayerRight();
        }
        // Function key (F)
        if (input.isKeyPressed(KeyEvent.VK_F)) {
            performFunctionAction();
        }
        // Pause key (P)
        if (input.isKeyPressed(KeyEvent.VK_P)) {
            System.out.println("Pause Game");
            pauseGame();
        }
        // Pause key (u)
        if (input.isKeyPressed(KeyEvent.VK_U)) {
            System.out.println("Game Over");
            countDown.stop();
            gameStateManager.pushState(new GameCompleteState(gameStateManager, mnClient, chef.getScore()));
        }
        // Pause key (P)
        if (input.isKeyPressed(KeyEvent.VK_M)) {
            System.out.println("Time==0");
            int t = 0;
            setCount(t);
        }
    }

    public void setupPauseMenuButtonListeners() {
        gamePlayPanel.getResumeButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Resume the game
                System.out.println("Resuming Game");
                //startTimer(getTime());      // resume timer on resuming the game
                countDown.start();  // resume timer on resuming the game
                resumeEntities(entities); // resume entity timers
                gamePlayPanel.showGamePanel();
                isPause = false;
                //gamePlayPanel.requestFocusInWindow(); // Refocus on the game panel
            }
        });

        gamePlayPanel.getRestartButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Restart the game
                System.out.println("Restarting Game");
                countDown.stop();
                gameStateManager.popState(); // Exit the current state
                gameStateManager.pushState(new GameplayState(gameStateManager, mnClient)); // Start a new game state
            }
        });

        gamePlayPanel.getExitButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Exit to the main menu
                System.out.println("Exiting to Main Menu");
                gameStateManager.setState(new MainMenuState(gameStateManager, mnClient));
            }
        });
    }
   
    private void movePlayerUp() {
        // Logic to move the player up
        //System.out.println("moving up");
        chef.moveUp();
    }

    private void movePlayerDown() {
        // Logic to move the player down
        //System.out.println("moving down");
        chef.moveDown();
    }

    private void movePlayerLeft() {
        // Logic to move the player left
        //System.out.println("moving Left");
        chef.moveLeft();
    }

    private void movePlayerRight() {
        // Logic to move the player right
        //System.out.println("moving right");
        chef.moveRight();
    }

    private void performFunctionAction() {
        // Logic to perform a function action (e.g., interact with an object)
        try{
        chef.checkInteract(entities);
        } catch (Exception e){
            e.getMessage();
        }
    }
    
    public void pauseGame() {
        // Logic to pause the game, such as pushing a PauseMenuState onto the state stack
        System.out.println("trying to Pause Game");
        if(gamePlayPanel != null){
            isPause = true;
            countDown.stop();   // stop timer while paused
            pauseEntities(entities); // stop any timers in entities
            gamePlayPanel.showPausePanel();
            gamePlayPanel.requestFocusInWindow();
        } else {
            logger.info("pausePanel is null in handleInput(). Please check initialization.");
        }
    }

    public void setCount(int num) {
        count = num;
    }
    
}
