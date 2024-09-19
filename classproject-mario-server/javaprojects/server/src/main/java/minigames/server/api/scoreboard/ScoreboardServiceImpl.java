package minigames.server.api.scoreboard;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import minigames.server.database.repositories.UserRepository;

import javax.inject.Inject;

public class ScoreboardServiceImpl implements ScoreboardService {
    private final UserRepository userRepository;

    @Inject
    public ScoreboardServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Future<JsonArray> getGameScoreboard(String gameName) {
        return userRepository.getGameScoreboard(gameName);
    }

    @Override
    public Future<JsonArray> getUserScores(String username) {
        return userRepository.getUserScores(username);
    }

    @Override
    public Future<Void> addScore(String username, String gameName, int score) {
        return userRepository.addScore(username, gameName, score);
    }

    @Override
    public Future<JsonArray> getGlobalScoreboard() {
        return userRepository.getGlobalScoreboard();
    }
}