package minigames.server.database.repositories;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import minigames.server.database.DatabaseUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LeaderboardRepositoryImpl implements LeaderboardRepository {

    private static final Logger logger = LogManager.getLogger(LeaderboardRepositoryImpl.class);

    // Method to add a score to the leaderboard
    @Override
    public Future<Void> addScore(String name, int score, String gameType) {
        Promise<Void> promise = Promise.promise();

        // Updated to include double quotes around table/column names for case sensitivity
        String query = "INSERT INTO leaderboards (name, gametype, score) VALUES ('" +
                name + "', '" + gameType + "', " + score + ")";

        try {
            int rowsAffected = DatabaseUtils.executeUpdate(query);
            if (rowsAffected > 0) {
                promise.complete();
            } else {
                promise.fail("Failed to add score");
            }
        } catch (Exception e) {
            logger.error("Error adding score to leaderboard", e);
            promise.fail(e);
        }

        return promise.future();
    }

    // Method to get the top scores for a particular game type, limited to a certain number
    @Override
    public Future<JsonArray> getTopScores(String gameType, int limit) {
        Promise<JsonArray> promise = Promise.promise();

        // Using double quotes around table and column names to ensure case sensitivity
        String query = "SELECT name, score FROM leaderboards WHERE gametype = '" + gameType + "' " +
                "ORDER BY score DESC FETCH FIRST " + limit + " ROWS ONLY";  // Using FETCH FIRST for Derby

        try {
            ResultSet rs = DatabaseUtils.executeQuery(query);
            JsonArray topScores = new JsonArray();

            while (rs != null && rs.next()) {
                JsonObject score = new JsonObject()
                        .put("name", rs.getString("name"))
                        .put("score", rs.getInt("score"));
                topScores.add(score);
            }
            promise.complete(topScores);
        } catch (SQLException e) {
            logger.error("Error retrieving top scores", e);
            promise.fail(e);
        }

        return promise.future();
    }

    // Method to get all scores for a particular game type
    @Override
    public Future<JsonArray> getAllScores(String gameType) {
        Promise<JsonArray> promise = Promise.promise();

        // Using double quotes around table and column names to ensure case sensitivity
        String query = "SELECT name, score FROM leaderboards WHERE gametype = '" + gameType +
                "' ORDER BY score DESC";

        try {
            ResultSet rs = DatabaseUtils.executeQuery(query);
            JsonArray allScores = new JsonArray();

            while (rs != null && rs.next()) {
                JsonObject score = new JsonObject()
                        .put("name", rs.getString("name"))
                        .put("score", rs.getInt("score"));
                allScores.add(score);
            }

            promise.complete(allScores);
        } catch (SQLException e) {
            logger.error("Error retrieving all scores", e);
            promise.fail(e);
        }

        return promise.future();
    }
}
