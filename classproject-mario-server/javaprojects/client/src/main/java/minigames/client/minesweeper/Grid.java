/**
 * Code written by Jake Mayled(Student number:220265608), last updated: August 2024
 */
package minigames.client.minesweeper;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Random;

/**
 * Grid class handles all grid-related logic
 * - Constructor takes in a string 'difficulty' (easy, medium, hard), if none of them are passed in an IllegalArgumentException is thrown
 * - Constructor then calls initializeGrid method that assigns, rows, cols, and numOfMines, it also fills the grid array with '-' which represents unopened tiles
 * - Constructor than calls placeMines method, which randomly places mines in the grid
 * - clearTiles and clearTile methods clears any tiles that are not adjacent a mine. 
 * - numOfMines method counts how many mines touch a tile in any given direction
 * - displayGrid method outputs the current grid to the console, for debugging purposes
 * - getter methods, getGrid, getMines, getRows, getCols, gets the respective data.
 *
 */
class Grid extends JPanel {

    // Variables that for simplicity will be shared with Tile.
    public static int pxSize = 18;
    public int xOffset = 0;
    public int yOffset = 0;
    public int margin = 10;
    public Font font;
    public int fontWidth = 0, fontHeight = 0, fontAscent = 0;
    public BufferedImage mineFlag;

    private static String dir = "/images/minesweeper/";
    private static String mineFlagFile = dir + "minesweeper_flag.png";

    private Tile[][] tiles;

    private int rows, cols, numMines, score; // Grid dimensions, number of mines and score
	private String difficulty;

    private GridAndUserInterfaceListener gridAndUserInterfaceListener; //Mines remaining listener
    private GameController gameController;
    
    /**
     * Constructor to initialize the grid based on difficulty
     * @param difficulty, (easy, medium, hard) accepted parameters
     */
    public Grid(String difficulty, GameController gameController, int[][] tilesdata) {
        super();
        
        //Adjust pxSize depending on the difficulty 
        if(difficulty.equalsIgnoreCase("easy")){
            pxSize = 32;
        }else if(difficulty.equalsIgnoreCase("medium")){
            pxSize = 30;
        }else if(difficulty.equalsIgnoreCase("hard")){
            pxSize = 28;
        }else{
            pxSize = 18;
        }

        this.gameController = gameController;
        score = 0;
        initializeGrid(difficulty, tilesdata);
        if(!(difficulty.equalsIgnoreCase("easy") || difficulty.equalsIgnoreCase("medium") || difficulty.equalsIgnoreCase("hard"))){
            setDifficulty("custom");
        }else{
            setDifficulty(difficulty);
        }

        if (tilesdata == null) {
            // Generate mine fild only for new game.
            placeMines();
        }

        // Set minimal sizes + margins to the grid pixel size
        setPreferredSize(new Dimension(cols * pxSize + margin * 2, rows * pxSize + margin * 2));
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        try {
            mineFlag = ImageIO.read(getClass().getResource(mineFlagFile));
        } catch (IOException e) {
            System.out.println("Error loading flag image. \nError message: " + e.getMessage());
        }        
        makeClickable();
    }

    /**
     * Method to make Grid react on mouse events
     */
    private void makeClickable() {
        this.addMouseListener( new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            	Sound.getInstance().playSFX(Sound.Type.CLICK);
                int idxCol = (e.getX() - xOffset) / pxSize;
                int idxRow = (e.getY() - yOffset) / pxSize;

                if (idxCol < 0 || idxCol >= cols || idxRow < 0 || idxRow >= rows) 
                    return;

                Tile tile = tiles[idxRow][idxCol];

                switch (e.getButton()) {
                    case MouseEvent.BUTTON1:
                        // Do nothing if left button pressed on FLAGGED or QUESTION tile
                        if (tile.getState() != State.NONE)
                            break;

                        if (tile.isMine()) {
                            // Game over!
                            displayGrid(true);
                            repaint();
                            gameController.playerLoses();
                        } else {
                            clearTiles(idxRow, idxCol);
                            updateScore();
                        }
                        break;
                    case MouseEvent.BUTTON3:
                        if (!tile.isCleared()) {
                            tile.setNextState();
                            if(tile.getState().equals(State.FLAGGED)){
                                gridAndUserInterfaceListener.onMinesRemainingChanged(1);
                            }else if (tile.getState().equals(State.QUESTION)) {
                                gridAndUserInterfaceListener.onMinesRemainingChanged(-1);
                            }
                        }
                        break;
                }
                repaint();
            }
        });
    }

    /**
     * Method to set grid size and mine count based on the chosen difficulty
     * @param difficulty (easy, medium, hard) string that represents the difficulty of the game
     * @param data array of data to initialize tiles if needed
     */
    private void initializeGrid(String difficulty, int[][] data) {
        this.score=0;
        switch (difficulty.toLowerCase()) {
            case "easy":
                rows = 5;
                cols = 5;
                numMines = 5;
                break;
            case "medium":
                rows = 8;
                cols = 8;
                numMines = 15;
                break;
            case "hard":
                rows = 18;
                cols = 18;
                numMines = 40;
                break;
            default:
                String[] customDifficulty = difficulty.split("\\.");
                rows = Integer.parseInt(customDifficulty[0]);
                cols = Integer.parseInt(customDifficulty[1]);
                numMines = Integer.parseInt(customDifficulty[2]);
                break;
        }

        if (data != null) {
            // Set the number of rows and columns depending on data.
            rows = data.length;
            cols = data[0].length;
            // Will be recalculated using data
            numMines = 0;
        }

        // Initialize the tiles array
        tiles = new Tile[rows][cols];

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                tiles[y][x] = new Tile(x, y, this);

                // We assume that data array has the same sizes as tiles
                if (data != null) {
                    tiles[y][x].fromInt(data[y][x]);
                    if (tiles[y][x].isMine()) {
                        numMines++;
                    }

                    // This is not too good because number of mines is also present in UI,
                    // but for the loading time it works.
                    if (tiles[y][x].getState() != State.NONE) {
                        numMines--;
                    }

                }
            }
        }
    }

    /**
     * Method to get the tiles data as array
     * 
     * @returns tile data as int[][] array
     */
    public int[][] getTilesData() {
        int [][] data = new int[rows][cols];
        
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                data[y][x] = tiles[y][x].toInt();
            }
        }
        return data;
    }

    /**
     * Method to paint the whole contents of the grid
     *
     * @param g, Graphics object
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D)g;

        Dimension d = getSize();
        xOffset = (d.width - cols * pxSize) / 2;
        yOffset = (d.height - rows * pxSize) / 2;

        if (font == null) {
            // Font and its params will be shared with Tile for
            // better performance.
            font = new Font("Monospaced", Font.BOLD, 13);
            FontMetrics metrics = g2.getFontMetrics(font);
            fontWidth = metrics.stringWidth("1");
            fontHeight = metrics.getHeight();
            fontAscent = metrics.getAscent();
        }

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                tiles[y][x].render(g2);
            }
        }
    }


    /**
     * Method to randomly place mines on the grid
     */
    private void placeMines() {
        Random rand = new Random();
        int placedMines = 0;

        // Keep placing mines until the required number of mines is reached
        while (placedMines < numMines) {
            int r = rand.nextInt(rows);
            int c = rand.nextInt(cols);

            if (!tiles[r][c].isMine()) {
                // Place a mine only if there isn't already one there
                tiles[r][c].setMine();
                placedMines++;
            }
        }
    }

    /**
     * Method to clear tiles starting from a specific tile (row, col)
     * If a tile is next to a mine, the tile in the grid is updated to show how many mines are touching that tile
     * @param row, row number
     * @param col, column number
     */
    public void clearTiles(int row, int col) {
        // Check if the starting tile is within bounds and hasn't been revealed yet
        if (row < 0 || row >= rows || col < 0 || col >= cols || tiles[row][col].isCleared()) {
            return;
        }

        // Calculate the number of adjacent mines
        int minesAround = numOfMines(row, col);

        // If there are no adjacent mines, start clearing recursively
        if (minesAround == 0) {
            clearTile(row, col);
        } else {
            // If there are adjacent mines, just reveal the count on this tile
            tiles[row][col].clear(minesAround);
        }
    }

    /**
     * clearTiles helper method to recursively clear tiles
     * @param row, row number
     * @param col, column number
     */
    private void clearTile(int row, int col) {
        // Base case: stop if out of bounds or tile already cleared
        if (row < 0 || row >= rows || col < 0 || col >= cols || tiles[row][col].isCleared()) {
            return;
        }

        tiles[row][col].clear(0);

        // Check all neighboring tiles
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i != 0 || j != 0) {
                    int newRow = row + i;
                    int newCol = col + j;
                    // Only clear the neighboring tile if it's within bounds
                    if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols) {
                        // If there are no adjacent mines, recursively clear the tile
                        if (numOfMines(newRow, newCol) == 0 && !tiles[newRow][newCol].isCleared()) {
                            clearTile(newRow, newCol);
                        } else {
                            // If there are adjacent mines, reveal the count on this tile
                            tiles[newRow][newCol].clear(numOfMines(newRow, newCol));
                        }
                    }
                }
            }
        }
    }


    /**
     * Method to count the number of mines adjacent to a given tile
     * @param row, num of row
     * @param col, num of column
     * @return number of mines adjacent to a tile
     */
    public int numOfMines(int row, int col) {
        int mineCount = 0;

        // Check all adjacent tiles for mines
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int r = row + i;
                int c = col + j;

                if (r >= 0 && r < rows && c >= 0 && c < cols && tiles[r][c].isMine()) {
                    mineCount++;
                }
            }
        }

        return mineCount; // Return the count of adjacent mines
    }

    /**
     * Updates the the score variable 
     */
    public void updateScore(){
        //Calculate how many tiles have been cleared in the gird
        int clearedTiles = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++){
                if (tiles[r][c].isCleared()){
                    clearedTiles++;
                }
            }
        }
        //Update the score but only add the cleared tiles that haven't been counted towards the score already
        score += clearedTiles - score;

        if(gridAndUserInterfaceListener != null){
            gridAndUserInterfaceListener.onScoreChange(score);
        }
    }
    /**
     * Method to display the current grid state, for debugging purposes
     * @param revealMines boolean, if true shows mines in the grid, if false does not show mines
     */
    public void displayGrid(boolean revealMines) {
        System.out.println("Current Grid:");
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int mineCount = numOfMines(i, j);
                tiles[i][j].clear(mineCount);

                if (revealMines && tiles[i][j].isMine()) { // Show mines if game over
                    System.out.print("* ");
                } else {
                    System.out.print(mineCount + " ");
                }
            }
            System.out.println();
        }
    }

    //-----------------GETTER METHODS-----------------//

    //Getter method to return a tile 
    public Tile getTile(int row, int col) {
        return tiles[row][col];
    }


    //Getter method to get the number of mines
    public int getNumMines(){
        return numMines;
    }
    
    // Getter method to access difficulty
    public String getDifficulty() {
        return difficulty;
    }

    // Getter method to access the number of rows in the grid
    public int getRows() {
        return rows;
    }

    // Getter method to access the number of columns in the grid
    public int getCols() {
        return cols;
    }

    //Getter method to get current score
    public int getScore(){
        return score;
    }

    //-----------------SETTER METHODS-----------------//

    //Setter to set the difficulty
    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    // Method to set the listener
    public void gridAndUserInterfaceListener(GridAndUserInterfaceListener listener) {
        this.gridAndUserInterfaceListener = listener;
    }

    public void setRows(int v) {
        this.rows = v;
    }

    public void setCols(int v) {
        this.cols = v;
    }

    public void setScore(int v) {
        this.score = v;
    }

    /**
     * ------Testing purposes only------
     * Method to clear all mines from the grid
     */
    public void clearMines() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (tiles[r][c].isMine()) {
                    tiles[r][c].removeMine();
                }
            }
        }
    }

/**
 * Main class only for debugging purposes.
 * It can run as a standalone GUI
 *
 * @param args
 
 
public static void main(String[] args){
    JFrame frame = new JFrame("Standalone GUI");
    Grid grid = new Grid("hard");
    grid.clearTiles(5,5);
    grid.displayGrid(true);

    frame.add(grid);
    frame.setSize(800, 600);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);

}
*/
}