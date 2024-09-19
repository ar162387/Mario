package minigames.client.smallworld;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;

public class Keyboard extends KeyAdapter{
     
    private HashSet<Integer> keyCodes = new HashSet<Integer>();
    public boolean up, down, left, right;
    public boolean sudoUp, sudoDown, sudoLeft, sudoRight;
    public boolean previous, next;
    public boolean showGameMenu;

    public void update() {
        up = keyCodes.contains(KeyEvent.VK_UP);
        down = keyCodes.contains(KeyEvent.VK_DOWN);
        left = keyCodes.contains(KeyEvent.VK_LEFT);
        right = keyCodes.contains(KeyEvent.VK_RIGHT);
        
        sudoUp = keyCodes.contains(KeyEvent.VK_W);
        sudoDown =  keyCodes.contains(KeyEvent.VK_S);
        sudoLeft = keyCodes.contains(KeyEvent.VK_A);
        sudoRight = keyCodes.contains(KeyEvent.VK_D);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keyCodes.add(e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keyCodes.remove(e.getKeyCode());
    }

    /**
     * Set values for keys we only want to check once per press (i.e. not every frame)
     * 
     * Currently this is used to tab backwards/forwards through a list of Tiles to place
     * 
     * Could put menu keybinds here
     * 
     */
    @Override
    public void keyTyped(KeyEvent e) {
        if(e.getKeyChar() == 'q')
            previous = true;
        if(e.getKeyChar() == 'e')
            next = true;
        if(e.getKeyChar() == 'm')
            showGameMenu = true;
    }
   
}
