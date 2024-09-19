package minigames.client.minesweeper;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import minigames.client.minesweeper.Grid;

enum State {
    NONE,
    FLAGGED,
    QUESTION
}


/**
 * Represents a single Tile on a game board
 */
public class Tile {

    // Coordinates in the grid
    private int col, row;
    // Size in pixels
    private int size = Grid.pxSize;
    // Parent object
    private Grid grid;

    private boolean mine = false;
    private boolean cleared = false;
    private int number = 0;
    private State state = State.NONE;

    // Constants for bits used for toInt()/fromInt() methods
    private static final int MINE_BIT = 16,
                             CLEARED_BIT = 32,
                             FLAGGED_BIT = 64,
                             QUESTION_BIT = 128;

    /**
     * Constructor of the Tile.
     *
     * @param row, num of row
     * @param col, num of column
     * @param grid, parent object Grid
     */
    public Tile(int col, int row, Grid grid) {
        this.col = col;
        this.row = row;
        this.grid = grid;
    }

    /**
     * Method to compact all tile properties into int number using bit flags
     * as follows:
     * 
     * (bits 0-3) 0-8 - Number of mines nearby (see int number)
     * (bit    4) 16  - Mine bit (see boolean mine)
     * (bit    5) 32  - Cleared bit (see boolean cleared)
     * (bit    6) 64  - State.FLAGGED (see State state)
     * (bit    7) 128 - State.QUESTION (see State state)
     */
    public int toInt() {
        return number +
            (mine ? MINE_BIT : 0) +
            (cleared ? CLEARED_BIT : 0) +
            (state == State.FLAGGED ? FLAGGED_BIT : 0) +
            (state == State.QUESTION ? QUESTION_BIT : 0);
    }

    public void fromInt(int val) {
        number = val & 15;
        mine = (val & MINE_BIT) > 0;
        cleared = (val & CLEARED_BIT) > 0;

        if ((val & FLAGGED_BIT) > 0) {
            state = State.FLAGGED;
        } else if ((val & QUESTION_BIT) > 0) {
            state = State.QUESTION;
        } else {
            state = State.NONE;
        }
    }

    // Make the tile a Mine
    public void setMine() { mine = true; }

    //Make the tile not a mine, testing purposes only
    public void removeMine() { mine = false; }

    // Check if the tile is a Mine
    public boolean isMine() { return mine; }

    // Check if the tile is cleared
    public boolean isCleared() { return cleared; }

    // Clear the tile
    public void clear(int num) {
        if (num > 8 || num < 0)
            throw new IllegalArgumentException("Invalid number of mines in the proximity");
        cleared = true;
        number = num;
    }

    // Get tile state
    public State getState() { return state; }
    
    // Set tile state
    public void setNextState() {
        switch (state) {
            case State.NONE:
                state = State.FLAGGED;
                break;
            case State.FLAGGED:
                state = State.QUESTION;
                break;
            case State.QUESTION:
                state = State.NONE;
                break;
        }
    }

    // Get the Left pixel coordinate of the tile
    private int getLeftCoord() { return grid.xOffset + col * size; }

    // Get the Top pixel coordinate of the tile
    private int getTopCoord() { return grid.yOffset + row * size; }

    /**
     * Method to render uncleared tile
     *
     * @param g2, Graphics2D object
     */
    private void renderUncleared(Graphics2D g2) {
        g2.setColor(Color.LIGHT_GRAY);
        g2.fill3DRect(getLeftCoord(),
                      getTopCoord(),
                      size, size, true);
        switch (state) {
            case State.FLAGGED:
                g2.drawImage(grid.mineFlag,
                             getLeftCoord() + (size - grid.mineFlag.getWidth()) / 2,
                             getTopCoord() + (size - grid.mineFlag.getHeight()) / 2,
                             grid);
                break;
            case State.QUESTION:
                drawString("?", g2, new Color(128, 128, 255));
                break;
        }
    }

    /**
     * Method to draw a string inside a tile
     *
     * @param s, string to draw (should be one character in length)
     * @param g2, Graphics2D object
     * @param c, color of the text
     */
    private void drawString(String s, Graphics2D g2, Color c) {
        // Coordinates to draw numbers
        int xf = getLeftCoord() + (size - grid.fontWidth) / 2;
        // Font is rendered from the bottom coordinate
        int yf = getTopCoord() + (size - grid.fontHeight) / 2 + grid.fontAscent;

        g2.setColor(c);
        g2.setFont(grid.font);
        g2.drawString(s, xf, yf);
    }

    /**
     * Method to render cleared tile
     *
     * @param g2, Graphics2D object
     */
    private void renderCleared(Graphics2D g2) {
        int xx = getLeftCoord();
        int yy = getTopCoord();
        
        g2.setColor(Color.LIGHT_GRAY);
        g2.fillRect(xx, yy, size, size);
        g2.setColor(Color.DARK_GRAY);
        g2.drawRect(xx, yy, size, size);

        if (number > 0) {
            String s = String.valueOf(number);

            switch(number) {
                case 1:
                    drawString(s, g2, Color.BLUE);
                    break;
                case 2:
                    drawString(s, g2, new Color(0, 128, 0)); // Dark Green
                    break;
                case 3: 
                    drawString(s, g2, Color.RED); 
                    break;
                case 4:
                    drawString(s, g2, new Color(0, 0, 128)); // Navy
                    break;
                case 5:
                    drawString(s, g2, new Color(128, 0, 0)); // Maroon
                    break;
                case 6:
                    drawString(s, g2, Color.CYAN);
                    break;
                case 7:
                    drawString(s, g2, Color.BLACK);
                    break;
                case 8:
                    drawString(s, g2, Color.DARK_GRAY);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid number of mines in the proximity");
            }
        }
    }

    /**
     * Method to render mined tile
     *
     * @param g2, Graphics2D object
     */
    private void renderMined(Graphics2D g2) {
        int xx = getLeftCoord();
        int yy = getTopCoord();

        g2.setColor(Color.RED);
        g2.fillRect(xx, yy, size, size);
        g2.setColor(Color.DARK_GRAY);
        g2.drawRect(xx, yy, size, size);
        drawString("*", g2, Color.BLACK);
    }


    /**
     * Method to render tile according to its state
     *
     * @param g2, Graphics2D object
     */
    public void render(Graphics2D g2) {
        if (!cleared) {
            renderUncleared(g2);
        } else {
            if (!mine) {
                renderCleared(g2);
            } else {
                renderMined(g2);
            }
            
        }
    }
}
