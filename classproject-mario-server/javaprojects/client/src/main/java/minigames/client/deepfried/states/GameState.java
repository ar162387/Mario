package minigames.client.deepfried.states;

import java.awt.Graphics;

public abstract class GameState {
    /**
     * interface/ abstract class, for all game states
     * 
     * methods
     * enterState(), update(), and exitState()
     */

    // Called every frame to update the game logic
    public abstract void update();

    // Called every frame to render the current state
    public abstract void render(Graphics g);

    // Called when the state is entered
    public abstract void enter();

    // Called when the state is exited
    public abstract void exit();
    
}
