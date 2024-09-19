package minigames.server.api.leaderboard;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;

public interface LeaderboardService {
    Future<Void> addScore(String name, int score, String gameType);
    Future<JsonArray> getTopScores(String gameType, int limit);
    Future<JsonArray> getAllScores(String gameType);
}
