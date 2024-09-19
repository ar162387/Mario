package minigames.client.gwent;

import minigames.client.MinigameNetworkClient;
import minigames.client.MinigameNetworkClientWindow;
import minigames.client.gwent.ui.MainMenu;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MainMenuTest {

    private JFrame frame;
    private MinigameNetworkClient networkClient;
    private MinigameNetworkClientWindow mainWindow;
    private GameController gameController;

    /**
     * Before each test create a new main menu
     */
    @BeforeEach
    public void setUp() {
        // Create a new JFrame for the test
        frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Create mocks
        networkClient = mock(MinigameNetworkClient.class);
        mainWindow = mock(MinigameNetworkClientWindow.class);

        // Set up mock interactions
        when(networkClient.getMainWindow()).thenReturn(mainWindow);

        // Create and display the main menu via GameController
        gameController = new GameController(networkClient);

        // Mock the behavior of addCenter to add the Component to the frame's content pane and make it visible
        doAnswer(invocation -> {
            Component component = invocation.getArgument(0);
            frame.getContentPane().add(component, BorderLayout.CENTER);
            frame.pack();
            frame.setVisible(true);
            return null;
        }).when(mainWindow).addCenter(any(Component.class));

        // Mock the behavior of clearAll to remove all components from the frame's content pane
        doAnswer(invocation -> {
            frame.getContentPane().removeAll();
            return null;
        }).when(mainWindow).clearAll();

        // Create and display the main menu
        MainMenu mainMenu = new MainMenu(gameController);
        mainMenu.show();
    }

    /**
     * After each test dispose of the created frame.
     */
    @AfterEach
    public void cleanUp() {
        if (frame != null) {
            frame.dispose();
        }
    }

    // /**
    //  * Test if main menu and its components exist.
    //  */
    // @Test
    // public void shouldDisplayMainMenu() {
    //     // Check if the frame is visible
    //     assertTrue(frame.isVisible());

    //     // Check if the main menu label and buttons exist
    //     boolean titleLabelFound = false;
    //     boolean startButtonFound = false;
    //     boolean exitButtonFound = false;

    //     Component[] components = frame.getContentPane().getComponents();
    //     // Loop through components
    //     for (Component component : components) {
    //         // Found the JPanel
    //         if (component instanceof JPanel panel) {
    //             // Loop through the components on the Panel
    //             for (Component panelComponent : panel.getComponents()) {
    //                 // Check if we found our label and buttons
    //                 if (panelComponent instanceof JLabel label) {
    //                     if ("Welcome to Gwent!".equals(label.getText())) {
    //                         titleLabelFound = true;
    //                     }
    //                 } else if (panelComponent instanceof JButton button) {
    //                     if ("Start New Game".equals(button.getText())) {
    //                         startButtonFound = true;
    //                     } else if ("Exit".equals(button.getText())) {
    //                         exitButtonFound = true;
    //                     }
    //                 }
    //             }
    //         }
    //     }

    //     assertTrue(titleLabelFound, "Title label not found");
    //     assertTrue(startButtonFound, "Start New Game button not found");
    //     assertTrue(exitButtonFound, "Exit button not found");
    // }

    /**
     * Test if the start game button correctly starts a new game.
     */
    @Test
    @Disabled
    public void shouldStartNewGame() {
        // TODO: Test that start game button launches a new game
    }

    /**
     * Helper method to find a button by its text.
     *
     * @param text The button text to search for.
     */
    private JButton findButton(String text) {
        Component[] components = frame.getContentPane().getComponents();
        // Loop through the components
        for (Component component : components) {
            // Found a JPanel
            if (component instanceof JPanel panel) {
                // Loop through components on JPanel
                for (Component panelComponent : panel.getComponents()) {
                    // Found button
                    if (panelComponent instanceof JButton button) {
                        // Check button text
                        if (text.equals(button.getText())) {
                            return button;
                        }
                    }
                }
            }
        }
        return null;
    }
}
