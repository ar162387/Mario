package minigames.smallworld;

import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;


/* 
 *  Contains all tiles in the map.
 * 
 *  Can think of it as a pixel grid scaled up by a factor of TILE_SIZE (probably 32)
 * 
 */
public class WorldMap {

    public char tileChars[][];
    public Tile tiles[][];
    public char itemChars[][];
    public Item items[][];

    int TILE_SIZE = Tile.SIZE;

    public WorldMap(int width, int height) {
        tileChars = new char[height][width];
        tiles = new Tile[height][width];
        itemChars = new char[height][width];
        items = new Item[height][width];
    }

    public WorldMap(char[][] heresOneIPreparedEarlier) {
        int height = heresOneIPreparedEarlier.length;
        int width = heresOneIPreparedEarlier[0].length;
        
        this.tileChars = heresOneIPreparedEarlier;
        this.tiles = new Tile[height][width];
        this.itemChars = new char[height][width];
        this.items = new Item[tileChars.length][tileChars[0].length];
        parseSprites();
        parseItems();
    }

    public WorldMap(JsonArray jTiles, JsonArray jItems) {
        this(jTiles.getString(0).length(),jTiles.size());
        this.tileChars = fromJson(jTiles);
        this.itemChars = fromJson(jItems);
        this.tiles = new Tile[tileChars.length][tileChars[0].length];
        this.items = new Item[itemChars.length][itemChars[0].length];
        parseSprites();
        parseItems();
    }

    public int getWidth() {
        return this.tiles[0].length;
    }

    public int getHeight() {
        return this.tiles.length;
    }

    /**
     * 
     * 
     * @param x
     * @param y
     * @return tile at pixel coordinates (x,y)
     */
    public Tile getTileAt(int x, int y) {
        if(x < 0 || y < 0 || x >= getWidth()*TILE_SIZE || y >= getHeight()*TILE_SIZE) {
            // out of bounds
            // returning a default collidable tile
            return Tile.DEFAULT;
        }
        return tiles[y/TILE_SIZE][x/TILE_SIZE];
    }

    /**
     * 
     * 
     * @param x
     * @param y
     * @return item at pixel coordinates (x,y)
     */
    public Item getItemAt(int x, int y) {
        if(x < 0 || y < 0 || x >= getWidth()*TILE_SIZE || y >= getHeight()*TILE_SIZE) {
            // out of bounds
            // returning a basic item
            return Item.DEFAULT;
        }
        return items[y/TILE_SIZE][x/TILE_SIZE];
    }

    /**
     * Reads a grid of ASCII chars from a text file and maps it to a 2D chars array.
     * 
     * Then parses each char into its corresponding tile ( = position and sprite image).
     */
    public void loadFromFile(String filePath) {

        try {

            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            
            List<String> lines = reader.lines().toList();

            int row = 0;
            for (String line : lines) {

                String tokens[] = line.split("");

                for(int i=0; i<tokens.length; i++) {
                    tileChars[row][i] = tokens[i].charAt(0);
                }
                row++;
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public char[][] getTileChars() {
        return this.tileChars;
    }

    public List<String> getTileStrings() {
        String[] mapStrings = new String[tileChars.length];
        for (int i = 0; i < tileChars.length; i++) {
            mapStrings[i] = new String(tileChars[i]);
        }
        return Arrays.asList(mapStrings);
    }

    public List<String> getItemStrings() {
        String[] itemStrings = new String[itemChars.length];
        for (int i = 0; i < itemChars.length; i++) {
            itemStrings[i] = new String(itemChars[i]);
        }
        return Arrays.asList(itemStrings);
    }

    public char[][] getItemChars() {
        return this.itemChars;
    }


    /**
     * The example map file is a grid of ASCII chars, each representing some tile type.
     * 
     * This method takes a grid of these chars and turns it into an array of Tiles containing the corresponding sprite image
     * 
     */
    private void parseSprites() {

        for (int row=0; row<tileChars.length;row++) {
            for(int col=0; col<tileChars[row].length;col++) {
                
                char tileChar = tileChars[row][col];
                this.tiles[row][col] = Tile.fromChar(tileChar);

            }
        }
    }

    private void parseItems() {

        for (int row=0; row<itemChars.length;row++) {
            for(int col=0; col<itemChars[row].length;col++) {
                
                char itemChar = itemChars[row][col];
                this.items[row][col] = Item.fromChar(itemChar);

            }
        }
    }

    /**
     * Defers the map render to each individual tile's render() method
     */
    public void render(Graphics g) {
        for (int row=0; row<tiles.length;row++) {
            for(int col=0; col<tiles[0].length;col++) {
                
                Tile tile = tiles[row][col];

                g.drawImage(tile.sprite, col*Tile.SIZE, row*Tile.SIZE, Tile.SIZE, Tile.SIZE, null);  
            }
        }
        for (int row=0; row<tiles.length;row++) {
            for(int col=0; col<tiles[0].length;col++) {
                Item item = items[row][col];
                if(item.ascii != '0'){
                    g.drawImage(item.sprite, col*Tile.SIZE, row*Tile.SIZE, Tile.SIZE, Tile.SIZE, null);
                }

                
                
                // tiles[row][col].render(g,xOffset,yOffset);
            }
        }
    }

    /**
     * 
     * Render the world when the camera has moved from (0,0).
     * 
     * While the world map can be huge, we only want to render what the player can see within the bounds of the window.
     * 
     * If the offset is not a multiple of TILE_SIZE then we also render another row/column in that direction
     * 
     * 
     * @param g
     * @param xOffset in pixels
     * @param yOffset in pixels
     * @param screenWidth
     * @param screenHeight
     */
    public void render(Graphics g, int xOffset, int yOffset, int screenWidth, int screenHeight) {
        for (int row=yOffset/TILE_SIZE; row<screenHeight/TILE_SIZE + Math.ceil((float) yOffset/TILE_SIZE);row++) {
            for(int col=xOffset/TILE_SIZE; col<screenWidth/TILE_SIZE + Math.ceil((float) xOffset/TILE_SIZE);col++) {
                
                Tile tile = tiles[row][col];
                g.drawImage(tile.sprite, col*Tile.SIZE - xOffset, row*Tile.SIZE - yOffset, Tile.SIZE, Tile.SIZE, null);

            }
        }
        for (int row=yOffset/TILE_SIZE; row<screenHeight/TILE_SIZE + Math.ceil((float) yOffset/TILE_SIZE);row++) {
            for(int col=xOffset/TILE_SIZE; col<screenWidth/TILE_SIZE + Math.ceil((float) xOffset/TILE_SIZE);col++) {
                Item item = items[row][col];
                if(itemChars[row][col] != '0'){
                    g.drawImage(item.getSprite(), col*Tile.SIZE - xOffset, row*Tile.SIZE - yOffset, Tile.SIZE, Tile.SIZE, null);
                }

                
                
                // tiles[row][col].render(g,xOffset,yOffset);
            }
        }
    }

    /**
     * 
     * Turns the world map into JSON for sending over the network
     * 
     * Used by the server
     * 
     * @return WorldMap char array to be unpacked by client
     */
    public JsonObject toJson() {
        JsonArray jsonArray = new JsonArray();
        for (char[] row : tileChars) {
            // concat all the chars for this row
            StringBuffer sb = new StringBuffer();

            for (char value : row) {
                sb.append(value);
            }
            jsonArray.add(sb.toString());
        }
        return new JsonObject().put("worldMap", jsonArray);
    }

    /**
     * 
     * Sends the WorldMap to the server
     * 
     * @return a WorldMap object
     */
    public WorldMap sendTheMap(){
        return this;
    }

    /**
     * 
     * Updates the map's tiles
     * 
     * Used by the server?
     */
    public void setTiles(char arr[][]){
        this.tileChars = arr;
        parseSprites();
    }

    /**
     * 
     * Updates the map's items
     * 
     * Used by the server?
     */
    public void setItems(char arr[][]){
        this.itemChars = arr;
        parseItems();
    }


    /**
     * 
     * Unpacks a JSON array into a worldmap 2D char array.
     * 
     * Used by the client
     * 
     * @param jArray
     * @return
     */
    private char[][] fromJson(JsonArray jArray) {
        char[][] worldMapChars = new char[jArray.size()][];
        for (int row = 0; row < jArray.size(); row++) {
            String rowString = jArray.getString(row);
            worldMapChars[row] = rowString.toCharArray();
        }

        return worldMapChars;
    }

    /* When an item has been picked, make it disappear */
    public void itemSpotUpdate(int x, int y) {
        if(x < 0 || y < 0 || x >= getWidth()*TILE_SIZE || y >= getHeight()*TILE_SIZE) {
            // out of bounds
        }
        else {
            items[y/TILE_SIZE][x/TILE_SIZE] = Item.DEFAULT;
            itemChars[y/TILE_SIZE][x/TILE_SIZE] = '0';
            parseItems();
        }
    }

    public void printToConsole() {
        Arrays.stream(tileChars).forEach(System.out::println);
        Arrays.stream(itemChars).forEach(System.out::println);
    }

    public void placeTileChar(int x, int y, char ascii) {
        if(x < 0 || y < 0 || x >= getWidth()*TILE_SIZE || y >= getHeight()*TILE_SIZE) {
            // out of bounds
        }
        else {
            tileChars[y/TILE_SIZE][x/TILE_SIZE] = ascii;
        }
    }
}
