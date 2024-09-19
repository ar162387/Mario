package minigames.client.gwent;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import minigames.gwent.cards.Card;
import minigames.gwent.Deck.Deck;
import minigames.gwent.Deck.DeckBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DeckBuilderTest {

	private DeckBuilder deckBuilder;

	@BeforeEach
	void setup() {
		deckBuilder = new DeckBuilder();
	}

	@Test
	void testBuildDeckFromJson() throws IOException {
		Deck deck = deckBuilder.buildDeckFromJson();

		assertNotNull(deck, "Deck should not be null");
		List<Card> cards = deck.getCards();
		assertNotNull(cards, "Cards list should not be null");
		assertEquals(29, cards.size(), "Deck should contain 29 cards");
	}
}
