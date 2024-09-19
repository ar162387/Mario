package minigames.client.bomberman;

import javafx.util.Pair;

import static java.lang.Math.abs;

/**
 * Implements a super simple seeker enemy ai that attempts
 * to move vertically towards player than horizontally or else it just patrols
 */
public class SeekPlayerStrategy implements MovementStrategy{
    @Override
    public Pair<Integer, Integer> move(Enemy enemy, Board board) {
        int playerTileX = (int)((board.getPlayer().getX() ) / 48);
        int playerTileY = (int)((board.getPlayer().getY() -1) / 48);
        // The player bounds are not centred on the middle point of the tile
        // Distance points were manually adjusted to accommodate for these differences
        int playerCenterX = playerTileX * 48 + 47;
        int playerCenterY = playerTileY * 48 + 47;

        int enemyX = (int)enemy.getX();
        int enemyY = (int)enemy.getY();
        Direction vertical;
        Direction horizontal;
        if (playerCenterY > enemyY) vertical = Direction.DOWN;
        else vertical = Direction.UP;
        if (playerCenterX > enemyX) horizontal = Direction.RIGHT;
        else horizontal = Direction.LEFT;
        int dx = Math.abs(playerCenterX-enemyX);
        int dy = Math.abs(playerCenterY-enemyY);
//        System.out.println("player " + playerY);
//        System.out.println("enemy " + enemyY);
        if (!checkCollision(vertical,board,enemy) && dy != 0)  return directionToPair(vertical); // Go towards player on Y plane
        else if (!checkCollision(horizontal,board,enemy) && dx != 0) return directionToPair(horizontal); // Go towards player on X plane
        else return getUnstuck(board,enemy);

    }
}
