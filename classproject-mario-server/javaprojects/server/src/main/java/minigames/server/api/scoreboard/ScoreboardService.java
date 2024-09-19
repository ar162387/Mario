package minigames.server.api.scoreboard;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;

public interface ScoreboardService {
    Future<JsonArray> getGameScoreboard(String gameName);
    Future<JsonArray> getUserScores(String username);
    Future<Void> addScore(String username, String gameName, int score);
    Future<JsonArray> getGlobalScoreboard();
}