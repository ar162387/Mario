package minigames.client.geowars;

import java.util.*;

public class GeoWarsScore implements Comparable<GeoWarsScore> {

  private int score;
  private double time;
  private String playerName;
  private String levelName;

  public GeoWarsScore(int score, double time, String playerName, String levelName) {
    this.score = score;
    this.time = time;
    this.playerName = playerName;
    this.levelName = levelName;
  }

  public int getScore() {
    return score;
  }

  public double getTime() {
    return time;
  }

  public String getPlayerName() {
    return playerName;
  }

  public String getLevelName() {
    return levelName;
  }

  @Override
  public String toString() {
    return "Score: " + score + " Time: " + time + " Player: " + playerName + " Level: " + levelName;
  }

  @Override
  public int compareTo(GeoWarsScore other) {
    int ret = 0;
    if (this.score == other.getScore()) {
      if (this.time == other.getTime()) {
        ret = 0;
      } else if (this.time < other.getTime()) {
        ret = 1;
      } else {
        ret = -11;
      }
    } else {
      if (this.score < other.getScore()) {
        ret = -1;
      } else {
        ret = 1;
      }
    }
    return ret;
  }

  /**
   * Get the top scores from a list of scores.
   * 
   * @param scores    The list of scores.
   * @param numScores The number of top scores to get.
   * @return The top scores.
   */
  public static ArrayList<GeoWarsScore> getTopScores(ArrayList<GeoWarsScore> scores, int numScores) {
    ArrayList<GeoWarsScore> topScores = new ArrayList<>();
    Collections.sort(scores, Collections.reverseOrder());
    for (int i = 0; i < numScores; i++) {
      if (i >= scores.size()) {
        break;
      }
      topScores.add(scores.get(i));
    }
    return topScores;
  }

}
