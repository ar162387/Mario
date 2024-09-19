package minigames.server.api.profile;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import minigames.server.database.repositories.ProfileRepository;

import javax.inject.Inject;

public class ProfileServiceImpl implements ProfileService {
    private final ProfileRepository profileRepository;

    @Inject
    public ProfileServiceImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public Future<JsonObject> getProfile(String username) {
        return profileRepository.getProfile(username)
            .map(profile -> profile != null ? profile.toJson() : new JsonObject());
    }

    @Override
    public Future<Void> createProfile(String username, JsonObject profileContent) {
        Profile profile = new Profile(profileContent);
        return profileRepository.createProfile(username, profile);
    }

    @Override
    public Future<Void> updateProfile(String username, JsonObject updatedProfileContent) {
        return profileRepository.getProfile(username)
            .compose(existingProfile -> {
                if (existingProfile == null) {
                    return Future.failedFuture("Profile not found");
                }
                Profile updatedProfile = new Profile(updatedProfileContent);
                return profileRepository.updateProfile(username, updatedProfile);
            });
    }

    @Override
    public Future<Void> incrementPlayTime(String username, long minutes) {
        return profileRepository.getProfile(username)
            .compose(profile -> {
                if (profile == null) {
                    return Future.failedFuture("Profile not found");
                }
                String currentPlayTime = profile.get(Profile.Field.TOTAL_PLAY_TIME_MINUTES);
                long newPlayTime = Long.parseLong(currentPlayTime != null ? currentPlayTime : "0") + minutes;
                profile.set(Profile.Field.TOTAL_PLAY_TIME_MINUTES, String.valueOf(newPlayTime));
                return profileRepository.updateProfile(username, profile);
            });
    }
}