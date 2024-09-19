package minigames.client.deepfried.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;


public class Input implements KeyListener {
    /**
     * handles keyboard input
     * 
     * Take keyboard inputs:
     * W, A, S, D, F, P
     * Assign a keyevent to the keys for mapping later.
     * 
     */

    private HashMap<Integer, Boolean> keyMap;

    Boolean up, down, left, right, function, pause, end, finish;
    
 
    public Input() {
        keyMap = new HashMap<>();
    }
 
    public void update() {
        up = keyMap.getOrDefault(KeyEvent.VK_W, false);
        down = keyMap.getOrDefault(KeyEvent.VK_S, false);
        left = keyMap.getOrDefault(KeyEvent.VK_A, false);
        right = keyMap.getOrDefault(KeyEvent.VK_D, false);
        function = keyMap.getOrDefault(KeyEvent.VK_F, false);
        pause = keyMap.getOrDefault(KeyEvent.VK_P, false);
        end = keyMap.getOrDefault(KeyEvent.VK_U, false);
        finish = keyMap.getOrDefault(KeyEvent.VK_M, false);
    }

     // Clear the key map and reset all input states
    public void clear() {
        // Clear the map to reset all key states
        keyMap.clear();

        // Reset all input state variables to false
        up = false;
        down = false;
        left = false;
        right = false;
        function = false;
        pause = false;
        end = false;
        finish = false;

        System.out.println("Input states have been cleared.");
    }
 
     @Override
     public void keyTyped(KeyEvent e) {
         // Not used, but must be implemented for "implements KeyListener"
     }
 
     @Override
     public void keyPressed(KeyEvent e) {
         keyMap.put(e.getKeyCode(), true);
     }
 
     @Override
     public void keyReleased(KeyEvent e) {
         keyMap.put(e.getKeyCode(), false);
     }
 
     public boolean isKeyPressed(int keyCode) {
         return keyMap.getOrDefault(keyCode, false);
     }
}
