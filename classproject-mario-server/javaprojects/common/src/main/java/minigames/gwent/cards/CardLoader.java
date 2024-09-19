package minigames.gwent.cards;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class CardLoader {

    public List<Card> loadCardsFromJson() throws IOException {
        // Read the JSON file content from the resources folder
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("gwent/CardData.json");
        if (inputStream == null) {
            throw new IOException("Resource not found: CardData.json");
        }

        String jsontext = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        JsonArray jsonArray = new JsonArray(jsontext);

        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject jsonCard = jsonArray.getJsonObject(i);
            cards.add(CardCreator.createCardFromJson(jsonCard));
        }

        return cards;
    }
}