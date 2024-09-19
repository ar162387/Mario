// package minigames.client.snake;

// import static org.junit.jupiter.api.Assertions.*;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import static org.mockito.Mockito.*;

// import javax.swing.*;
// import java.awt.*;
// import minigames.client.snake.ui.SnakeMainMenu;
// import minigames.client.MinigameNetworkClient;
// import minigames.client.MinigameNetworkClientWindow;

// /**
//  * This class contains unit tests for the SnakeMainMenu class, which represents the main menu of the Snake game.
//  * The tests cover the initialisation of the menu, the functionality of the buttons, and the layout of the components.
//  */

// class SnakeMainMenuTest {

//     private SnakeMainMenu snakeMainMenu;
//     private MinigameNetworkClient networkClient;
//     private JPanel mainPanel;

//     /**
//      * Sets up the test environment by initialising the SnakeMainMenu and its components
//      * before each test. Mocks the necessary dependencies.
//      */
//     @BeforeEach
//     void setUp() {
//         // Reinitialize the SnakeMainMenu and mainPanel before each test
//         networkClient = mock(MinigameNetworkClient.class);
    
//         // Directly create an actual JFrame instead of mocking it
//         JFrame realFrame = new JFrame();
//         MinigameNetworkClientWindow mainWindow = new MinigameNetworkClientWindow(realFrame);
        
//         when(networkClient.getMainWindow()).thenReturn(mainWindow);
        
//         snakeMainMenu = new SnakeMainMenu(networkClient);
//         snakeMainMenu.show();  // Initialize the mainPanel and other components
        
//         mainPanel = snakeMainMenu.getMainPanel();
//     }

//     /**
//      * Tests that the main panel of the SnakeMainMenu is correctly initialised.
//      */
//     @Test
//     void testMainPanelInitialisation() {

//         // Ensure that the main panel is not null
//         assertNotNull(mainPanel);
//      }

//     /**
//      * Tests that the CardLayout is correctly initialised and set on the main panel of the SnakeMainMenu.
//      */
//     @Test
//     void testCardLayoutInitialisation() {

//         // Ensure that the CardLayout is initialised and set on the main panel
//         assertTrue(mainPanel.getLayout() instanceof CardLayout);
//     }

//     /**
//      * Tests the functionality of the "Play" button in the SnakeMainMenu.
//      * Simulates a button click and verifies that the game starts as expected.
//      */
//     @Test
//     void testPlayButtonFunctionality() {

//         // Find the Play button and simulate a click
//         JButton playButton = findButtonByText("Play");
//         playButton.doClick();

//         // Check that the expected method is called (for example, starting the game)
//         verify(networkClient.getMainWindow(), times(1)).clearAll();
//     }

//     /**
//      * Tests the functionality of the "High Scores" button in the SnakeMainMenu.
//      * Simulates a button click and checks that the "High Scores" card is displayed.
//      */
//     @Test
//     void testHighScoresButtonFunctionality() {
//         JButton highScoresButton = findButtonByText("High Scores");
    
//         // Simulate a button click
//         highScoresButton.doClick();
    
//         // Check that the "High Scores" card is shown
//         boolean highScoresVisible = false;
    
//         for (Component comp : mainPanel.getComponents()) {
//             if (comp.isVisible() && comp instanceof JPanel) {
//                 Component[] components = ((JPanel) comp).getComponents();
//                 for (Component innerComp : components) {
//                     if (innerComp instanceof JLabel) {
//                         JLabel label = (JLabel) innerComp;
//                         if ("High Scores (To be implemented)".equals(label.getText())) {
//                             highScoresVisible = true;
//                             break;
//                         }
//                     }
//                 }
//             }
//         }
    
//         assertTrue(highScoresVisible);
//     }

//     /**
//      * Tests the functionality of the "Achievements" button in the SnakeMainMenu.
//      * Simulates a button click and verifies that the "Achievements" card is displayed.
//      */
//     @Test
//     void testAchievementsButtonFunctionality() {
//         JButton achievementsButton = findButtonByText("Achievements");
    
//         // Simulate a button click
//         achievementsButton.doClick();
    
//         // Check that the "Achievements" card is shown
//         boolean achievementsVisible = false;
//         for (Component comp : mainPanel.getComponents()) {
//             if (comp.isVisible()) {
//                 // Check if this component is the Achievements panel
//                 if (comp instanceof JPanel) {
//                     Component[] components = ((JPanel) comp).getComponents();
//                     for (Component innerComp : components) {
//                         if (innerComp instanceof JLabel) {
//                             JLabel label = (JLabel) innerComp;
//                             if ("Achievements (To be implemented)".equals(label.getText())) {
//                                 achievementsVisible = true;
//                                 break;
//                             }
//                         }
//                     }
//                 }
//             }
//         }
//         assertTrue(achievementsVisible);
//     }

//     /**
//      * Helper method to find a JButton by its text within the main panel of the SnakeMainMenu.
//      * Throws an IllegalArgumentException if the button is not found.
//      *
//      * @param text The text of the button to find.
//      * @return The JButton with the specified text.
//      */
//     private JButton findButtonByText(String text) {
//         JButton button = findButtonByText(mainPanel, text);
//         if (button == null) {
//             throw new IllegalArgumentException("No button with text: " + text);
//         }
//         return button;
//     }
    
//     /**
//      * Recursive helper method to find a JButton by its text within a given container.
//      *
//      * @param container The container to search within.
//      * @param text The text of the button to find.
//      * @return The JButton with the specified text, or null if not found.
//      */
//     private JButton findButtonByText(Container container, String text) {
//         for (Component comp : container.getComponents()) {
//             System.out.println("Checking component: " + comp.getClass().getName() + " with text: " + 
//                                 ((comp instanceof JButton) ? ((JButton) comp).getText() : "Non-button component"));
//             if (comp instanceof JButton && ((JButton) comp).getText().equals(text)) {
//                 return (JButton) comp;
//             } else if (comp instanceof Container) {
//                 JButton button = findButtonByText((Container) comp, text);
//                 if (button != null) {
//                     return button;
//                 }
//             }
//         }
//         return null;  // Return null if button not found in this container
//     }
// }