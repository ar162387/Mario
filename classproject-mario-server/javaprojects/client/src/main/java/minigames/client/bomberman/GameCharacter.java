package minigames.client.bomberman;

import javafx.scene.Node;

/**
 * The GameCharacter interface represents the common methods that all game characters must implement.
 * Contributors: Daniel Gooden - dgooden@myune.edu.au
 */
public interface GameCharacter {
    void move(int dx, int dy);
    double getX();
    double getY();
    void setPosition(int x, int y);
    void update(long now);
    int getLives();
    void setLives(int lives);
    void hit(long now);
    Node getGraphic();
    void handleDeath(Runnable onFinished);
}
