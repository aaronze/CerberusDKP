package games;

import bot.Gambling;
import games.cards.DeckOfCards;
import games.cards.DeckOfCards.Card;
import java.util.ArrayList;
import java.util.Arrays;
import secure.Account;

/**
 * @author Aaron
 */
public abstract class Game {
    protected boolean isRunning = false;
    protected String[] output;
    private String[] inputs;
    private String param;
    private int betAmount = 1;
    private int winnings = 0;
    private final Account account;
    
    public abstract String getGameName();
    public abstract void newGame();
    public abstract void input();
    
    public Game(Account account) {
        this.account = account;
    }
    
    public final void input(String... inputs) {
        winnings = 0;
        this.inputs = inputs;
        input();
    }
    
    public final String[] output() {
        if (winnings > 0) {
            appendOutput("You have won " + winnings + " plat!");
            account.deposit("Blackjack", winnings);
        }
        
        return output;
    }
    
    public final boolean newGame(int betAmount) {
        this.betAmount = Math.max(1, betAmount);
        if (account.withdraw(getGameName(), this.betAmount)) {
            isRunning = true;
            newGame();
            return true;
        } else {
            setOutput("Insufficient funds to bet that amount.");
        }
        
        return false;
    }
    
    public void endGame() {
        isRunning = false;
    }
    
    public boolean isRunning() {
        return isRunning;
    }
    
    public static DeckOfCards getNewShuffledDeckOfCards() {
        DeckOfCards cards = getNewDeckOfCards();
        cards.shuffle();
        return cards;
    }
    
    public static DeckOfCards getNewDeckOfCards() {
        return new DeckOfCards();
    }
    
    public boolean hasCommand(String command) {
        for (int i = 0; i < inputs.length; i++) {
            String s = inputs[i];
            if (s.toLowerCase().contains(command.toLowerCase())) {
                param = "";
                if (i + 1 < inputs.length)
                    param = inputs[i+1];
                return true;
            }
        }
        return false;
    }
    
    public String getCommandParameter() {
        return param;
    }
    
    public void appendOutput(String... s) {
        ArrayList<String> list = new ArrayList<>();
        list.addAll(Arrays.asList(output));
        list.addAll(Arrays.asList(s));
        output = list.toArray(new String[0]);
    }
    
    public void prependOutput(String... s) {
        ArrayList<String> list = new ArrayList<>();
        list.addAll(Arrays.asList(s));
        list.addAll(Arrays.asList(output));
        output = list.toArray(new String[0]);
    }
    
    public void prependOutput(Card card) {
        ArrayList<String> list = new ArrayList<>();
        list.addAll(Arrays.asList(Gambling.makeCard(card)));
        list.addAll(Arrays.asList(output));
        output = list.toArray(new String[0]);
    }
    
    public void appendOutput(Card card) {
        ArrayList<String> list = new ArrayList<>();
        list.addAll(Arrays.asList(output));
        list.addAll(Arrays.asList(Gambling.makeCard(card)));
        output = list.toArray(new String[0]);
    }
    
    public void prependOutput(Card... cards) {
        ArrayList<String> list = new ArrayList<>();
        list.addAll(Arrays.asList(Gambling.makeHand(cards)));
        list.addAll(Arrays.asList(output));
        output = list.toArray(new String[0]);
    }
    
    public void appendOutput(Card... cards) {
        ArrayList<String> list = new ArrayList<>();
        list.addAll(Arrays.asList(output));
        list.addAll(Arrays.asList(Gambling.makeHand(cards)));
        output = list.toArray(new String[0]);
    }
    
    public void prependOutput(ArrayList<Card> cards) {
        ArrayList<String> list = new ArrayList<>();
        list.addAll(Arrays.asList(Gambling.makeHand(cards)));
        list.addAll(Arrays.asList(output));
        output = list.toArray(new String[0]);
    }
    
    public void appendOutput(ArrayList<Card> cards) {
        ArrayList<String> list = new ArrayList<>();
        list.addAll(Arrays.asList(output));
        list.addAll(Arrays.asList(Gambling.makeHand(cards)));
        output = list.toArray(new String[0]);
    }
    
    
    public void setOutput(String... s) {
        output = s;
    }
    
    public void setOutput(Card card) {
        output = Gambling.makeCard(card);
    }
    
    public void setWinnings(int value) {
        winnings = Math.max(0, value);
    }
    
    public int getWinnings() {
        return winnings;
    }
    
    public int getBet() {
        return betAmount;
    }
    
    public boolean addBettingAmount(int bet) {
        if (account.withdraw(getGameName(), bet)) {
            betAmount += bet;
            return true;
        }
        
        return false;
    }
    
    protected boolean canAffordToBet(int amount) {
        return account.getBalance() >= amount;
    }
}
