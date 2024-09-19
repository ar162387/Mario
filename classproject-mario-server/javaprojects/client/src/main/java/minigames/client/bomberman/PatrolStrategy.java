package minigames.client.bomberman;

import javafx.util.Pair;

/**
 * Strategy for AI to move in a random straight patrol vertical or horizontal
 */
public class PatrolStrategy implements MovementStrategy{

    /**
     * Move in a straight line direction up and down
     * @param enemy
     * @param board
     * @return
     */
    @Override
    public Pair<Integer, Integer> move(Enemy enemy, Board board) {
        return (checkCollision(enemy.getDirection(),board,enemy)) ?
                // Go back if collide
                directionToPair(RelativeDirection.BACK.getTrueDirection(enemy)) : directionToPair(enemy.getDirection());


    }
}
