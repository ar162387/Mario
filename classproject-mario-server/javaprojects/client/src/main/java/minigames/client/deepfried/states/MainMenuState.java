package minigames.client.deepfried.states;

import minigames.client.MinigameNetworkClient;
import minigames.client.deepfried.GameStateManager;
import minigames.client.deepfried.UI.MainMenuUI;

import java.awt.Graphics;
import javax.swing.JFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenuState extends GameState{
    /**
     * Class for the main menu and event listeners for the main menu
     * 
     */
    // UI panel for the main menu
    private MainMenuUI mainMenuUI;
    private GameStateManager gameStateManager;
    @SuppressWarnings("unused")
    private MinigameNetworkClient mnClient;

    public MainMenuState(GameStateManager gameStateManager, MinigameNetworkClient mnClient) {

        this.gameStateManager = gameStateManager;
        this.mnClient = mnClient;
        
        mainMenuUI = new MainMenuUI();

        setupButtonListeners(mnClient);

        // Add the MainMenuUI to the main window
        mnClient.getMainWindow().clearAll();
        mnClient.getMainWindow().addCenter(mainMenuUI);
        mnClient.getMainWindow().pack();

        JFrame frame = mnClient.getMainWindow().getFrame();
        frame.setTitle("DeepFried");
        frame.setSize(1400, 900);
        frame.setResizable(true);
        frame.setVisible(true);
    }

    @Override
    public void enter() {
        // Code to execute when the main menu state is entered
        if (mainMenuUI != null) {
            mainMenuUI.showTutorial();
        } else {
            System.out.println("TutorialPanel is null in enter(). Please check initialization.");
        }
        if (mainMenuUI != null) {
            mainMenuUI.showMainMenu();
        } else {
            System.out.println("mainMenuPanel is null in enter(). Please check initialization.");
        }
    }

    @Override
    public void exit() {
        // Code to execute when exiting the main menu state
        if (mainMenuUI != null) {
            mainMenuUI.setVisible(false); // Hide the MainMenuUI
            mainMenuUI.removeAll();
    
        }
        // reclaim memory to run rest of game
        mainMenuUI = null;
    }

    @Override
    public void update() {
        // Handle dynamic UI for the main menu
    }

    @Override
    public void render(Graphics g) {
        // repaint the main menu and tutorial on the screen
        if (mainMenuUI != null && mainMenuUI.isVisible()) {
            mainMenuUI.paint(g);
        }
    }


    private void setupButtonListeners(MinigameNetworkClient mnClient) {
        // Setup all button listeners
        mainMenuUI.getStartButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Start Game Button clicked");
                // Transition to GameplayState
                gameStateManager.pushState(new GameplayState(gameStateManager, mnClient)); 
            }
        });

        // Set up listeners for the tutorial button to show the tutorial panel
        mainMenuUI.getTutorialButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("How to Play Button clicked");
                mainMenuUI.showTutorial(); // Show the tutorial panel
            }
        });
        // set up return to menu button
        mainMenuUI.getReturnButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("How to Play Button clicked");
                mainMenuUI.showMainMenu(); // Show the tutorial panel
            }
        });
        
            }

}


