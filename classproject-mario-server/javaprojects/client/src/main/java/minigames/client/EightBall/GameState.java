package minigames.client.EightBall;

import java.util.ArrayList;
import java.util.HashMap;

public class GameState {
    private Player[] players = new Player[2]; 
    private Player currentPlayer; 
    private static boolean gameOver;
    private boolean playersBallTypesDetermined;
    private HashMap<String, ArrayList<Double>> ballPos = new HashMap<String, ArrayList<Double>>();

    public GameState() {
        this.playersBallTypesDetermined = false;
        initialise(new Player(true), new Player(false));
    }

    private void initialise(Player p1, Player p2) { 
        players[0] = p1; 
        players[1] = p2; 
  
        if (p1.isGoingFirst()) { 
            this.currentPlayer = p1; 
        } else { 
            this.currentPlayer = p2; 
        } 
    } 

    public Player getCurrentPlayer() {
        return this.currentPlayer;
    }

    public Player setCurrentPlayer(Player currentPlayer) {
        return this.currentPlayer = currentPlayer;
    }
    
    public Player getOtherPlayer() {
        for (Player player : players) {
            if (!player.equals(currentPlayer)) {
                return player;
            }
        }
        return null; 
    }

    public boolean isPlayersBallTypesDetermined() {
        return this.playersBallTypesDetermined;
    }

    public void setPlayersBallTypesDetermined(boolean playersBallTypesDetermined) {
        this.playersBallTypesDetermined = playersBallTypesDetermined;
    }
}

