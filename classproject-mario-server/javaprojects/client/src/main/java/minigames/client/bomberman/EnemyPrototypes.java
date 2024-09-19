package minigames.client.bomberman;

import javafx.scene.image.ImageView;

/**
 * Enums for bomberman enemy types
 *
 * Contributors: Lixang Li lli32@myune.edu.au
 */
public enum EnemyPrototypes {
    GUARD, MINER, WIZARD;

    /**
     * Create a prototype
     * @param x
     * @param y
     * @param difficulty
     * @return
     */
    public Enemy getPrototype(int x, int y,int difficulty) {
        Enemy enemy = null;
        EnemyMovementStrategies strategy = null;
        Integer points = difficulty;
        switch (this) {
            case GUARD -> {
                int lives = difficulty;
                enemy = new Enemy(x, y, lives);
                strategy = EnemyMovementStrategies.PATROL;
            }

            case MINER -> {
                int speed = difficulty;
                enemy = new Enemy(x, y,1, speed);

                strategy = EnemyMovementStrategies.WINDOWS95;
            }
            case WIZARD -> {
                enemy = new Enemy(x, y);

                strategy = EnemyMovementStrategies.SEEK;
            }
        }

        // Set Movement

        enemy.setMovementStrategy(strategy.get());

        // Set graphics
        enemy.setGraphic(getGraphic());

        // Set points
        enemy.setPoints(points);

        return enemy;
    }
    /**
     * Get the graphic imageview for the movement ai
     * @return
     */
    public ImageView getGraphic() {
        return switch (this) {
            case GUARD -> BombermanGraphics.getInstance().getTileSprite(TileType.ENEMY_ARMOUR);
            case MINER -> BombermanGraphics.getInstance().getTileSprite(TileType.ENEMY);
            case WIZARD -> BombermanGraphics.getInstance().getTileSprite(TileType.ENEMY_WIZARD);
        };
    }
    /**
     * Animate graphic
     */
    public ImageView getAnimationFrame(int idx) {
        return null; //TODO
    }
}
