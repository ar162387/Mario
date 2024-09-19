package minigames.client.bomberman;

import javafx.util.Pair;

/**
 * The class implements a movement strategy for an enemy character Based on the Windows 95 screensaver
 * in the Bomberman game. The strategy involves moving the enemy in its current direction
 * and turning right on every intersection, if it reaches a dead end turn around.
 *
 *  Contributors: Lixang Li lli32@myune.edu.au
 */

public class TurnRightStrategy implements MovementStrategy{

    /**
     * Moves the enemy in its current direction. If a collision is detected, the enemy turns
     * right and attempts to move in the new direction. This process continues until a valid
     * move is found.
     *
     * @param enemy
     * @param board
     * @return a Pair containing the change in x and y positions (dx, dy)
     */
    @Override
    public Pair<Integer, Integer> move(Enemy enemy, Board board) {
        Direction relRight = RelativeDirection.RIGHT.getTrueDirection(enemy);
        Direction relStraight = RelativeDirection.STRAIGHT.getTrueDirection(enemy);
        Direction relBack = RelativeDirection.BACK.getTrueDirection(enemy);
        Direction relLeft = RelativeDirection.LEFT.getTrueDirection(enemy);
        if (!checkCollision(relRight,board,enemy)) {
            return directionToPair(relRight);
        }
        if (!checkCollision(relStraight,board,enemy)){
            return directionToPair(relStraight);
        }
        if (!checkCollision(relLeft,board,enemy)) {
            return directionToPair(relLeft);
        }
        else return directionToPair(relBack);
    }
}
