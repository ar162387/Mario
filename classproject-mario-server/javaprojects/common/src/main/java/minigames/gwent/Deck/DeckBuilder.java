package minigames.gwent.Deck;

import minigames.gwent.cards.Card;
import minigames.gwent.cards.CardLoader;

import java.io.IOException;
import java.util.List;

/**
 * This class is responsible for building decks.
 * It currently reads card data from a JSON file and creates a deck from it.
 * In the future, it could be extended to support building multiple decks (one for each faction) based on a card collection.
 */

public class DeckBuilder {

	/**
	 * Builds a deck from a JSON file containing card data.
	 * @return A Deck object containing all the cards from the JSON file.
	 * @throws IOException If there is an error reading the file.
	 */
	public Deck buildDeckFromJson() throws IOException {
		CardLoader cardLoader = new CardLoader();
		List<Card> cards = cardLoader.loadCardsFromJson();

		Deck deck = new Deck(cards, "Test", "Test", "A test deck containing 1 of each card in the json file", "test_001");
		return deck;
	}
}