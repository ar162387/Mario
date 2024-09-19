package minigames.server.RodentsRevenge;

import java.util.*;
import java.util.List;
import java.awt.*;
import minigames.server.RodentsRevenge.RodentsRevenge;
import minigames.server.RodentsRevenge.Pathfinding;
import minigames.server.RodentsRevenge.RodentPlayer;


/**
 * Create Cat object
 */
class Cat {
    int x;
    int y;
    int[][] boardState;

    public Cat(int x, int y, int[][] boardState) {
        this.x = x;
        this.y = y;
        this.boardState = boardState;

    }

    /**
     * path finding algorithm for cat to find the shortest path to the mouse
     */
    public void update() {

        Point nextMove = Pathfinding.getNextMove(x, y, boardState, RodentsRevenge.colSize, RodentsRevenge.rowSize, RodentsRevenge.players.values());

        if(nextMove!=null){
            boardState[y][x] = 0;
            x = (int) nextMove.getX();
            y = (int) nextMove.getY();
        }
        else{
            doRandomMove();
        }

    }

    /**
     * if the cat can't find a path, then move randomly
     */
    private void doRandomMove() {
        Random randInt = new Random();
        boardState[y][x] = 0;
        int[] listX = {x - 1, x, x + 1};
        int[] listY = {y - 1, y, y + 1};
        do {
            x = listX[randInt.nextInt(listX.length)];
            y = listY[randInt.nextInt(listY.length)];
        } while (boardState[y][x] != 0);
    }

    /**
     * display cat on the board
     */
    public void display() {
        boardState[y][x] = 4;
    }

    /**
     * catDead function returns true if cat is dead. Cat is considered dead if surrounded
     * by walls or boxes on all sides
     * @param cat object
     * @return true if cat is dead, else returns false.
     */
    public boolean catDead(Cat cat) {
        ArrayList<Integer> surroundingBoxes = new ArrayList<Integer> ();
        boolean[] trapped = new boolean[8];

        // Add coordinates of surrounding boxes
        surroundingBoxes.add(boardState[cat.y+1][cat.x]);
        surroundingBoxes.add(boardState[cat.y-1][cat.x]);
        surroundingBoxes.add(boardState[cat.y][cat.x+1]);
        surroundingBoxes.add(boardState[cat.y][cat.x-1]);
        surroundingBoxes.add(boardState[cat.y-1][cat.x+1]); // Top Right corner
        surroundingBoxes.add(boardState[cat.y-1][cat.x-1]); // Top Left
        surroundingBoxes.add(boardState[cat.y+1][cat.x+1]); // Bottom Right corner
        surroundingBoxes.add(boardState[cat.y+1][cat.x-1]); // Bottom Left

        for (int i=0; i<surroundingBoxes.size(); i++){
            // if surrounding box is either wall/box/another cat/cheese
            if ( surroundingBoxes.get(i) == 1 || surroundingBoxes.get(i) == 2 || surroundingBoxes.get(i) ==4 || surroundingBoxes.get(i) ==6){
                trapped[i] = true;
            } else {
                return false;
            }
        }
        for (boolean j: trapped){ //loop through trapped array.
            if (j != true) {      // if there is a false value, then cat is not trapped
                return false;
            }
        }
        return true;
    }
}
