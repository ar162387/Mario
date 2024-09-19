package minigames.server.database.repositories;

import java.util.List;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import minigames.server.api.achievement.Achievement;
import minigames.server.api.user.AppUser;

public interface UserRepository {
    Future<String> getHashedPassword(String username);
    Future<Boolean> userExists(String username);
    Future<Boolean> userExists(Long id);
    Future<Void> addUser(AppUser user);
    Future<AppUser> getUserData(String username);
    Future<AppUser> getUserData(Long id);
    Future<String> getUsernameFromEmail(String email);
    Future<List<AppUser>> getAllUsers();
    Future<Void> updateUser(AppUser user);
    Future<Void> incrementPlayTime(String username, long minutes);

    Future<List<Achievement>> getAllAchievements();
    Future<Achievement> getAchievement(String achievementId);
    Future<List<Achievement>> getUserAchievements(String username);
    Future<Void> unlockAchievement(String username, String achievementId);
    Future<Void> unlockAchievementByName(String username, String achievementName);
    Future<Void> addAchievement(Achievement achievement);

    Future<JsonArray> getGameScoreboard(String gameName);
    Future<JsonArray> getUserScores(String username);
    Future<Void> addScore(String username, String gameName, int score);
    Future<JsonArray> getGlobalScoreboard();
}