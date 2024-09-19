package minigames.client.bomberman;

import javafx.scene.image.ImageView;

/**
 * Enums for movement strategies for bomberman enemies
 *
 * Contributors: Lixang Li lli32@myune.edu.au
 */
public enum EnemyMovementStrategies {
    PATROL, WINDOWS95,SEEK;

    /**
     * Return the movement strategy
     * @return
     */
    public MovementStrategy get() {
        return switch (this) {
            case PATROL -> new PatrolStrategy();
            case WINDOWS95 -> new TurnRightStrategy();
            case SEEK -> new SeekPlayerStrategy();
        };
    }
}
