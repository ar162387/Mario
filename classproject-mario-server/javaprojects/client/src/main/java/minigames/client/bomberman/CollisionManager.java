package minigames.client.bomberman;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

import java.awt.Point;
import java.util.Map;
import java.util.Set;

public class CollisionManager {

    private static CollisionManager instance;

    private CollisionManager() {
        // Private constructor to prevent instantiation
    }

    public static CollisionManager getInstance() {
        if (instance == null) {
            instance = new CollisionManager();
        }
        return instance;
    }

    public boolean checkCollision(Node node1, Node node2) {
        if (node1.getBoundsInParent().intersects(node2.getBoundsInParent())) {
            DebugManager.getInstance().log("Collision detected");
            return true;
        }
        return false;
    }

    public boolean checkTileCollision(Node movingObject, Map<Point, Tile> tiles) {
        for (Tile tile : tiles.values()) {
            if (!tile.passable() && checkCollision(movingObject, tile.graphic())) {
                return true; // Collision detected with a non-passable tile
            }
        }
        return false; // No collision detected
    }

    public void drawBounds(Pane pane, Node node) {
        var bounds = node.getBoundsInParent();
        Rectangle rect = new Rectangle(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight());
        rect.setStroke(javafx.scene.paint.Color.RED);
        rect.setFill(javafx.scene.paint.Color.TRANSPARENT);
        pane.getChildren().add(rect);
    }

    public void clearBounds(Pane pane) {
        pane.getChildren().removeIf(node -> node instanceof Rectangle);
    }
}
