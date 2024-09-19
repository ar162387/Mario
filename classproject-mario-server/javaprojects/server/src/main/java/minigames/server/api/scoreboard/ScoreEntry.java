package minigames.server.api.scoreboard;

import java.util.EnumMap;
import java.util.Map;
import io.vertx.core.json.JsonObject;

public class ScoreEntry {
    public enum Field {
        USERNAME,
        SCORE,
        GAME_NAME,
        DATE_ACHIEVED
    }

    private final Map<Field, String> data;

    public ScoreEntry() {
        this.data = new EnumMap<>(Field.class);
    }

    public ScoreEntry(JsonObject json) {
        this();
        for (Field field : Field.values()) {
            String value = json.getString(toSnakeCase(field.name()));
            if (value != null) {
                set(field, value);
            }
        }
    }

    public String get(Field field) {
        return data.get(field);
    }

    public void set(Field field, String value) {
        data.put(field, value);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        for (Map.Entry<Field, String> entry : data.entrySet()) {
            json.put(toSnakeCase(entry.getKey().name()), entry.getValue());
        }
        return json;
    }

    private String toSnakeCase(String input) {
        return input.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }
}