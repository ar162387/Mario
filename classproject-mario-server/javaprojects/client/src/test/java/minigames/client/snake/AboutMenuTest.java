package minigames.client.snake;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import javax.swing.JButton;
import javax.swing.JPanel;

import java.awt.event.ActionListener;

import minigames.client.snake.ui.AboutMenu;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AboutMenuTest {

    private AboutMenu aboutMenu;
    private ActionListener mockBackToMenuListener;

    @BeforeEach
    void setUp() {

        mockBackToMenuListener = mock(ActionListener.class);
        aboutMenu = new AboutMenu(mockBackToMenuListener);
    }

    @Test
    void testAboutPanelNotNull() {

        // Ensure that the aboutPanel is created and not null
        JPanel aboutPanel = aboutMenu.getAboutPanel();
        assertNotNull(aboutPanel);
    }

    /**
     * 
     */
    @Test
    void testBackButtonAddedToPanel() {

        // Check that the back button is added to the aboutPanel
        JPanel aboutPanel = aboutMenu.getAboutPanel();
        JButton backButton = findButtonInPanel(aboutPanel, "Back to Menu");
        
        assertNotNull(backButton);
        assertEquals(300, backButton.getBounds().x);
        assertEquals(600, backButton.getBounds().y);
        assertEquals(200, backButton.getBounds().width);
        assertEquals(50, backButton.getBounds().height);
    }

    /**
     * Tests that the action listener for the back to menu button triggers event correctly and is 
     * appled to the button correctly
     */
    @Test
    void testBackButtonActionListener() {

        // Ensure the "Back to Menu" button has the correct ActionListener
        JPanel aboutPanel = aboutMenu.getAboutPanel();
        JButton backButton = findButtonInPanel(aboutPanel, "Back to Menu");

        // Simulate a button click and verify that the action listener was triggered
        assertNotNull(backButton);
        backButton.doClick();
        verify(mockBackToMenuListener, times(1)).actionPerformed(any());
    }

    /**
     * Helper method to find a button with the correct text in a panel.
     */
    private JButton findButtonInPanel(JPanel panel, String text) {
        for (int i = 0; i < panel.getComponentCount(); i++) {
            if (panel.getComponent(i) instanceof JButton) {
                JButton button = (JButton) panel.getComponent(i);
                if (button.getText().equals(text)) {
                    return button;
                }
            }
        }
        return null;
    }
}
