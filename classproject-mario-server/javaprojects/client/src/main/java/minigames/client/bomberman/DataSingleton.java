package minigames.client.bomberman;

import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import javafx.application.Platform;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class DataSingleton {

    private static DataSingleton instance;
    private Vertx vertx;
    private WebClient webClient;

    // A map to store different data types by name (leaderboards, achievements, etc.)
    private Map<String, JsonArray> dataStore;

    private static final String HOST = "localhost";
    private static final int PORT = 41234;

    private DataSingleton() {
        vertx = Vertx.vertx();
        WebClientOptions options = new WebClientOptions().setDefaultHost(HOST).setDefaultPort(PORT);
        webClient = WebClient.create(vertx, options);
        dataStore = new HashMap<>();
    }

    // Get the singleton instance
    public static DataSingleton getInstance() {
        if (instance == null) {
            instance = new DataSingleton();
        }
        return instance;
    }

    // Generic method to fetch data from any endpoint
    public void fetchData(String endpoint, String dataKey, Consumer<JsonArray> callback) {
        webClient.get(endpoint)
                .as(BodyCodec.jsonArray())
                .send(ar -> {
                    if (ar.succeeded()) {
                        JsonArray response = ar.result().body();
                        dataStore.put(dataKey, response); // Store the fetched data with its key
                        if (callback != null) {
                            Platform.runLater(() -> callback.accept(response));  // Run on JavaFX thread if necessary
                        }
                    } else {
                        System.out.println("Failed to fetch data: " + ar.cause().getMessage());
                    }
                });
    }

    // Method to post data to any endpoint (POST)
    public void postData(String endpoint, JsonObject data, Consumer<JsonObject> callback) {
        webClient.post(endpoint)
                .sendJsonObject(data, ar -> {
                    if (ar.succeeded()) {
                        JsonObject response = ar.result().bodyAsJsonObject();
                        if (callback != null) {
                            Platform.runLater(() -> callback.accept(response));  // Run on JavaFX thread if necessary
                        }
                    } else {
                        System.out.println("Failed to post data: " + ar.cause().getMessage());
                    }
                });
    }

    // Method to get cached data from the dataStore
    public JsonArray getData(String key) {
        return dataStore.getOrDefault(key, new JsonArray()); // Return data or empty JsonArray if not found
    }

    // Method to periodically data
    public void startAutoFetch(String endpoint, String dataKey, long intervalMillis, Consumer<JsonArray> callback) {
        vertx.setPeriodic(intervalMillis, id -> fetchData(endpoint, dataKey, callback));
    }

    public void stopAutoFetch() {
        vertx.setPeriodic(0, id -> dataStore.remove(dataStore.keySet().iterator().next()));
    }


}
