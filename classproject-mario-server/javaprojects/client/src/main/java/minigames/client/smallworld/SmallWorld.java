package minigames.client.smallworld;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JDialog;
import javax.swing.JFrame;

import io.vertx.core.json.JsonObject;
import minigames.client.Animator;
import minigames.client.GameClient;
import minigames.client.MinigameNetworkClient;
import minigames.client.Tickable;
import minigames.commands.CommandPackage;
import minigames.rendering.GameMetadata;
import minigames.rendering.NativeCommands;

import minigames.smallworld.WorldMap;
import minigames.smallworld.Player;
import minigames.smallworld.Tile;
import minigames.smallworld.Item;

/**
 * 
 * Just dumping a bunch of code in this class to get a baseline working for now.
 * 
 * Eventually we want to separate any client state from the rendering process
 * 
 */
public class SmallWorld implements GameClient, Tickable {

    MinigameNetworkClient mnClient;

    /** We hold on to this because we'll need it when sending commands to the server */
    GameMetadata gm;

    /** Your name */    
    String player;

    JButton exit;
    JButton send;
    JPanel commandPanel;
    JLabel label;
    JPanel pointsbar;
    JDialog gameMenu;
    JPanel gamePanel;
    Animator animator;

    Keyboard key;

    /**
     * 
     * Temp game state for testing reactive paradigm
     * 
     */
    Player playerObj;
    WorldMap world;

    static int SCREEN_WIDTH = 1280;
    static int SCREEN_HEIGHT = 800;
    int TILE_SIZE = Tile.SIZE;
    
    // camera offset in pixels
    int xOffset = 0;
    int yOffset = 0;
    
    /**
     * padding from edge of screen in pixels to where we register pan commands
     *      (i.e. offset increments/decrements)
     */
    int xCameraMargin = SCREEN_WIDTH / 5;
    int yCameraMargin = SCREEN_HEIGHT / 5;

    float gravity = 0.45f;
    int downBounceSpeed = 2;

    int highlightX = 0;
    int highlightY = 0;
    
    Tile tileToPlace = Tile.DIRT;
    List<Tile> tilePool = Arrays.asList(
        Tile.DIRT,
        Tile.GRASS,
        Tile.LAVA,
        Tile.SKY,
        Tile.DEFAULT            
    );

    private boolean isGameActive = false;
    private boolean isGodSpeaking;
    
    int pollTick = 0;

    public SmallWorld() {
        gamePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                renderPanel(g);
            }
        };


        gameMenu = new JDialog((JFrame) null, "Game Menu", false); // 'true' makes it modal
        gameMenu.setSize(300, 200);
        gameMenu.setLayout(new GridLayout(2, 1));
        JButton resumeButton = new JButton("Resume Game");
        JButton quitButton = new JButton("Quit to Main Menu");
        resumeButton.addActionListener(e -> gameMenu.dispose());
        quitButton.addActionListener(e -> sendQtm());
        gameMenu.add(resumeButton);
        gameMenu.add(quitButton);
        gameMenu.setLocationRelativeTo(gamePanel);


        pointsbar = new JPanel();
        label = new JLabel("Items: 0");
        label.setOpaque(true);
        label.setForeground(Color.WHITE);
        label.setBackground(Color.BLACK);
        Dimension labelSize = label.getPreferredSize();
        label.setSize(labelSize);
        label.setLocation((int)(labelSize.getWidth() / 2), (int)(labelSize.getHeight() / 2));
        pointsbar.add(label, JLayeredPane.PALETTE_LAYER);

        gamePanel.add(pointsbar);

        gamePanel.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));

        key = new Keyboard();
        gamePanel.addKeyListener(key);
    
        //  update tile on mouse click
        gamePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int mouseX = e.getX();
                int mouseY = e.getY();
                sendTileUpdate(mouseX + xOffset, mouseY + yOffset, tileToPlace);
            }

        });

        // update tile outline on mouse movement
        gamePanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int mouseX = e.getX();
                int mouseY = e.getY();
                updateHighlightedTile(mouseX, mouseY);
            }
        });

        exit = new JButton("quit to menu");
        exit.addActionListener((evt) -> {
            sendQtm();
        });

        commandPanel = new JPanel();
        for (Component c : new Component[] { exit }) {
            commandPanel.add(c);
        }

        // initialise game state
        playerObj = new Player(SCREEN_WIDTH/2, SCREEN_HEIGHT * 1/4);
    }

    private void showGameMenu() {
        gameMenu.setModalityType(JDialog.ModalityType.MODELESS);
        gameMenu.setVisible(true);
    }
    

    /**
     * Render method we pass into the JPanel on construction.
     * 
     * This is run every time we call repaint().
     * 
     */
    private void renderPanel(Graphics g) {
        this.label.setText("Items: "+String.valueOf(playerObj.getItemCount()));
        g.setColor(Color.BLACK);
        g.fillRect(0,0,SCREEN_WIDTH,SCREEN_HEIGHT);

        if(world == null) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            g.drawString("LOADING WORLD...", 400, 360);
        } else {
            world.render(g, xOffset, yOffset, SCREEN_WIDTH, SCREEN_HEIGHT);
            playerObj.render(g, xOffset, yOffset);
        }
    
        g.setFont(new Font("Arial", Font.BOLD, 32));
        g.setColor(Color.BLACK);
        g.drawString("block type: ", 850, 50);
        g.drawString(tileToPlace.getTypeName(), 1100, 50);

        g.setColor(Color.BLACK);
        g.drawRect(highlightX, highlightY, TILE_SIZE, TILE_SIZE);
        
        playerObj.render(g, xOffset, yOffset);
    }

    /**
     * Sets the position of the tile to be highlighted (currently a thin black border)
     * 
     * Used as a visual aid for the player to identify which tile their mouse clicks will register in
     * 
     * @param x
     * @param y
     */
    private void updateHighlightedTile(int x, int y) {
        // proportion of a tile in pixels
        // (xShift, yShift) also represents the coordinates of the top-left-most fully visible tile relative to the camera
        // we use this to track how the highlight should be offset
        int xShift = xOffset % TILE_SIZE;
        int yShift = yOffset % TILE_SIZE;

        // using integer division to get num. multiples of tiles along screen dimensions
        highlightX = (x + xShift)/TILE_SIZE*TILE_SIZE - xShift;
        highlightY = (y + yShift)/TILE_SIZE*TILE_SIZE - yShift;

    }

    /**
     * 
     * Perform the game loop.
     * 
     * This method is called by Vertx approx 60 times per second
     * 
     */
    @Override
    public void tick(Animator al, long now, long delta) {
        if (!isGameActive) return;

        pollWorldServer();

        key.update();

        // god commands
        // bypass the laws of physics
        // use at your own risk
        if (key.up || key.down || key.left || key.right) {
            isGodSpeaking = false;
        } else {
            processGodCommand();
        }

        if (key.previous || key.next) {
            selectTileToPlace();

            //  reset key status since we want this to happen once per press, not every frame
            //  see the Keyboard class for more info
            key.previous = key.next = false;
        }

        if(playerObj != null && world != null) {
            
            if(!isGodSpeaking)
                updatePlayerPos();

            // Special item handling
            if(isPlayerOnASpecialItem() == true){
                playerObj.collect();
                JsonObject json = new JsonObject().put("command", "collectItem");
                json.put("xPos", playerObj.getX());
                json.put("yPos", playerObj.getY());
                sendCommand(json);
                sendCommand("getWorld");
            }
        }

        if (key.showGameMenu) {
            showGameMenu();
            key.showGameMenu = false;
        }

        updateCameraOffsets();

        if (gamePanel.isVisible()) {
            gamePanel.repaint();
        }
        
        al.requestTick(this);
        
    }

    /**
     * Get the current state of the world map from the game server
     * 
     * Since CommandPackages only elicit a response from the client who sent the command, we need a way to receive updates when another player has made a change.
     * 
     * Currently this just polls the server every 1 second and updates the client map from the RenderingPackage
     * 
     */
    private void pollWorldServer() {
        pollTick++;
        if(pollTick >= 60) {
            pollTick = 0;
            sendCommand("getWorld");
        }
    }

    /**
     * tilePool is a list of all tiles the player can place.
     * tileToPlace is the currently selected tile that will be applied to the map where the player clicks.
     * 
     * When the player presses Q or E (previous/next) we rotate through the list to change what tile is focused. 
     * 
     */
    private void selectTileToPlace() {
        int direction = 0;

        if(key.previous) {
            direction = 1;
        }
        if(key.next) {
            direction = -1;
        }
        
        Collections.rotate(tilePool, direction);

        tileToPlace = tilePool.get(0);
    }

    /**
     * Move while ignoring (most of) the programmed player restrictions
     * 
     * Currently bound to WASD
     * 
     */
    private void processGodCommand() {
        if(key.sudoUp) { playerObj.moveUp(); playerObj.setVelocity(-playerObj.getSpeed()); isGodSpeaking = true;}
        if(key.sudoDown) { playerObj.moveDown(); playerObj.setVelocity(playerObj.getSpeed()); isGodSpeaking = true;}
        if(key.sudoLeft) { playerObj.moveLeft(); isGodSpeaking = true;}
        if(key.sudoRight) { playerObj.moveRight(); isGodSpeaking = true;}
    }

    /**
     * Handles all the logic for player movement, including gravity and falling over gaps
     * 
     * Runs every tick
     * 
     */
    private void updatePlayerPos() {
           
        int xSpeed = 0;
        if(key.left)
            xSpeed -= playerObj.getSpeed();
        if(key.right)
            xSpeed += playerObj.getSpeed();
        
        if(key.up)
            playerObj.jump();

        
        if(playerObj.isAirborne()) {
            if(canPlayerMoveHere(playerObj.getX(), playerObj.getY() + playerObj.getVelocity(), playerObj.getWidth(), playerObj.getHeight())) {
                playerObj.moveVertical(playerObj.getVelocity());
                playerObj.increaseVelocity(gravity);
            } else{
                // the player has hit either the roof or the floor
                playerObj.setY(getYPosNextToSolid(playerObj.getY(), playerObj.getHeight(), playerObj.getVelocity()));
                if(playerObj.getVelocity() > 0) {
                    playerObj.resetAirborne();
                }
                else {
                    // hit the roof. reset vertical speed to a fixed magnitude
                    playerObj.setVelocity(downBounceSpeed);
                }
            }
        } else {
            /**
             *  if not "airborne", check whether player is still on ground
             *  this is so we can set them to fall when walking over gaps but not jumping
             */
            if(!playerIsOnGround(playerObj.getX(), playerObj.getY(), playerObj.getWidth(), playerObj.getHeight())) {
                playerObj.setAirborne(true);
            }
        }

        movePlayerHorizontal(xSpeed);
    }

    /**
     * Checks whether there is a collidable tile under at least one of the bottom-left and bottom-right corners of the player model
     * 
     * @param x
     * @param y
     * @param width
     * @param height
     * @return
     */
    private boolean playerIsOnGround(int x, int y, int width, int height) {
            return
                isTileAtPosCollidable(x, y + height + 1) ||
                isTileAtPosCollidable(x+width, y + height + 1);
    }

    /**
     * During collision detection we check if the player would move into a solid object next frame.
     * 
     * In that case we don't move the player by their current speed but instead shift them as close as possible.
     * 
     * Currently as the player's hitbox is the same as a square tile that just means we return the position of
     *  the adjacent tile in the opposite direction to where the player is travelling.
     * 
     * @param x player's current x co-ordinate
     * @param width
     * @param speed
     * @return the horizontal position x of the pixel that would place the player right next to a collidable they are running into
     */
    private int getXPosNextToSolid(int x, int width, int speed) {
        int currentTileX = x / TILE_SIZE * TILE_SIZE;

        // player is already next to solid
        if(x == currentTileX)
            return x;

        // player is some small distance away from solid (less than speed value)
        if (speed > 0) {
            // right
            return currentTileX + width;
        } else {
            // left
            return currentTileX;
        }
    }

    /**
     * During collision detection we check if the player would move into a solid object next frame.
     * 
     * In that case we don't move the player by their current speed but instead shift them as close as possible.
     * 
     * Currently as the player's hitbox is the same as a square tile that just means we return the position of
     *  the adjacent tile in the opposite direction to where the player is travelling.
     * 
     * @param y player's current Y co-ordinate
     * @param width
     * @param speed
     * @return the vertical position y of the pixel that would place the player right next to a collidable they are running into
     */
    private int getYPosNextToSolid(int y, int height, int speed) {
        int currentTileY = y / TILE_SIZE * TILE_SIZE;
    
        // player is already next to solid
        if(y == currentTileY)
            return y;
        
        // play is some small distance away from solid (less than speed value)
        if (speed > 0) {
            // falling
            return currentTileY + height ;
        } else {
            // jumping
            return currentTileY;
        }
    }
    
    /**
     * Move the player in their direction of travel.
     * 
     * If this would put them inside a collidable tile, instead place them next to it
     * 
     * @param speed
     */
    private void movePlayerHorizontal(int speed) {
        if(canPlayerMoveHere(playerObj.getX() + speed, playerObj.getY(), playerObj.getWidth(), playerObj.getHeight())) {
            playerObj.moveHorizontal(speed);
        } else {
            /**
             * player is within <speed> units of a collidable.
             * Then we close the gap
             */
            playerObj.setX(getXPosNextToSolid(playerObj.getX(), playerObj.getWidth(), speed));
        }
    }

    /**
     * Check whether the player can move to the given position (x,y) without overlapping a solid object.
     * 
     * @param x
     * @param y
     * @param width
     * @param height
     * @return true if none of the corners of the rectangle defined by the parameters are in a collidable tile, false otherwise
     */
    private boolean canPlayerMoveHere(int x, int y, int width, int height) {
        return
            !(isTileAtPosCollidable(x, y) ||
             isTileAtPosCollidable(x, y+height-1) ||
             isTileAtPosCollidable(x+width-1, y) ||
             isTileAtPosCollidable(x+width-1, y+height-1));
    }

    /**
     * 
     * @param x
     * @param y
     * @return true if out of bounds, otherwise returns isCollidable for the tile at (x,y)
     */
    private boolean isTileAtPosCollidable(int x, int y) {
        
        // out of bounds
        if(x < 0 || y < 0 || x >= world.getWidth()*TILE_SIZE || y >= world.getHeight()*TILE_SIZE) {
            return true;
        }
        
        Tile tile = world.getTileAt(x, y);
        return tile.isCollidable();
    }

    /**
     * 
     * @param x
     * @param y
     * @return the specialItemID for the given tile, or -1 if it is out of bounds
     */
    private int getSpecialItemID(int x, int y) {
     
        Item item = world.getItemAt(x, y);
        return item.getSpecialItemID();
    }

    private boolean isPlayerOnASpecialItem(){
        return (getSpecialItemID(playerObj.getX(),playerObj.getY()) > -1);
    }


    /**
     * Controls how the "camera" moves around the world.
     * 
     * In reality we're just rendering the world offset by some value along each axis.
     * 
     * These offsets update when the player passes some threshold near the edge of the screen,
     *  defined by xCameraMargin and yCameraMargin.
     * 
     */
    private void updateCameraOffsets() {

        // player hit left screen margin
        if(playerObj.getX() < xOffset + xCameraMargin) {
            // keep camera within bounds
            if(xOffset - playerObj.getSpeed() > 0)
                xOffset -= playerObj.getSpeed();
            else
                xOffset = 0;
        }

        // player hit right screen margin
        if(playerObj.getX() > SCREEN_WIDTH + xOffset - xCameraMargin) {
            // keep camera within bounds
            if(xOffset + playerObj.getSpeed() < world.getWidth()*TILE_SIZE - SCREEN_WIDTH)
                xOffset += playerObj.getSpeed();
            else
                xOffset = world.getWidth()*TILE_SIZE - SCREEN_WIDTH;
        }

        // player hit upper screen margin
        if(playerObj.getY() < yOffset + yCameraMargin) {
            // keep camera within bounds
            if(yOffset + playerObj.getVelocity() > 0) {
                // make sure player is traveling up
                if (playerObj.getVelocity() < 0)    
                    yOffset += playerObj.getVelocity();
            } else {
                yOffset = 0;
            }
        }

        // player hit lower screen margin
        if(playerObj.getY() > SCREEN_HEIGHT + yOffset - yCameraMargin) {
            // keep camera within bounds
            if(yOffset + playerObj.getVelocity() < world.getHeight()*TILE_SIZE - SCREEN_HEIGHT && playerObj.getVelocity() > 0)
                yOffset += playerObj.getVelocity();
            else
                yOffset = world.getHeight()*TILE_SIZE - SCREEN_HEIGHT;
        }

    }

    /**
     * Sends a QuitToMenu command to the server
     * 
     * This is needed because QTMs are a special type of command with key "NativeCommand"
     */
    public void sendQtm() {
        JsonObject json = (new NativeCommands.QuitToMenu()).toJson();
        mnClient.send(new CommandPackage(gm.gameServer(), gm.name(), player, Collections.singletonList(json)));
    }
 
    /**
     * What we do when our client is loaded into the main screen
     */
    @Override
    public void load(MinigameNetworkClient mnClient, GameMetadata game, String player) {
        this.mnClient = mnClient;
        this.gm = game;
        this.player = player;
        this.animator = mnClient.getAnimator();
        this.isGameActive = true;
        animator.requestTick(this);

        // Add our components to the north, south, east, west, or centre of the main window's BorderLayout
        mnClient.getMainWindow().setCenterSize(gamePanel.getPreferredSize());
        mnClient.getMainWindow().addCenter(gamePanel);
        mnClient.getMainWindow().addSouth(commandPanel);

        // Don't forget to call pack - it triggers the window to resize and repaint itself
        mnClient.getMainWindow().pack();
        mnClient.getMainWindow().centreWindow();

        gamePanel.requestFocusInWindow();

        sendCommand("getWorld");
    }

    /** 
     * Sends a command to the game at the server.
     * 
     * The server should update some game state in response.
     * 
     * We're sending these as 
     * { "command": command }
     */
    public void sendCommand(String command) {
        JsonObject json = new JsonObject().put("command", command);
        
        // Collections.singletonList() is a quick way of getting a "list of one item"
        mnClient.send(new CommandPackage(gm.gameServer(), gm.name(), player, Collections.singletonList(json)));
    }

    
    /** 
     * Sends a command to the game at the server.
     * 
     * This definition takes JSON Objects to allow more complex commands
     * 
     * Pass it a JSON Object with arbitrarily many keys, as long as one is "command"
     * 
     * @param jason JSON object containing at least an entry with the key "command". If sending just one command use sendCommand(String) instead
     */
    public void sendCommand(JsonObject jason) {
        mnClient.send(new CommandPackage(gm.gameServer(), gm.name(), player, Collections.singletonList(jason)));
    }

    /**
     * Sends a request to the server to change a tile in the world map.
     * 
     * Selects the tile that contains the point (x,y).
     * 
     * Server currently responds with a "getWorld" (i.e. the entire map)
     * 
     * @param x xPos of tile to update
     * @param y yPos of tile to update
     * @param t Tile to update to
     */
    private void sendTileUpdate(int x, int y, Tile t) {
        JsonObject json = new JsonObject().put("command", "updateTile");
        json.put("xPos", x);
        json.put("yPos", y);
        json.put("tileAscii", t.getAscii());

        sendCommand(json);
    }

    /**
     * 
     * This is where we handle render commands from the server
     * 
     * Any command passed in will be custom crafted in the SmallWorldGame class on the server
     *  and should have an appropriate response action here
     * 
     */
    @Override
    public void execute(GameMetadata game, JsonObject command) {
        this.gm = game;

        // We should only be receiving messages that our game understands
        switch (command.getString("command")) {
            case "getWorld" -> {
                world = new WorldMap(command.getJsonArray("worldMapTiles"),command.getJsonArray("worldMapItems"));
            }
        }
    }

    /**
     * 
     * This just runs as a side effect of receiving a QuitToMenu command from the server.
     * 
     */
    @Override
    public void closeGame() {

        System.out.println("Closing the game and reseting the animator");
        isGameActive = false;
        System.out.println(
        """
            
            goodbye world.
                
        """
        );
    }
    
}
