package games.cards;

import games.Game;
import games.cards.DeckOfCards.Card;
import java.util.ArrayList;
import java.util.Scanner;
import secure.Account;

/**
 * @author Aaron
 */
public class Blackjack extends Game {
    private DeckOfCards deckOfCards;
    private ArrayList<Card> playersHand;
    private ArrayList<Card> dealerHand;
    
    /**
     * Simulate a game of blackjack via console
     * 
     * @param args 
     */
    public static void main(String[] args) {
        Game game = new Blackjack(new Account("Druad"));
        game.newGame(12);
        
        Scanner kb = new Scanner(System.in);
        String line;
        do {
            
            for (String s : game.output()) {
                System.out.println(s);
            }
            
            line = kb.nextLine();
            
            if (game.isRunning()) {
                game.input(line);
            }
            
            if (!game.isRunning()) {
                for (String s : game.output()) {
                    System.out.println(s);
                }
                
                System.out.println("Blackjack has ended.");
                break;
            }
        } while (true);
    }

    /**
     * Create a new game of blackjack using the given account for deposits and withdrawals.
     * 
     * @param account 
     */
    public Blackjack(Account account) {
        super(account);
    }

    /**
     * The name of the game, for reference on transaction logs for accounts.
     * 
     * @return Name of this game.
     */
    @Override
    public String getGameName() {
        return "Blackjack";
    }
    
    /**
     * Called by Raidbot when the player initializes a new Blackjack game.
     */
    @Override
    public void newGame() {
        // Shuffle a new deck of cards
        deckOfCards = getNewShuffledDeckOfCards();
        
        // Empty the players and the dealer's hands
        playersHand = new ArrayList<>();
        dealerHand = new ArrayList<>();
        
        // Draw a card into both the player and the dealer
        dealerHand.add(deckOfCards.drawNextCard());
        playersHand.add(deckOfCards.drawNextCard());
        
        // Draw a card face down for the dealer
        Card dealerFaceDown = deckOfCards.drawNextCard();
        dealerFaceDown.turnover();
        dealerHand.add(dealerFaceDown);
        
        // Draw another face up card for the player
        playersHand.add(deckOfCards.drawNextCard());
        
        setOutput(
                "You're playing Blackjack!"
        );
        
        appendOutput(dealerHand);
        appendOutput(" ");
        appendOutput(playersHand);
        
        appendOutput(
                "/reply hit",
                "/reply stay"
        );
        
        if (canDoubleDown(playersHand))
            appendOutput("/reply double down");
        
        if (canSplit(playersHand))
            appendOutput("/reply split");
    }
    
    @Override
    public void input() {
        setOutput(
                "/reply hit",
                "/reply stay"
        );
        
        // If player has requested to double down
        if (hasCommand("double")) {
            if (canDoubleDown(playersHand)) {
                doubleDown();
            }
            
            // Double down ends player's turn
            return;
        }
        
        // If player has requested to split
        if (hasCommand("split")) {
            if (canSplit(playersHand)) {
                split();
            }
            
            // Splitting ends a player's turn
            return;
        }
        
        // If the player has requested to hit
        if (hasCommand("hit")) {
            hit();
        }
        
        // Daisy chain checks so "/t Gambler hit stay" becomes possible and will hit, and then stay.
        if (hasCommand("stay")) {
            setOutput();
            dealerTurn();
        }
    }
    
    public void doubleDown() {
        // Check if account has enough credit to double down
        if (addBettingAmount(getBet())) {
            // Add a new card to the player's hand
            Card card = deckOfCards.drawNextCard();
            playersHand.add(card);
            prependOutput(card);

            dealerTurn();
        } else {
            setOutput(
                "Insufficient funds to double down.",
                "/reply hit",
                "/reply stay"
            );
        }
    }
    
    public void hit() {
        // Add a new card to the player's hand
        Card card = deckOfCards.drawNextCard();
        playersHand.add(card);
        prependOutput(playersHand);

        // Count the value of the cards in the player's hand
        int score = scoreHand(playersHand);

        // Declare blackjack or bust if they occured
        if (score == 21) {
            setWinnings((int)(getBet() * 2.5));
            setOutput("BLACKJACK!");
            prependOutput(playersHand);
            endGame();
        } else if (score > 21) {
            setOutput("#PLAYER Bust!");
            prependOutput(playersHand);
            endGame();
        }
    }
    
    public void dealerTurn() {
        appendOutput("Dealer plays...");
        
        // Turn over second card
        dealerHand.get(1).turnover();
        int dealerValue = scoreHand(dealerHand);
        
        if (dealerValue >= 17) {
            appendOutput(dealerHand);
        }
        
        // Dealer stays on 17 or above
        while (dealerValue < 17) {
            Card card = deckOfCards.drawNextCard();
            dealerHand.add(card);
            dealerValue = scoreHand(dealerHand);
            appendOutput(dealerHand);
            appendOutput(" ");
        }

        if (dealerValue > 21) {
            appendOutput("Dealer Busts... You win!");
            setWinnings(getBet() * 2);
        } else {
            // If dealer beat player
            if (dealerValue >= scoreHand(playersHand)) {
                appendOutput("Dealer won this round.");
            } else {
                appendOutput("You beat the dealer!");
                setWinnings(getBet() * 2);
            }
        }
        
        endGame();
    }

    public int scoreHand(ArrayList<Card> hand) {
        int score = 0;
        
        for (Card card : hand) {
            score += Math.min(10, card.rank());
        }
        
        if (score <= 11) {
            for (Card card : hand) {
                if (card.rank() == 1) {
                    score += 10;
                    break;
                }
            }
        }
        
        return score;
    }
    
    public boolean canDoubleDown(ArrayList<Card> hand) {
        int score = 0;
        
        // Can only double down with first 2 cards
        if (hand.size() > 2)
            return false;
        
        for (Card card : hand) {
            score += Math.min(10, card.rank());
        }
        
        return (score == 10) || (score == 11);
    }
    
    public boolean canSplit(ArrayList<Card> hand) {
        // Can only split with first 2 cards
        if (hand.size() > 2)
            return false;
        
        // Can split if ranks are the same, but count everything above 10 as equal
        return Math.min(10, hand.get(0).rank()) == Math.min(10, hand.get(1).rank());
    }
    
    public void split() {
        if (addBettingAmount(getBet())) {
            // Play 2 separate games with each card
            ArrayList<Card> cards = new ArrayList<>();
            cards.addAll(playersHand);
            
            playersHand.clear();

            // Play the first card
            playersHand.add(cards.remove(0));
            hit();
            dealerTurn();

            String[] firstOutput = output;
            setOutput();

            // Play the second card
            playersHand.add(cards.remove(0));
            hit();
            dealerTurn();

            prependOutput(firstOutput);
            endGame();
        }
    }
}
