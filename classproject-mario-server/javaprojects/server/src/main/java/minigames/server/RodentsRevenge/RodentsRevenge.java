package minigames.server.RodentsRevenge;

import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import minigames.server.RodentsRevenge.*;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.json.JsonObject;
import minigames.commands.CommandPackage;
import minigames.rendering.*;
import minigames.rendering.NativeCommands.LoadClient;

/**
 * Represents an actual Muddle game in progress
 */
public class RodentsRevenge {

    /** A logger for logging output */
    private static final Logger logger = LogManager.getLogger(RodentsRevenge.class);
    public static int colSize = 25;
    public static int rowSize = 19;
    private Timer timer;
    private int timeRemaining = 300;

    public int score = 0; // counter to keep track of score

    /** Uniquely identifies this game */
    String name;
    ScheduledExecutorService executorService;
    private final ArrayList<Cat> cats = new ArrayList<Cat> ();
    public int[][] boardState = {
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 1},
            {1, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 1},
            {1, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 1},
            {1, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 1},
            {1, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 1},
            {1, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 1},
            {1, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 1},
            {1, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 1},
            {1, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 1},
            {1, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 1},
            {1, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}

    };

    private void startTimer() {
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (timeRemaining > 0) {
                    timeRemaining--;

                } else {
                    timer.stop();
                    //gameOver();
                }
            }
        });
        timer.start();
    }

    public RodentsRevenge(String name) {
        this.name = name;
        this.executorService = Executors.newSingleThreadScheduledExecutor();
        startTimer();
        spawnCat();
        updateCats();
    }


    /**
     * Iterate through the cats Arraylist to update the cats positions every second
     * if the player's position is next to a cat, then it will die
     * when a player dies, it will display the dead mouse image and it will disappear after 2 seconds
     * a new mouse will reappear in a random location
     */
    private final ArrayList<Cat> cheesePosition = new ArrayList<>();
    public void updateCats(){
        executorService.scheduleAtFixedRate(

                new Runnable() {
                    @Override
                    public void run() {
                        // Loop through cats array
                        for (int i = 0; i < cats.size(); i++) {
                            Cat cat = cats.get(i);
                            cat.update();

                            if (cat.catDead(cat)) {            // If cat is dead, turn it into cheese
                                boardState[cat.y][cat.x] = 6;
                                cheesePosition.add(cat);
                                score += 100;                 // increment score by 100
                                cats.remove(i);
                            } else {                          // else, display the cat's new position
                                cat.update();
                                cat.display();
                            }

                            // if a cat next to a mouse which can be diagnal, then the mouse die
                            for (int m = cat.x-1; m <= cat.x+1; m++) {
                                for (int n = cat.y-1; n <= cat.y+1; n++) {
                                    if (boardState[n][m] == 3) {
                                        final int a = m;
                                        final int b = n;

                                        executorService.schedule(() -> { boardState[b][a] = 0;}, 2, TimeUnit.SECONDS);
                                        boardState[n][m] = 5; // the dead mouse image will disapear after 2 seconds
                                        for (RodentPlayer player : players.values()) {
                                            if (player.x == m & player.y == n) {
                                                player.setDead();

                                                Random randInt = new Random();  // display a new mouse in a random position
                                                   int s, t;
                                                   do {
                                                        s = randInt.nextInt(10,15);
                                                        t = randInt.nextInt(8, 13);
                                                } while (boardState[t][s] != 0 && boardState[t][s] != 2);
                                               boardState[t][s] = 3;
                                               player.setX(s); // set the player's position to the new position
                                               player.setY(t);
                                            }
                                        }
                                    }
                                }
                            }

                            updateScore(); // Update score

                        }
                    }
                }, 0, 1, TimeUnit.SECONDS);

    }

    /**
     * updateScore function updates the player's score upon trapping cat/picking up cheese
     */
    public void updateScore() {
        List<Cat> cheeseToRemove = new ArrayList<>(); //List to keep track of which cheese to remove

        for (RodentPlayer player : players.values()) {
            // Check if cat has picked up cheese
            for(Cat cheese: cheesePosition) {
                // if playerPosition == cheesePosition then cheese is picked up
                if (player.y==cheese.y && player.x==cheese.x) {
                    score += 250; // increment score by 250
                    cheeseToRemove.add(cheese); // Add cheese picked up to removeCheeseArray
                }
            }
            player.setScore(score); // update score
        }
        cheesePosition.removeAll(cheeseToRemove);  // remove all cheese
    }

    /**
     * To spawn one cat every 15 seconds in a random position of the outskirts of the bricks
     */
    public void spawnCat() {
        executorService.scheduleAtFixedRate(
                new Runnable() {
                    @Override
                    public void run() {
                        Random randInt = new Random();
                          int[] listX = {1, 2, 3, 21, 22, 23};
                          int[] listY = {1, 2, 17, 18};

                        int x, y;
                        do {
                            x = listX[randInt.nextInt(listX.length)];
                            y = listY[randInt.nextInt(listY.length)];
                        } while (boardState[y][x] != 0);

                        if (cats.size() < 3) {
                            Cat cat = new Cat(x, y, boardState);
                            cat.display();
                            cats.add(cat); // add the cat to a Arraylist
                        }

                    }
                }, 0, 15, TimeUnit.SECONDS);

    }

    public static HashMap<String, RodentPlayer> players = new HashMap<>();

    /** The players currently playing this game */
    public String[] getPlayerNames() {
        return players.keySet().toArray(String[]::new);
    }

    /** Metadata for this game */
    public GameMetadata gameMetadata() {
        return new GameMetadata("RodentsRevenge", name, getPlayerNames(), true);
    }

    public RenderingPackage runCommands(CommandPackage cp) {
        logger.info("Received command package {}", cp);
        RodentPlayer p = players.get(cp.player());
        ArrayList<JsonObject> renderingCommands = new ArrayList<>();
        ArrayList<Integer> blocks = new ArrayList<Integer>();
        cp.commands().iterator().forEachRemaining(comm ->
            {
                switch (comm.getString("command")) {
                    case "getTimeRemain":
                        renderingCommands.add(new JsonObject().put("timeRemain", timeRemaining));
                    case "getPlayerName":
                        renderingCommands.add(new JsonObject().put("playerName", players.values()));
                    case "getState":
                        renderingCommands.add(new JsonObject().put("boardState", boardState));
                        break;
                    case "red":
                    case "blue":
                    case "green":
                    case "gray":
                        p.setColor(comm.getString("command"));
                        break;
                    case "LEFT":
                        if (p.alive == true) {
                            outerloop:
                            if (boardState[p.y][p.x - 1] == 1) { // if tile on left of the player is a wall
                                break;
                            } else if (boardState[p.y][p.x - 1] == 2) {    // if tile on the left of the player is a box

                                for (int i = p.x - 1; i > 0; i--) {   // loop through all boxes to the left
                                    if (boardState[p.y][i] == 2) {     // if it is a block
                                        blocks.add(i);  // add x cordinate to blocks list
                                    } else {       // if not a block
                                        break;     // exit loop
                                    }
                                }

                                for (int i : blocks) {          // loop through items in blocksX array
                                    if (boardState[p.y][i - 1] == 1 || boardState[p.y][i - 1] == 4 || boardState[p.y][i - 1] == 6) { // if  block is next to wall or cat or cheese, exit outerloop
                                        break outerloop;
                                    } else {
                                        boardState[p.y][i - 1] = 2;  // otherwise move block left
                                    }
                                }
                                boardState[p.y][p.x] = 0;
                                p.x -= 1;
                                boardState[p.y][p.x] = 3;

                            } else {
                                boardState[p.y][p.x] = 0;
                                p.x -= 1;
                                boardState[p.y][p.x] = 3;
                            }
                            blocks.clear();
                            break;
                        } else {
                            break;
                        }

                    case "RIGHT":
                        if (p.alive == true) {
                            outerloop:
                            if (boardState[p.y][p.x + 1] == 1) { // if tile on right of the player is a wall
                                break;
                            } else if (boardState[p.y][p.x + 1] == 2) {    // if tile on the right of the player is a box

                                for (int i = p.x + 1; i < colSize; i++) {   // loop through all boxes to the right
                                    System.out.println(i);
                                    if (boardState[p.y][i] == 2) {     // if it is a block
                                        blocks.add(i);  // add x cordinate to blocks list
                                    } else {       // if not a block
                                        break;     // exit loop
                                    }
                                }

                                for (int i : blocks) {          // loop through items in blocksX array
                                    if (boardState[p.y][i + 1] == 1 || boardState[p.y][i + 1] == 4 || boardState[p.y][i + 1] == 6) { // if  block is next to wall, exit outerloop
                                        break outerloop;
                                    } else {
                                        boardState[p.y][i + 1] = 2;  // otherwise move block left
                                    }
                                }
                                boardState[p.y][p.x] = 0;
                                p.x += 1;
                                boardState[p.y][p.x] = 3;
                                blocks.clear();
                            } else {
                                boardState[p.y][p.x] = 0;
                                p.x += 1;
                                boardState[p.y][p.x] = 3;
                            }
                            break;
                        } else {
                            break;
                        }

                    case "UP":
                        if (p.alive == true) {
                            outerloop:
                            if (boardState[p.y - 1][p.x] == 1) { // if tile above of the player is a wall
                                break;
                            } else if (boardState[p.y - 1][p.x] == 2) {    // if tile above the player is a box

                                for (int i = p.y - 1; i > 0; i--) {   // loop through all boxes to the left
                                    System.out.println(i);
                                    if (boardState[i][p.x] == 2) {     // if it is a block
                                        blocks.add(i);  // add y cordinate to blocks list
                                    } else {       // if not a block
                                        break;     // exit loop
                                    }
                                }

                                for (int i : blocks) {          // loop through items in blocks array
                                    if (boardState[i - 1][p.x] == 1 || boardState[i - 1][p.x] == 4 || boardState[i - 1][p.x] == 6) { // if  wall is above block, exit outerloop
                                        break outerloop;
                                    } else {
                                        boardState[i - 1][p.x] = 2;  // otherwise move block left
                                    }
                                }
                                boardState[p.y][p.x] = 0;
                                p.y -= 1;
                                boardState[p.y][p.x] = 3;
                                blocks.clear();
                            } else {
                                boardState[p.y][p.x] = 0;
                                p.y -= 1;
                                boardState[p.y][p.x] = 3;
                            }
                            break;
                        } else {
                            break;
                        }

                    case "DOWN":
                        if (p.alive == true) {
                            outerloop:
                            if (boardState[p.y + 1][p.x] == 1) { // if tile below of the player is a wall
                                break;
                            } else if (boardState[p.y + 1][p.x] == 2) {    // if tile below of the player is a box

                                for (int i = p.y + 1; i < rowSize; i++) {   // loop through all boxes below
                                    System.out.println(i);
                                    if (boardState[i][p.x] == 2) {     // if it is a block
                                        blocks.add(i);  // add y cordinate to blocks list
                                    } else {       // if not a block
                                        break;     // exit loop
                                    }
                                }

                                for (int i : blocks) {          // loop through items in blocks array
                                    if (boardState[i + 1][p.x] == 1 || boardState[i + 1][p.x] == 4 || boardState[i + 1][p.x] == 6) { // if  block is next to wall, exit outerloop
                                        break outerloop;
                                    } else {
                                        boardState[i + 1][p.x] = 2;  // otherwise move block left
                                    }
                                }
                                boardState[p.y][p.x] = 0;
                                p.y += 1;
                                boardState[p.y][p.x] = 3;
                                blocks.clear();
                            } else {
                                boardState[p.y][p.x] = 0;
                                p.y += 1;
                                boardState[p.y][p.x] = 3;
                            }
                            break;
                        } else {
                            break;
                        }

                    default:
                        throw new AssertionError();
                }
            });
        return new RenderingPackage(this.gameMetadata(), renderingCommands);

    }


    /** Joins this game */
    public RenderingPackage joinGame(String playerName) {
        if (players.containsKey(playerName)) {
            return new RenderingPackage(
                    gameMetadata(),
                    Arrays.stream(new RenderingCommand[] {
                            new NativeCommands.ShowMenuError("That name's not available")
                    }).map((r) -> r.toJson()).toList()
            );
        } else { // pick a random position inside of the block area for the mouse (player)
            Random randInt = new Random();
            int x, y;
            do {
                x = randInt.nextInt(10, 15);
                y = randInt.nextInt(8, 13);
            } while (boardState[y][x] != 0 && boardState[y][x] != 2);

            RodentPlayer p = new RodentPlayer(playerName, x, y, true);
            players.put(playerName, p);
            boardState[p.y][p.x] = 3;
            ArrayList<JsonObject> renderingCommands = new ArrayList<>();
            renderingCommands.add(new LoadClient("RodentsRevenge", "RodentsRevenge", name, playerName).toJson());

            return new RenderingPackage(gameMetadata(), renderingCommands);
        }

    }

}