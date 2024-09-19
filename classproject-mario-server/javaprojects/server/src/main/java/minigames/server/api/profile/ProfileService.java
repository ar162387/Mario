package minigames.server.api.profile;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

public interface ProfileService {
    Future<JsonObject> getProfile(String username);
    Future<Void> createProfile(String username, JsonObject newProfile);
    Future<Void> updateProfile(String username, JsonObject updatedProfile);
    Future<Void> incrementPlayTime(String username, long minutes);
}