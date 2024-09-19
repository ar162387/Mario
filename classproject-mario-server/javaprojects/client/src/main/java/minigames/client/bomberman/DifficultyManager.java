package minigames.client.bomberman;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.Random;

/**
 * Implements a difficulty manager to construct the levels based on difficulty
 */
public class DifficultyManager {

    private int difficultyFactor;
    private static DifficultyManager difficultyManagerSingleton;
    public static DifficultyManager getInstance() {
        if (difficultyManagerSingleton == null) {
            difficultyManagerSingleton =  new DifficultyManager();
        }
        return difficultyManagerSingleton;
    }

    /**
     * Make a level based on the current difficulty
     */
    public Level getLevel(int difficultyFactor) {
        final int height = GameConstants.BOARD_NO_OF_ROWS;
        final int width = GameConstants.BOARD_NO_OF_COLUMNS;

        // Max of difficultyfactor * 2 or 1
        int enemies = Math.max(1,difficultyFactor << 1);

        final boolean border = true;
//
//        //TODO: Every odd level include pillars etc
//        boolean pillars = (difficultyFactor % 2 == 1);
        final boolean pillars = true;

        int percentDescructible = 10 + (difficultyFactor << 1);  // 10 + difFactor * 2, its a shift


        // TODO: Future implementations
        // level.setTerrainType(terrain); // Change the getTileSprite to a different number
         // level.setEnemyType(enemyType); future implementationss
//

        // For singlePlayer
        return new Level(height,width,enemies,border,pillars,percentDescructible);


//        return level;
    }

    /**
     *  Gets an enemy based on the current level
     *  Has a lot of parameters here
     *  Uses factory design pattern
     * @return
     */
    public Enemy getEnemy(Integer x, Integer y) {
        Random random = new Random();
        // Random enemy out of three atm
        EnemyPrototypes[] values = EnemyPrototypes.values();

        int randomIdx = random.nextInt(values.length);

        Optional<EnemyPrototypes> who = Optional.ofNullable(values[randomIdx]);
        //Return a miner if null
        return who.orElse(EnemyPrototypes.MINER).getPrototype(x,y,difficultyFactor);
        // END RANDOM
    }

    /**
     * Get level difficulty
     */
    public int getDifficultyFactor() {
        return difficultyFactor;
    }
    /**
     * Reset level difficulty to 1
     */
    public int resetDifficultyFactor() {
        difficultyFactor = 1;
        return  difficultyFactor;
    }
    /**
     * Increase level difficulty
     */
    public int levelUp(int number) {
        difficultyFactor += number;
        return difficultyFactor;
    }


    /**
     * Singleton constructor
     */
    private DifficultyManager(){}





}
