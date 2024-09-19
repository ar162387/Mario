package minigames.server.database.repositories;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import minigames.server.api.profile.Profile;
import minigames.server.database.DatabaseUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProfileRepositoryImpl implements ProfileRepository {
    private static final Logger logger = LogManager.getLogger(ProfileRepositoryImpl.class);

    @Override
    public Future<Profile> getProfile(String username) {
        Promise<Profile> promise = Promise.promise();
        String query = "SELECT * FROM profiles WHERE username = '" + username + "'";
        ResultSet rs = DatabaseUtils.executeQuery(query);
        try {
            if (rs != null && rs.next()) {
                Profile profile = createProfileFromResultSet(rs);
                promise.complete(profile);
            } else {
                promise.complete(null);
            }
        } catch (SQLException e) {
            promise.fail(e);
        }
        return promise.future();
    }

    @Override
    public Future<Void> createProfile(String username, Profile profile) {
        Promise<Void> promise = Promise.promise();
        String query = "INSERT INTO profiles (username, first_name, last_name, date_of_birth, bio, favorite_game, total_play_time_minutes) VALUES ('" +
                username + "', '" +
                profile.get(Profile.Field.FIRST_NAME) + "', '" +
                profile.get(Profile.Field.LAST_NAME) + "', '" +
                profile.get(Profile.Field.DATE_OF_BIRTH) + "', '" +
                profile.get(Profile.Field.BIO) + "', '" +
                profile.get(Profile.Field.FAVORITE_GAME) + "', " +
                Integer.parseInt(profile.get(Profile.Field.TOTAL_PLAY_TIME_MINUTES)) + ")";
        int rowsAffected = DatabaseUtils.executeUpdate(query);
        if (rowsAffected > 0) {
            promise.complete();
        } else {
            promise.fail("Failed to create profile");
        }
        return promise.future();
    }

    @Override
    public Future<Void> updateProfile(String username, Profile profile) {
        Promise<Void> promise = Promise.promise();
        String query = "UPDATE profiles SET " +
                "first_name = '" + profile.get(Profile.Field.FIRST_NAME) + "', " +
                "last_name = '" + profile.get(Profile.Field.LAST_NAME) + "', " +
                "date_of_birth = '" + profile.get(Profile.Field.DATE_OF_BIRTH) + "', " +
                "bio = '" + profile.get(Profile.Field.BIO) + "', " +
                "favorite_game = '" + profile.get(Profile.Field.FAVORITE_GAME) + "', " +
                "total_play_time_minutes = " + Integer.parseInt(profile.get(Profile.Field.TOTAL_PLAY_TIME_MINUTES)) + " " +
                "WHERE username = '" + username + "'";
        int rowsAffected = DatabaseUtils.executeUpdate(query);
        if (rowsAffected > 0) {
            promise.complete();
        } else {
            promise.fail("Failed to update profile");
        }
        return promise.future();
    }

    private Profile createProfileFromResultSet(ResultSet rs) throws SQLException {
        Profile profile = new Profile();
        profile.set(Profile.Field.FIRST_NAME, rs.getString("first_name"));
        profile.set(Profile.Field.LAST_NAME, rs.getString("last_name"));
        profile.set(Profile.Field.DATE_OF_BIRTH, rs.getString("date_of_birth"));
        profile.set(Profile.Field.BIO, rs.getString("bio"));
        profile.set(Profile.Field.FAVORITE_GAME, rs.getString("favorite_game"));
        profile.set(Profile.Field.TOTAL_PLAY_TIME_MINUTES, String.valueOf(rs.getInt("total_play_time_minutes")));
        return profile;
    }
}