package minigames.client.geowars;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import minigames.client.geowars.gameobjects.enemies.*;
import minigames.client.geowars.scenes.Level;
import minigames.client.geowars.util.Sound;
import minigames.client.geowars.util.Vector2D;

/**
 * Represents a spawn event in GeoWars.
 * This class holds all the attributes needed by the GameManager to know how and
 * when to spawn a new wave of enemies in a Level.
 */
public class SpawnEvent {
  private static final Logger logger = LogManager.getLogger(SpawnEvent.class);

  private double spawnTime;
  private int enemyType;
  private ArrayList<Vector2D> spawnLocations;
  private int numEnemies;
  private int wavePattern;
  private double spacing;

  public final static int RANDOM = 0;
  public final static int LINE = 1;
  public final static int PLAYER_CIRCLE = 2;
  public final static int CORNERS = 3;
  public final static int BORDER = 4;
  public final static int CLUSTERS = 5;

  public SpawnEvent(double spawnTime, int enemyType, int numEnemies, int wavePattern, double spacing,
      ArrayList<Vector2D> spawnLocations) {
    this.spawnTime = spawnTime;
    this.enemyType = enemyType;
    this.numEnemies = numEnemies;
    this.wavePattern = wavePattern;
    this.spacing = spacing;
    this.spawnLocations = spawnLocations;
  }

  public double getSpawnTime() {
    return spawnTime;
  }

  public int getEnemyType(){
    return enemyType;
  }

  public ArrayList<Enemy> spawnWave(GeoWars engine, LevelManager levelManager) {
    ArrayList<Enemy> wave = new ArrayList<>();

    switch (wavePattern) {
      case RANDOM:
        // On a random pattern, we just spawn 'numEnemies' of 'enemyType' at random
        // locations.
        for (int i = 0; i < numEnemies; i++) {
          Vector2D spawnPosition = findSpawnPosition(levelManager, i);
          Enemy newEnemy = spawnEnemy(engine, spawnPosition);
          wave.add(newEnemy);
        }
        break;
      case LINE:
        break;
      case PLAYER_CIRCLE:
        break;
      case CORNERS:
        break;
      case BORDER:
        break;
      case CLUSTERS:
        break;
    }

    return wave;
  }

  private Vector2D findSpawnPosition(LevelManager levelManager, int currentEnemy) {
    Level currentLevel = levelManager.getCurrentLevel();
    Vector2D[] playableArea = currentLevel.getPlayableArea();
    Vector2D topLeft = playableArea[0];
    Vector2D bottomRight = playableArea[1];

    Vector2D playerPosition = levelManager.getPlayer().getPosition();

    Vector2D spawnPosition = null;

    boolean validPosition = false;

    switch (wavePattern) {
      case RANDOM:
        // On a random pattern, we just find a random spawn location inside the playable
        // area, but further than 'spacing' from the player.
        do {
          double x = Math.random() * (bottomRight.x - topLeft.x) + topLeft.x;
          double y = Math.random() * (bottomRight.y - topLeft.y) + topLeft.y;
          spawnPosition = new Vector2D(x, y);

          // Find the distance^2 from the player
          Vector2D toPlayer = new Vector2D(spawnPosition);
          toPlayer.sub(playerPosition);
          double distance2 = toPlayer.lengthSquared();
          double spacing2 = spacing * spacing;

          // If the distance is greater than the spacing, we are good to go
          if (distance2 > spacing2) {
            validPosition = true;
          }
        } while (!validPosition);

        break;
      case LINE:
        break;
      case PLAYER_CIRCLE:
        break;
      case CORNERS:
        break;
      case BORDER:
        break;
      case CLUSTERS:
        break;
      default:
        break;
    }

    return spawnPosition;
  }

  private Enemy spawnEnemy(GeoWars engine, Vector2D position) {
    Enemy newEnemy = null;

    double rotation = Math.random() * (2 * Math.PI) - Math.PI;

    switch (enemyType) {
      case Enemy.PURPLE:
        newEnemy = new PurpleEnemy(engine, position, rotation);
        break;
      case Enemy.LIGHT_BLUE:
        newEnemy = new LightBlueEnemy(engine, position, rotation);
        break;
      case Enemy.PINK:
        newEnemy = new PinkEnemy(engine, position, rotation);
        break;
      case Enemy.GREEN:
        newEnemy = new GreenEnemy(engine, position, rotation);
        break;
      default:
        break;
    }

    return newEnemy;
  }
}