package minigames.client.EightBall;

/**
 * Player class is used to store information about the player.
 * This includes the player's name, the ball type they are playing as, and the number of shots they have left.
 */
public class Player {
    /**
     * Player fields
     * 
     * goesFirst: true if the player is going first, false otherwise
     * ballType: the ball type the player is playing as from BallType enum. Neutral by default
     * numOfShots: the number of shots the player has left. 1 by Default
     */
    private boolean goesFirst;
    private BallType ballType = BallType.NEUTRAL;
    private int numOfShots = 1;

    /**
     * Constructor for the Player class.
     * @param goesFirst true if the player is going first, false otherwise
     */
    public Player(boolean goesFirst) {
        this.goesFirst = goesFirst;
    }

    /**
     * Sets the ball type the player is playing as.
     * This is determined by the first ball the player sinks.
     * @param ballType the ball type the player is playing as from BallType enum
     */
    public void setPlayerBallType(BallType ballType) {
        this.ballType = ballType;
    }

    /**
     * @return the ball type the player is playing as from BallType enum
     */   
    public BallType getPlayerBallType() {
        return ballType;
    }

    /**
     * Sets the number of shots the player has left.
     * Incremented when a ball is sunk
     * @param numOfShots the number of shots the player has left
     */
    public void setNumOfShots(int numOfShots) {
        this.numOfShots = numOfShots;
    }

    /**
     * @return the number of shots the player has left
     */
    public int getNumOfShots() {
        return numOfShots;
    }

    /**
     * @return true if the player is going first, false otherwise
     */
    public boolean isGoingFirst() {
        return this.goesFirst;
    }
}