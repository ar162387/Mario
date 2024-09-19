package minigames.server.api.user;

import java.util.List;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import minigames.server.api.achievement.Achievement;

public interface UserService {

    Future<String> registerUser(String username, String password, String email);
    Future<String> loginUser(String username, String password);
    Future<JsonObject> getUserData(String username);
     public Future<String> getUsernameFromEmail(String email);
    Future<List<JsonObject>> getAllUsers();
    Future<Void> updateUser(String username, JsonObject newUserDetails);
    Future<Void> incrementPlayTime(String username, long minutes);
    
    Future<JsonArray> getAllAchievements();
    Future<JsonObject> getAchievement(String achievementId);
    Future<JsonArray> getUserAchievements(String username);
    Future<Void> unlockAchievement(String username, String achievementId);
    Future<Void> unlockAchievementByName(String username, String achievementName);
    Future<Void> addAchievement(Achievement achievement);
    
    Future<JsonArray> getUserScores(String username);
    Future<Void> addScore(String username, String gameName, int score);
    Future<JsonArray> getGlobalScoreboard();
    Future<JsonArray> getGameScoreboard(String gameName);
    
}