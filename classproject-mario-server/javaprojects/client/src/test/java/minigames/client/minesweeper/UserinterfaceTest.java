package minigames.client.minesweeper;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;

import io.vertx.core.json.JsonObject;

/**
     * The Purpose of this class is to perform tests on the user interface class of minesweeper
     * Tests:
     * getAndSetTimeTest
     * getAndSetMinesRemainingTest
     * getAndSetUserTest
     * getAndSetScoreTest
     * testDockTopPanel
     * testMineAreaPanel
     * testMainPanel
     * formatTimeTest
     * onMinesRemainingChangedTest
     * scoreMultiplierTest
     * Author: Matt Hayes
     * 
     * testOptionsButtonExists() {
     * 
*/
     
public class UserinterfaceTest {
	
    private static final Logger logger = LogManager.getLogger(GridTest.class);
    private GameController gameController;
    private Leaderboard leaderboard;
    private Minesweeper minesweeper;
    private userInterface userInterface;
    private user user;
    private JsonObject gameTempData;
    private Menu mockMenu;
    
        /**
     * Searches for a JButton within a given container by matching the button's text.
     * This method recursively searches through all components in the container,
     * including any nested containers such as JPanels.
     *
     * @param container The parent container (e.g., JPanel) in which to search for the button.
     * @param buttonText The text label of the button to search for.
     * @return The JButton if found, or null if no button with the specified text is found.
     */
    private JButton findButtonByLabelInPanel(java.awt.Container container, String buttonText) {
        for (java.awt.Component component : container.getComponents()) {
            if (component instanceof JButton) {
                JButton button = (JButton) component;
                if (button.getText().equals(buttonText)) {
                    return button;
                }
            } else if (component instanceof java.awt.Container) {
                // Recursively search in nested containers (e.g., nested JPanel)
                JButton button = findButtonByLabelInPanel((java.awt.Container) component, buttonText);
                if (button != null) {
                    return button;
                }
            }
        }
        return null;
    }

    @BeforeEach
    public void setUp() {  
        minesweeper = mock(Minesweeper.class);
		gameController = new GameController(minesweeper);
		user user = new user("Matt", 10);
        userInterface = new userInterface(user, gameController);
        mockMenu = mock(Menu.class);
        userInterface.setMenu(mockMenu);

    }
     /**
     * Tests the get and set difficulty methods
     */
    @Test
    public void getAndSetDifficultyTest(){
        logger.info("Tests the get and set difficulty methods");
        userInterface.setDifficulty("Easy");
        assertEquals(userInterface.getDifficulty(), "Easy");
    }
    
     /**
     * Tests the get and set time methods
     */
    @Test
    public void getAndSetTimeTest(){
        logger.info("Tests the get and set time methods");
        userInterface.setTime(120);
        assertEquals(userInterface.getTime(), 120);
    }
    
     /**
     * Tests the get and set mines remaining methods
     */
    @Test
    public void getAndSetMinesRemainingTest(){
        logger.info("Tests the get and set mines remaining methods");
        userInterface.setMinesRemaining(10);
        assertEquals(userInterface.getMinesRemaining(), 10);
    } 
     
     /**
     * Tests the get and set user methods
     */
    @Test
    public void getAndSetUserTest(){
        logger.info("Tests the get and set user methods");
        user user = new user("Matt", 10);
        assertEquals(user.getUserName(), "Matt");
        user.setUserName("Bob");
        assertEquals(user.getUserName(), "Bob");
    }  
    
     /**
     * Tests the get and set Score methods
     */
    @Test
    public void getAndSetScoreTest(){
        logger.info("Tests the get and set score methods");
        userInterface.setScore(10);
        assertEquals(userInterface.getScore(), 10);
    }   
    
     /**
     * Tests the creation of dockTop panel
     */
    @Test
    public void testDockTopPanel(){
        logger.info("Test to see if the Dock Top panel works");
	 	  
        // Get the dockTop panel
        JPanel dockTopPanel = userInterface.getDockTop();
		  assertNotNull(dockTopPanel, "dockTop panel should not be null");
    } 
    
     /**
     * Tests the creation of Mine Area panel
     */
    @Test
    public void testMineAreaPanel(){
        logger.info("Test to see if the Mine Area panel works");
	 	  
        // Get the Mine Area panel
        JPanel mineAreaPanel = userInterface.getMineArea("Easy" , gameTempData);
		  assertNotNull(mineAreaPanel, "Mine Area panel should not be null");
    }   
    
    /**
     * Tests the creation of MainPanel panel
     */
    @Test
    public void testMainPanel(){
        logger.info("Test to see if the Main Panel panel works");
	 	  
        // Get the Main anel panel
        JPanel mainPanel = userInterface.createMainPanel("Easy" , gameTempData);
		  assertNotNull(mainPanel, "main Panel should not be null");
    }  
    
     /**
     * Tests the format time function
     */
    @Test
    public void formatTimeTest(){
        logger.info("Test to see if the format time function works");
	 	  
        // Used to turn a time in seconds to mm:ss format
        String time = userInterface.formatTime(119);
		  assertEquals(time, "01:59");
    }  

    /**
     * Tests that the 'Options' button exists in the dockTop panel.
     */
    @Test
    public void testOptionsButtonExists() {
        logger.info("Tests to see if the 'Options' button exists in the dockTop panel.");
        JPanel dockTopPanel = userInterface.getDockTop();
    
        // Check if the "Options" button exists
        JButton optionsButton = findButtonByLabelInPanel(dockTopPanel, "Options");
        assertNotNull(optionsButton, "'Options' button should exist in the dockTop panel.");
    }
    
     /**
     * Tests the onMinesRemainingChanged function
     
    @Test
    public void onMinesRemainingChangedTest(){
        logger.info("Test to see if the onMinesRemainingChanged function works");
	 	  
        ?????
    }  
    */
    
    /**
     * Tests the scoreMultiplier function
     
    @Test
    public void scoreMultiplierTest(){
        logger.info("Test to see if the scoreMultiplier function works");
	 	  
        ?????
    }  
    */
    
}

   