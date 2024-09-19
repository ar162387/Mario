package minigames.server.api.leaderboard;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import minigames.server.database.repositories.LeaderboardRepository;

public class LeaderboardServiceImpl implements LeaderboardService {

    private final LeaderboardRepository leaderboardRepository;

    public LeaderboardServiceImpl(LeaderboardRepository leaderboardRepository) {
        this.leaderboardRepository = leaderboardRepository;
    }

    @Override
    public Future<Void> addScore(String name, int score, String gameType) {
        return leaderboardRepository.addScore(name, score, gameType);
    }

    @Override
    public Future<JsonArray> getTopScores(String gameType, int limit) {
        return leaderboardRepository.getTopScores(gameType, limit);
    }

    @Override
    public Future<JsonArray> getAllScores(String gameType) {
        return leaderboardRepository.getAllScores(gameType);
    }
}
