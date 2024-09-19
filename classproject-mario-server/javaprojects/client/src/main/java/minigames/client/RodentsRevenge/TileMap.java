package minigames.client.RodentsRevenge;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class TileMap {
    GamePanel gamePanel;
    Tile[] tile;
    int mapTileNum[][];
    BufferedImage[] mouseImages;



    public TileMap(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        tile = new Tile[7];
        mapTileNum = new int[gamePanel.screenRow][gamePanel.screenCol];
        getTitleImage();
        loadMouseImage();  // Load all mouse image
        loadMap("/RodentsRevenge/Map/map.txt");
    }

    public void setTilemap(int[][] tilemap){
        mapTileNum = tilemap;
    }

    /**
     * The getTitleImage reads through the image files stored in the resources folder and stores in against
     * a tile object. The numbering system for this tile object is used to create the map file.
     */
    public void getTitleImage(){
        try {
            tile[0] = new Tile();
            tile[0].image = ImageIO.read(getClass().getResourceAsStream("/RodentsRevenge/Map/ground.png"));

            tile[1] = new Tile();
            tile[1].image = ImageIO.read(getClass().getResourceAsStream("/RodentsRevenge/Map/wall.png"));

            tile[2] = new Tile();
            tile[2].image = ImageIO.read(getClass().getResourceAsStream("/RodentsRevenge/Map/block.png"));

            tile[3] = new Tile();
            tile[3].image = ImageIO.read(getClass().getResourceAsStream("/RodentsRevenge/Map/mouse.png"));

            tile[4] = new Tile();
            tile[4].image = ImageIO.read(getClass().getResourceAsStream("/RodentsRevenge/Map/cat_moving.png"));

            tile[5] = new Tile();
            tile[5].image = ImageIO.read(getClass().getResourceAsStream("/RodentsRevenge/Map/mouse_dead.png"));

            tile[6] = new Tile();
            tile[6].image = ImageIO.read(getClass().getResourceAsStream("/RodentsRevenge/Map/cheese.png"));


        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * load all the mouse image with different colors
     */
    public void loadMouseImage() {
        try {
            mouseImages = new BufferedImage[5];
            mouseImages[0] = ImageIO.read(getClass().getResourceAsStream("/RodentsRevenge/MouseColors/mouse_red2.png"));
            mouseImages[1] = ImageIO.read(getClass().getResourceAsStream("/RodentsRevenge/MouseColors/mouse_blue2.png"));
            mouseImages[2] = ImageIO.read(getClass().getResourceAsStream("/RodentsRevenge/MouseColors/mouse_green.png"));
            mouseImages[3] = ImageIO.read(getClass().getResourceAsStream("/RodentsRevenge/Map/mouse.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The loadMap method takes in a txt file that represents the tile arrangement of the map.
     * It parses through the txt file and maps each value against its corresponding position
     * in a variable called mapTileNum.
     * The mapTileNum variable is in the form of a 2D array: mapTileNum[row][col]
     * @param filepath - the filepath of the Map file (txt)
     */
    public void loadMap(String filepath) {

        try {
            // load map file
            InputStream is = getClass().getResourceAsStream(filepath);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            // initialise current column and row as 0
            int col = 0;
            int row = 0;

            while(col < gamePanel.screenCol && row < gamePanel.screenRow){

                String line = br.readLine();

                while(col < gamePanel.screenCol) {
                    String numbers[] = line.split(" "); // Split the string at every 'space'
                    int num = Integer.parseInt(numbers[col]); // Parse string to int
                    mapTileNum[row][col] = num; // store value of tile with its corresponding position at [row][col]
                    col ++; // increment column
                }

                // if on last column, reset to first column and increment row
                if(col == gamePanel.screenCol) {
                    col = 0;
                    row++;
                }
            }
            br.close(); // Close bufferReader
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * The draw method renders the tile layout of the game
     */
    public void draw(Graphics g) {

        int col = 0;
        int row = 0;
        int x = 0;
        int y = 0;

        while (col < gamePanel.screenCol && row < gamePanel.screenRow) {

            int tileNum = mapTileNum[row][col];//Extract tile number which is stored in mapTileNum[0][0]

            g.drawImage(tile[0].image, x, y, gamePanel.tileSize, gamePanel.tileSize, null); // Draw ground first
            g.drawImage(tile[tileNum].image, x, y, gamePanel.tileSize, gamePanel.tileSize, null);
            if (tileNum==3) { // Draw playername on top of the player and display player color with chosen color
                BufferedImage mouseImage = null;
                RodentPlayer rPlayer = RodentsRevenge.rrPlayerMap.get(col + "|" + row); // Get RodentPlayer by player's position
                String playerName = rPlayer != null ? rPlayer.getName() : ""; // Get player's name
                String playerColor = rPlayer != null && rPlayer.getColor() != null ? rPlayer.getColor() : "";
                // Get player's color and display the player image based on the color
                switch (playerColor) {
                    case "red":
                        mouseImage = mouseImages[0];
                        break;
                    case "blue":
                        mouseImage = mouseImages[1];
                        break;
                    case "green":
                        mouseImage = mouseImages[2];
                        break;
                    case "gray":
                        mouseImage = mouseImages[3];
                        break;
                    default:
                        mouseImage = tile[0].image;
                        break;
                }

                g.drawImage(mouseImage, x, y, gamePanel.tileSize, gamePanel.tileSize, null);
                // Display player name on top of the mouse
                if (playerName != null) {
                    Font font = new Font("Courier", Font.BOLD, 15);
                    g.setFont(font);
                    g.setColor(Color.WHITE);
                    g.drawString(playerName, x, y);
                }
            }
            col++;
            x += gamePanel.tileSize;

            if (col == gamePanel.screenCol) {
                col = 0;
                x = 0;
                row++;
                y += gamePanel.tileSize;
            }
        }
    }
}
