package minigames.client.geowars.scenes;

import minigames.client.geowars.*;
import minigames.client.geowars.gameobjects.enemies.Enemy;
import minigames.client.geowars.util.*;

public class ArcadeLevel extends Level {

  private double defaultSpawnInterval = 10;
  private double deathSpawnInterval = 3.1;
  private double defaultSafeZone = 100;

  /**
   * Constructor for ArcadeLevel.
   * 
   * @param engine The GeoWars client.
   */
  public ArcadeLevel(GeoWars engine) {
    super(engine, "Fred");
  }

  /**
   * Load the Arcade Level.
   * This method contains the instructions to build the starting conditions for an
   * Arcade Level.
   */
  @Override
  public void load() {
    // Call the superclass load method
    super.load();

    // Player Start Position - Center of the screen
    playerStartPosition = new Vector2D((double) GeoWars.SCREEN_WIDTH / 2, (double) GeoWars.SCREEN_HEIGHT / 2);

    // Player Start Lives - 3
    playerStartingLives = 3;
    super.setPlayerLives(playerStartingLives);

    //Starts the background music when level is loaded. 
    Sound.getInstance().playMusic(Sound.Type.BGM);
  }

  /**
   * Recreate an ArcadeLevel.
   * 
   * @return A new instance of an ArcadeLevel.
   */
  @Override
  public Scene recreate() {
    return new ArcadeLevel(engine);
  }

  /**
   * Generate the next SpawnEvent.
   * 
   * @param lastSpawnTime  The time of the last spawn event. This is used to
   *                       determine the time of the next spawn event.
   * @param levelTime      The current time of the level. This is used to
   *                       determine the current difficulty.
   * @param lastWaveNumber The number of the last wave spawned. This is used to
   *                       determine the type of wave to spawn.
   * @param playerDead     A boolean indicating if the reason this wave is being
   *                       called for is because the player died and wiped the
   *                       level. In this case, the difficulty goes down a little
   *                       bit, and the time of the next spawn event is based on
   *                       the current level time instead of the last spawn time.
   */
  public SpawnEvent getNextSpawnEvent(double lastSpawnTime, double levelTime, int lastWaveNumber, boolean playerDead) {

    double spawnTime;
    if (lastWaveNumber == 0) {
      spawnTime = 0;
    } else if (playerDead) {
      spawnTime = levelTime + deathSpawnInterval;
    } else {
      spawnTime = lastSpawnTime + defaultSpawnInterval;
    }
    int enemyType = lastWaveNumber % 4;
    int numEnemies = lastWaveNumber + 1;

    return new SpawnEvent(spawnTime, enemyType, numEnemies, SpawnEvent.RANDOM, defaultSafeZone, null);
  }
}