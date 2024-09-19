package minigames.client.RodentsRevenge;

/**
 * RodentPlayer class the same as in the server side
 */
class RodentPlayer{

    boolean alive;
    String name;
    int x;
    int y;
    String color;
    int numOfDeath;
    int score;

    public RodentPlayer(String name, int x, int y, boolean alive){
        this.x = x;
        this.y = y;
        this.name = name;
        this.alive = alive;
        this.color = color;
        this.numOfDeath = 0;
        this.score = 0;
    }

    public RodentPlayer(){

    }

    public void setDead() {
        numOfDeath ++; // Increment numOfDeath when the player dies
    }

    public String getName() {
        return name;
    }

    public int getX(){
        return this.x;
    }

    public int getY(){
        return this.y;
    }

    public void setX(int m) {
        x = m;
    }

    public void setY(int n) {
        y = n;
    }
    public boolean isAlive(){
        return this.alive;
    }

    public String getColor() {
        return this.color;
    }

    public void setColor(String mouseColor) {
        color = mouseColor;
    }

    public int getNumOfDeath() {
        return numOfDeath;
    }


    public int getScore() { 
        return score; 
    
    }
    public void incrementScore(int points) { 
        this.score += points; 
    }


}