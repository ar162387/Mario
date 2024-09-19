package minigames.client.deepfried;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import minigames.client.MinigameNetworkClient;
import minigames.client.deepfried.states.GameCompleteState;
import minigames.client.deepfried.states.GameplayState;
import minigames.client.deepfried.states.MainMenuState;

@SuppressWarnings("unused")
public class GameStateManagerTest {

    private GameStateManager gameStateManager;
    private MinigameNetworkClient mnClientMock;
    private MainMenuState mainMenuStateMock;
    private GameplayState gameplayStateMock;
    private GameCompleteState gameCompleteStateMock;


    @BeforeEach
    void setUp() {
        // Create mocks for all necessary objects
        mnClientMock = mock(MinigameNetworkClient.class);
        mainMenuStateMock = mock(MainMenuState.class);
        gameplayStateMock = mock(GameplayState.class);
        gameCompleteStateMock = mock(GameCompleteState.class);

        // Initialize the real GameStateManager with mocked MinigameNetworkClient
        gameStateManager = new GameStateManager(mnClientMock);
    }
 
    @Test
    void testSetStatetmainMenuStateMock() {
        gameStateManager.setState(mainMenuStateMock);
        assertEquals(mainMenuStateMock, gameStateManager.getCurrentState());
        verify(mainMenuStateMock).enter();
    }

    @Test
    void testPushmainMenuStateMock() {
        gameStateManager.pushState(mainMenuStateMock);
        assertEquals(mainMenuStateMock, gameStateManager.getCurrentState());
        verify(mainMenuStateMock).enter();
    }

    @Test
    void testMainMenuToGamePlayState() {
        gameStateManager.pushState(mainMenuStateMock);
        gameStateManager.pushState(gameplayStateMock);
        assertEquals(gameplayStateMock, gameStateManager.getCurrentState());
        verify(mainMenuStateMock).exit();
        verify(gameplayStateMock).enter();
    }

    @Test
    void testGamePlayStateToGameCompleteState() {
        gameStateManager.pushState(gameplayStateMock);
        gameStateManager.pushState(gameCompleteStateMock);
        assertEquals(gameCompleteStateMock, gameStateManager.getCurrentState());
        verify(gameplayStateMock).exit();
        verify(gameCompleteStateMock).enter();
    }

    @Test
    void testGameCompleteStateToMainMenuState() {
        gameStateManager.pushState(gameCompleteStateMock);
        gameStateManager.setState(mainMenuStateMock);
        assertEquals(mainMenuStateMock, gameStateManager.getCurrentState());
        verify(gameCompleteStateMock).exit();
        verify(mainMenuStateMock, atLeastOnce()).enter();
    }

    @Test
    void testPopState() {
        gameStateManager.pushState(mainMenuStateMock);
        gameStateManager.pushState(gameplayStateMock);
        gameStateManager.popState();
        
        assertEquals(mainMenuStateMock, gameStateManager.getCurrentState());
        verify(gameplayStateMock).exit();
        verify(mainMenuStateMock, atLeastOnce()).enter();
    }
    @Test
    void testStackIntegrity() {
        gameStateManager.pushState(mainMenuStateMock);
        gameStateManager.pushState(gameplayStateMock);
        gameStateManager.popState(); // Should pop gameplayStateMock

        assertEquals(mainMenuStateMock, gameStateManager.getCurrentState());
        assertEquals(1, gameStateManager.getStateCount());

        gameStateManager.pushState(gameCompleteStateMock);
        assertEquals(gameCompleteStateMock, gameStateManager.getCurrentState());
        assertEquals(2, gameStateManager.getStateCount());
    }

    @Test
    void testPushNullState() {
        gameStateManager.pushState(mainMenuStateMock);
        assertEquals(mainMenuStateMock, gameStateManager.getCurrentState());

        gameStateManager.pushState(null); // Attempt to push a null state
        assertEquals(mainMenuStateMock, gameStateManager.getCurrentState()); // Ensure the current state remains unchanged
        assertEquals(1, gameStateManager.getStateCount()); 
    }

    @Test
    void testSetNullState() {
        gameStateManager.pushState(mainMenuStateMock);
        assertEquals(mainMenuStateMock, gameStateManager.getCurrentState());

        gameStateManager.setState(null); // Attempt to set a null state
        assertEquals(mainMenuStateMock, gameStateManager.getCurrentState()); // Ensure the current state remains unchanged
        assertEquals(1, gameStateManager.getStateCount()); 
    }

    @Test
    void testPopStateOnEmptyStack() {
        gameStateManager.popState(); // Attempt to pop from an empty stack
        assertNull(gameStateManager.getCurrentState());
        assertEquals(0, gameStateManager.getStateCount());
    }

    @Test
    void testRePushSameState() {
        gameStateManager.pushState(mainMenuStateMock);
        gameStateManager.pushState(mainMenuStateMock); // Attempt to push the same state again
        
        assertEquals(mainMenuStateMock, gameStateManager.getCurrentState());
        assertEquals(1, gameStateManager.getStateCount()); 
    }

    @Test
    void testGetCurrentStateWhenEmpty() {
        assertNull(gameStateManager.getCurrentState());
    }

    @Test
    void testStackIsEmpty() {
        assertTrue(gameStateManager.isEmpty()); // Initially, the stack should be empty

        gameStateManager.pushState(mainMenuStateMock);
        assertFalse(gameStateManager.isEmpty()); // Stack should not be empty after push
        
        gameStateManager.popState();
        assertTrue(gameStateManager.isEmpty()); // Stack should be empty after popping the only state
    }


}