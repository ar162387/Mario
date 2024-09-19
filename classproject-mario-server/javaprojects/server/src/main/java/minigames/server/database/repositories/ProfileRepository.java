package minigames.server.database.repositories;

import io.vertx.core.Future;
import minigames.server.api.profile.Profile;

public interface ProfileRepository {
    Future<Profile> getProfile(String username);
    Future<Void> createProfile(String username, Profile profile);
    Future<Void> updateProfile(String username, Profile profile);
}