package bot;

import util.ScreenSearch;
import database.MemTable;
import database.Table;
import ui.DiceMonitor;
import static java.lang.Thread.sleep;
import java.util.ArrayList;
import java.util.Date;
import util.EverQuest;
import static util.EverQuest.closeItem;
import static util.EverQuest.inspectItem;
import static util.EverQuest.sendToEQ;
import util.SQL;
import java.awt.Robot;
import java.awt.event.KeyEvent;

/**
 * @author Aaron
 */
public class DiceRolls {
    /**
     * Time taken between each auction call.
     */ 
    public static int AUCTION_INTERVAL = 10;
    
    /**
     * Table containing the current auction and the bids.
     */
    public static final Table randoms;
    
    /**
     * Table containing a list of players who have already randomed.
     */
    private static final Table ignoreList;
    
    /**
     * How many of the item to auction.
     */
    private static int numberOfItems = 1;
    
    /**
     * Name of the item being auctioned.
     */
    public static String nameOfItem;
    
    /**
     * If true then an auction is currently underway.
     */
    public static volatile boolean isRolling = false;
    
    /**
     * Final time for auction to finish will be time of the last bid + 2 seconds.
     */
    private static volatile long cutoffTime = 0;
    
    private static SQL auctionSQL;
    
    private static ArrayList<String> usableClasses;
    
    /**
     * Create the tables and set the data types.
     */
    static {
        randoms = new MemTable("auctions");
        randoms.addRows("Name", "Random");
        
        ignoreList = new MemTable("ignore");
        ignoreList.addRows("Name");
    }
    
    /**
     * Starts a new auction on the given item.
     * 
     * @param itemName Full name of the item to be auctioned.
     * @param numOfItems Number of that item to be auctioned.
     */
    public static void startAuction(final String itemName, final int numOfItems) {
        isRolling = true;
        
        // Clear the auction and ignore tables
        randoms.removeAllEntries();
        ignoreList.removeAllEntries();
        numberOfItems = numOfItems;
        nameOfItem = itemName;
        
        auctionSQL = new SQL();
        
        // Set the cutoff time to 30 seconds after the auction starts
        setCutoffTime(getTimeInSeconds() + (AUCTION_INTERVAL * 3));
        
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    
                    String strSendTells = DiceMonitor.CHANNEL + "Send any tell to me for ";
                    if (numberOfItems > 1) strSendTells += numberOfItems + "x ";
                    
                    inspectItem();
                    
                    usableClasses = ScreenSearch.findClasses();
                    
                    sendToEQ(strSendTells, "<LINK ITEM>"+itemName, " " + (AUCTION_INTERVAL*3) + " Seconds");
                    sleep(AUCTION_INTERVAL * 1000);
                    
                    sendToEQ(strSendTells, "<LINK ITEM>"+itemName, " " + (AUCTION_INTERVAL*2) + " Seconds");
                    sleep(AUCTION_INTERVAL * 1000);
                    
                    sendToEQ(strSendTells, "<LINK ITEM>"+itemName, " " + (AUCTION_INTERVAL) + " Seconds");
                    sleep(AUCTION_INTERVAL * 1000);
                    
                    sendToEQ(strSendTells, "<LINK ITEM>"+itemName, " Closed");
                        
                    while (getTimeInSeconds() < getCutoffTime()) {
                        sleep(500);
                    } 
                    
                    // Declare the winners
                    for (int i = 0; i < numberOfItems; i++) {
                        Table winner = getNextWinner();
                        
                        // If no winner, declare that and continue with the auction
                        if (winner == null) {
                            sendToEQ(DiceMonitor.CHANNEL + "No one sent me a tell :( ");
                            closeItem();
                            continue;
                        }
                        
                        String name = winner.select("Name");
                        String random = winner.select("Random");
                     //   Table runUp = getNextWinner();
                      //  String runner = runUp.select("Name2");
                      //  String random2 = runUp.select("Random2");
                                
                        // Declare the winner
                        sendToEQ(DiceMonitor.CHANNEL + "Gratz " + name + " on ", "<LINK ITEM>"+itemName, " with a roll of " + random + "!");
                        
                        // Remove player from the database
                        randoms.removeEntry(name);
                    }
                    
                    closeItem();
                    

                    isRolling = false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }
    
    /**
     * Places a bid for the current auctioned item.
     * 
     * @param playerName Name of the player
     * @param bid Amount of DKP bid
     */
    public static void placeBid(String playerName) {
        // Work out how many tells the person has placed
        if (!ignoreList.where("Name", playerName).isEmpty())
            return;
        
        int random = (int)(Math.random()*32000);
        randoms.addEntry(playerName, ""+random);
        
        // Thank them for the bid 
        String tell = "A magic die was rolled, it could have been any number from 0 to 32000. This time it turned up a " + random + "... "; 
        if (random < 2) tell += "Wow. You are the winner for unluckiest person award.";
        else if (random < 100) tell += "Honestly, thats really pathetic...Were you even trying?";
        else if (random < 500) tell += "Do you realize you only had about a 1% chance of rolling that low?";
        else if (random < 1000) tell += "You might still stand a chance at winning. I doubt it though.";
        else if (random < 2000) tell += "Vegas is not for you.";
        else if (random < 3000) tell += "Giving me a few compliments may or may not help your random numbers....I feel pretty...oh so pretty...";
        else if (random < 6000) tell += "It's low, but not low enough to complain.";
        else if (random < 10000) tell += "I bet theres a few people in the raid that would gladly have rolled this number....The sad thing is you'll probably still lose.";
        else if (random < 16000) tell += "Not bad... But not quite good either.";
        else if (random < 20000) tell += "Well, at least it's over halfway.";
        else if (random < 25000) tell += "Nice. That's a decent roll!";
        else if (random < 28000) tell += "Raidbot felt sorry for you. Here's a decent roll!";
        else if (random < 29000) tell += "Even if you don't win the item. You can feel pride in a high roll like this!  If you didn't win the item, feel free to complain to Druad.";
        else if (random < 30000) tell += "You might just win this won. Nice roll!";
        else if (random < 31000) tell += "Your luck is impressive, I'm a bit jealous.";
        else if (random < 31500) tell += "The high roll gods were on your side today!";
        else if (random < 31800) tell += "Wow! That item is as good as yours!";
        else if (random < 31997) tell += "I would be really suprised if you lost this, but knowing your luck...";
        else tell += "I call hax. You have been reported.";
            
        if (random == 42) {
             tell = "A magic die was rolled, it could have been any number from 0 to 32000. This time it turned up a " + random + "... " +
                     "The answer to the ultimate question of life, the universe and everything. But probably not for this item.";
        }
        if (random == 69) {
             tell = "A magic die was rolled, it could have been any number from 0 to 32000. This time it turned up a " + random + "... " +
                     "*Giggles* Yep, I'm immature. Normally it's really awesome, but not for winning items."; 
        }
        if (random == 666) {
             tell = "A magic die was rolled, it could have been any number from 0 to 32000. This time it turned up a " + random + "... " +
                     "Will something terrible happen to Raidbot if you lose this Auction? That number could be a sign of an impending apocalypse. Or a bug."; 
        }
        if (random == 8008) {
             tell = "A magic die was rolled, it could have been any number from 0 to 32000. This time it turned up a " + random + "... " +
                     "If your a dude who owned a pocket calculator. Then you know why this number is great..."; 
        }
        if (random == 12345) {
             tell = "A magic die was rolled, it could have been any number from 0 to 32000. This time it turned up a " + random + "... " +
                     "Woah, how did you work out my Security Code. Erm. I mean... Raidbot's security code isn't 12345, I don't know what you are talking about."; 
        }
        if (random == 31415) {
             tell = "A magic die was rolled, it could have been any number from 0 to 32000. This time it turned up a " + random + "... " +
                     "My personal favorite number. Did you notice that it's the first 5 digits of Pi? Nope? Only me?"; 
        }
             
            
        EverQuest.sendTell(playerName, tell);
        
        ignoreList.addEntry(playerName);
        
        // Set cutoff time to this bid end + 1 second
        setCutoffTime(getTimeInSeconds() + 1);
    }
    
    /**
     * Returns the next winner and erases them from the auction list.
     * 
     * Multiple calls returns the next winner.
     * 
     * @return Current winner of the auction
     */
    public static Table getNextWinner() {
        // Get the maximum bid made
        int maxBid = (int)randoms.max("Random");
        
        // Get a table of people who made the maximum bid possible
        Table maxBidders = randoms.where("Random", ""+maxBid);
        
        // If the table is empty, return nothing
        if (maxBidders.isEmpty())
            return null;
        
        // If the table has multiple winners, select one at random
        if (maxBidders.size() > 1) {
            return maxBidders.where("Name", maxBidders.getEntry((int)(Math.random()*maxBidders.size()))[0]);
        }
        
        return maxBidders;
    }

    
    /**
     * Returns the time in seconds of right now.
     * 
     * @return Time in seconds
     */
    public static long getTimeInSeconds() {
        return new Date().getTime() / 1000;
    }
    
    /**
     * Returns the cutoff time for when bids should stop being processed.
     * 
     * @return Cutoff time in seconds
     */
    public static synchronized long getCutoffTime() {
        return cutoffTime;
    }
    
    /**
     * Sets the cutoff time in seconds for when bids should no longer be processed.
     * 
     * @param time Cutoff time in seconds
     */
    public static synchronized void setCutoffTime(long time) {
        cutoffTime = time;
    }
    public static void closeTest() {
        try {
            Robot robot = new Robot();
            
      //      robot.keyPress(KeyEvent.VK_F1);
        //    robot.delay(100);
         //   robot.keyPress(KeyEvent.VK_F2);
         //   robot.delay(10);
            robot.keyPress(KeyEvent.VK_SLASH);
         //   robot.delay(10);
          //  robot.keyPress(KeyEvent.VK_BACK_SPACE);
            robot.delay(100);
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.delay(20);
            robot.keyPress(KeyEvent.VK_ESCAPE);
            robot.delay(10);
            robot.keyPress(KeyEvent.VK_ESCAPE);
           // robot.delay(10);
           // robot.keyPress(KeyEvent.VK_ESCAPE);
           // robot.delay(10);
        }
        catch (Exception e) {
            e.printStackTrace();
        
        }
    }
}

