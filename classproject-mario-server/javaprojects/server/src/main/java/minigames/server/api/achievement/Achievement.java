package minigames.server.api.achievement;

import io.vertx.core.json.JsonObject;

public class Achievement {
    private final Long id;
    private final String game;
    private final String name;
    private final String description;
    private final Integer points;
    private final String dateAchieved;
    private final String image;

    public Achievement(Long id, String game, String name, String description, Integer points, String dateAchieved, String image) {
        this.id = id;
        this.game = game;
        this.name = name;
        this.description = description;
        this.points = points;
        this.dateAchieved = dateAchieved;
        this.image = image;
    }

    public Long getId() {
        return id;
    }

    public String getGame() {
        return game;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Integer getPoints() {
        return points;
    }

    public String getDateAchieved() {
        return dateAchieved;
    }

    public String getImage() {
        return image;
    }

    public JsonObject toJson() {
        return new JsonObject()
            .put("id", id)
            .put("game", game)
            .put("name", name)
            .put("description", description)
            .put("points", points)
            .put("dateAchieved", dateAchieved)
            .put("image", image);
    }

    public JsonObject toJsonNoDate() {
        return new JsonObject()
            .put("id", id)
            .put("game", game)
            .put("name", name)
            .put("description", description)
            .put("points", points)
            .put("image", image);
    }
}