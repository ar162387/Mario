package minigames.client.minesweeper;

/**
 * This class is used to handle the user associated with debugging ninjas minesweeper game.
 * Author: Matt Hayes
 */

public class user {
    //Initialise variables
    public String userName;
    public int score;

    //User constructor
    public user(String userName, int score) {
        this.userName = userName;
        this.score = score;
    }

    //getters
    public int getScore() {
        return score;
    }
    
    public String getUserName() {
        return userName;
    }

    //setters
    public void setScore(int score) {
        this.score = score;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
}
