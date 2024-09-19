package minigames.client.RodentsRevenge;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class GamePanel extends JPanel {
    public final int tileSize = 32;
    public static final int screenCol = 25;
    public static final int screenRow = 19;
    public final int screenWidth = tileSize * screenCol;
    public final int screenHeight = tileSize * screenRow;
    TileMap tileMap = new TileMap(this);
    TimerScorePanel timerScorePanel = new TimerScorePanel();


    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true); // Used to improve rendering performance

        this.setFocusable(true); // GamePanel 'focused' to receive key input
        this.requestFocusInWindow();

        setLayout(new BorderLayout());
        this.add(timerScorePanel, BorderLayout.NORTH); // add timerScorePanel

    }


    /**
     * The paintComponent renders the graphics onto the screen
     */
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g; // Graphics2D extends Graphics class to provide better control over graphics
        tileMap.draw(g2);

    }

    public void setTailMap(int[][] tilemap ) {

        tileMap.setTilemap(tilemap);
    }

    public void setTime(int time) {
        timerScorePanel.setTime(time);
    }

    public void setNumOfDeath(int num) {
        timerScorePanel.setNumOfDeath(num);
    }

    public void setScore(int score) {timerScorePanel.setScore(score);}
}
