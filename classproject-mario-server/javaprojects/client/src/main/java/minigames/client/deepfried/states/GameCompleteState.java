package minigames.client.deepfried.states;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

import minigames.client.MinigameNetworkClient;
import minigames.client.deepfried.GameStateManager;
import minigames.client.deepfried.UI.GameOVerUI;

public class GameCompleteState extends GameState{
    
    private GameStateManager gameStateManager;
    private MinigameNetworkClient mnClient;
    private int score;

    
    //private CardLayout cardLayout;
    public GameOVerUI gameOverPanel;


    /**
     * Class handles a gameover from pause menu or from inside gameplay
     * 
     */
    public GameCompleteState(GameStateManager gameStateManager,MinigameNetworkClient mnClient, int score) {

        this.gameStateManager = gameStateManager;
        this.mnClient = mnClient;
        this.score = score;
                //Original code: how the Main menu handles components on the mainWindow


        // call the UI as a Jpanel as it now extends JPanel
        gameOverPanel = new GameOVerUI();

        // setup listteners for pause Menu Buttons
        setupButtonListeners();

        // Add the Input object as a KeyListener
        gameOverPanel.setFocusable(true); 
        gameOverPanel.requestFocusInWindow();

        // add gameplay screen to main Client screen
        mnClient.getMainWindow().clearAll();
        mnClient.getMainWindow().addCenter(gameOverPanel);
        mnClient.getMainWindow().pack();

        JFrame frame = mnClient.getMainWindow().getFrame();
        frame.setTitle("DeepFried");
        frame.setSize(1400,900);
        frame.setResizable(true);
        frame.setVisible(true);
        

    }

    public void displayGameOverInNewFrame() {
            // Create a new frame to test the display of gameOverPanel
            JPanel testFrame = new JPanel();
            //testFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close only this test frame when closing
    
            // Set size and layout for the test frame
            testFrame.setSize(1000, 800);
            testFrame.setLayout(new BorderLayout());
    
            // Add the gameOverPanel to the test frame
            testFrame.add(gameOverPanel, BorderLayout.CENTER);
    
            // Make sure the frame is visible
            testFrame.setVisible(true);
    
            // Revalidate and repaint to ensure everything is properly displayed
            testFrame.revalidate();
            testFrame.repaint();
    }
        
    @Override
    public void enter() {
        // Code to execute when the Gameplay state is entered
        if (gameOverPanel != null){
            gameOverPanel.showGameOverPanel();
            } else {
                System.out.println("GameOverPanel is null in enter(). Please check initialization.");
            }
    }

    @Override
    public void exit() {
        // Code to execute when exiting the Gameplay state
        if (gameOverPanel != null) {
            gameOverPanel.setVisible(false);
        }
    }

    @Override
    public void update() {
        // Handle user input for the Gameplay
    }

    @Override
    public void render(Graphics g) {
        // Draw the Gameplay components on the screen
        if (g != null && gameOverPanel != null) {
            //gameOverPanel.paint(g);
            gameOverPanel.repaint();
        }
    }


    public void setupButtonListeners() {
        gameOverPanel.getReturnButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // return to main menu
                System.out.println("Returning to Main Menu");
                mnClient.getMainWindow().clearAll();
                gameStateManager.setState(new MainMenuState(gameStateManager, mnClient));
            }
        });
    }
        
}
