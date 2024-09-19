package minigames.client.snake.util;

/**
 * The Observer interface defines a method for receiving updates from a subject
 * - the subject is SnakeGraphic and it maintains a list of Observers and
 * notifies them when it moves.
 */

public interface Observer {

    void update();
}