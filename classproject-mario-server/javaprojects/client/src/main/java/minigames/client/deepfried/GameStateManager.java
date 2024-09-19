package minigames.client.deepfried;

import minigames.client.MinigameNetworkClient;
import minigames.client.deepfried.input.Input;
import minigames.client.deepfried.states.GameState;
import java.awt.Graphics;
import java.util.Stack;

public class GameStateManager {
    /**
     * Manages the transitions between different game states
     * 
     */
    // Stack to manage the game states
    private Stack<GameState> stateStack;
    // input instance that will be passed down to the gameStates and the entities
    private Input input;
    private MinigameNetworkClient mnClient;
 
    // called when game is initialised ("open game" in client main menu)
    public GameStateManager(MinigameNetworkClient mnClient) {
        stateStack = new Stack<>();
        input = new Input();
        this.mnClient = mnClient;
    }

    /**
     * Push a new state onto the stack
     * tasks:
     *  - Call enter() on the new state
     *  - Push the new state onto the stack
     */
    public void pushState(GameState state) {
            if (state == null) {
                System.out.println("Cannot push null state onto the stack.");
                return; // Exit early if the state is null
            }
        
            try {
                // Optional: Check if the same state instance is already on top of the stack to avoid duplicates
                if (!stateStack.isEmpty() && stateStack.peek() == state) {
                    System.out.println("State is already the current state; no need to push.");
                    return; // Exit early if the same state is already at the top
                }
                if (!stateStack.isEmpty()) {
                    GameState currentState = stateStack.peek(); // Get the current state
                    currentState.exit(); // Exit the current state
                }
                //stateStack.peek().exit();
                stateStack.push(state); // Push the state onto the stack
                System.out.println("Entering new state: " + state.getClass().getSimpleName());
                state.enter(); // Directly call enter() on the newly pushed state
            } catch (Exception e) {
                System.out.println(e.getMessage()); // Print the stack trace for better debugging
            }

            if (state != null) {
                //System.out.println("state "+ state.getClass().getSimpleName() + " Is NOT NULL in PushState()");
            }
    }

    /**
     * Pop the current state from the stack
     * 
     * tasks:
     * - Call exit() on the current state
     * - Remove the current state from the stack
     * - Call enter() on the new top state, if any
     */
    public void popState() {

        if (!stateStack.isEmpty()) {
            GameState currentState = stateStack.peek(); // Get the current state at the top of the stack
            currentState.exit(); // Call exit() on the current state before removing it
            stateStack.pop(); // Now, safely remove the state from the stack
    
            if (!stateStack.isEmpty()) {
                GameState newState = stateStack.peek(); // Get the new top state
                newState.enter(); // Call enter() on the new top state
            } else {
                System.out.println();
                System.out.println("StateStack is now empty after popping state.");
                System.out.println();
            }
        } else {
            System.out.println();
            System.out.println("Cannot pop state: StateStack is already empty!");
            System.out.println();
        }
    }

    /**
     * Initialise Main Menu state when game is called. If game has a fault, this should be called as a default.
     * tasks:
     * - Clear the stack of all states
     * - Call enter() on the new state
     * - Add the new state to the stack
     */
    public void setState(GameState state) {
        // pop all states from stack and implement the desired state

        // Check if the new state is null
        if (state == null) {
            System.out.println("Cannot set a null state.");
            return; // Exit early if the state is null
        }

        // Check if the new state is already the current state
        // may break the gameplay state when restart game is called
        if (!stateStack.isEmpty() && stateStack.peek() == state) {
            System.out.println("The new state is already the current state so no need to set.");
            return; 
        }

        // Log the state stack size before the operation
        System.out.println("State stack size before set: " + getStateCount());

        // reset stack size to empty
        reset();
        
        if (isEmpty()){
            System.out.println("Stack is Reset in reset(). Stack size: " + getStateCount());
        }

        // Push the new state onto the stack and enter
        stateStack.push(state);
        state.enter(); 

        // Log the state stack size after the operation
        System.out.println("State stack size after set: " + getStateCount() + " with state: " + state.getClass().getSimpleName());

    }

    public void reset() {
        // Clear all states from the stack
        while (!stateStack.isEmpty()) {
            GameState currentState = stateStack.pop();
            currentState.exit(); // Call exit() for each state
        }
        System.out.println("State stack has been fully reset.");
    }

    // Get the current state at the top of the stack
    public GameState getCurrentState() {
        // Return the state at the top of the stack without removing it (peek)
        if(!stateStack.isEmpty()){
            return stateStack.peek();
        } else {
            System.out.println("StateStack NULL!!!");
            return null;
        }
    }

    /**
     * Update the current state.
     * tasks: 
     *  - Call update(input) on the current state
     */
    public void update() {
        if (!stateStack.isEmpty()){
            //System.out.println("State is "+stateStack.peek());
            stateStack.peek().update();
        }
    }

    /**
     * Render the current state
     * tasks: 
     *  - Call render(g) on the current state
     */
    public void render(Graphics g) {
        if (!stateStack.isEmpty()){
            stateStack.peek().render(g);
        }

    }

    /**
     * Check if the state stack is empty
     * tasks: 
     *  - Return true if there are no states in the stack
     */
    public boolean isEmpty() {
         if (stateStack.size() == 0){
            return true;
        } else {
            return false;
        }
    }

    // Get the number of states in the stack
    public int getStateCount() {
        // Return the number of states in the stack
        int stackCount = stateStack.size();
        return stackCount;
    }

    public Input getInput() {
        return input;
    }

    public MinigameNetworkClient getMnClient() {
        return mnClient;
    }


}
