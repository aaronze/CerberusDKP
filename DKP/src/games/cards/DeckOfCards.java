package games.cards;

import java.util.Collections;
import java.util.LinkedList;

/**
 * @author Aaron
 */
public class DeckOfCards {
    private final LinkedList<Card> cards;

    public DeckOfCards() {
        cards = new LinkedList<>();

        for (int s = 0; s < 4; s++) {
            for (int r = 1; r <= 13; r++) {
                cards.add(new Card(s, r));
            }
        }
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }
    
    /**
     * Returns the next card, or null if there are no more cards.
     * 
     * @return The next card
     */
    public Card drawNextCard() {
        if (cards.isEmpty())
            return null;
        
        return cards.removeFirst();
    }

    public class Card {
        private final int suit;
        private final int rank;
        private boolean isFaceUp = true;

        public Card(int s, int r) {
            suit = s;
            rank = r;
        }

        public int suit() {
            return suit;
        }

        public int rank() {
            return rank;
        }
        
        public void turnover() {
            isFaceUp = !isFaceUp;
        }
        
        public boolean isFaceUp() {
            return isFaceUp;
        }
    }
}
