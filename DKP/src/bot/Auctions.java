package bot;

import util.ScreenSearch;
import database.MemTable;
import database.Table;
import ui.DiceMonitor;
import static java.lang.Thread.sleep;
import java.util.ArrayList;
import java.util.Date;
import util.EverQuest;
import static util.EverQuest.*;
import util.SQL;
import util.StopWatch;

/**
 * @author Aaron
 */
public class Auctions {

    /**
     * Maximum amount a single player can bid for an item.
     */
    public static int MAXIMUM_BID = 50;

    /**
     * Time taken between each auction call.
     */
    public static int AUCTION_INTERVAL = 10;

    /**
     * Maximum hours per raid.
     */
    public static int MAX_HOURS = 3;

    /**
     * Current hour of the raid.
     */
    public static int CURRENT_HOUR = 0;

    /**
     * Table containing the current auction and the bids.
     */
    public static final Table auctions;

    /**
     * Table containing tell counts for players to ignore players who make too
     * many bids per auction.
     */
    private static final Table ignoreList;

    /**
     * Table of people who have won an item in the current raid.
     */
    private static final Table winners;

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
    public static volatile boolean isBidding = false;

    /**
     * Final time for auction to finish will be time of the last bid + 2
     * seconds.
     */
    private static volatile long cutoffTime = 0;

    private static SQL auctionSQL;

    private static ArrayList<String> usableClasses;

    /**
     * Create the tables and set the data types.
     */
    static {
        auctions = new MemTable("auctions");
        auctions.addRows("Name", "Bid", "RA", "DKP", "Roll");

        ignoreList = new MemTable("ignore");
        ignoreList.addRows("Name", "Tells");

        winners = new MemTable("winners");
        winners.addRows("Name", "Bid", "Item");
    }

    public static void reset() {
        try {
            auctions.removeAllEntries();
            ignoreList.removeAllEntries();
            winners.removeAllEntries();

            usableClasses = null;
            auctionSQL.closeConnection();
            auctionSQL = new SQL();

            cutoffTime = 0;
            isBidding = false;
            numberOfItems = 1;
            nameOfItem = "";
            CURRENT_HOUR = 0;
            AUCTION_INTERVAL = 10;
            MAX_HOURS = 3;
            MAXIMUM_BID = 50;

            ScreenSearch.buildNewScreenshot();
            while (!ScreenSearch.find(ScreenSearch.CLOSE_ITEM_IMAGE, null).isEmpty()) {
                EverQuest.closeItem();
                EverQuest.closeItem();

                ScreenSearch.buildNewScreenshot();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts a new auction on the given item.
     *
     * @param itemName Full name of the item to be auctioned.
     * @param numOfItems Number of that item to be auctioned.
     */
    public static void startAuction(final String itemName, final int numOfItems) {
        DiceMonitor.isBidding = true;

        // Clear the auction and ignore tables
        auctions.removeAllEntries();
        ignoreList.removeAllEntries();
        numberOfItems = numOfItems;
        nameOfItem = itemName;

        auctionSQL = new SQL();

        // Set the cutoff time to 40 seconds after the auction starts - In order to give 10 seconds after it finishes for calculations
        setCutoffTime(getTimeInSeconds() + (AUCTION_INTERVAL * 3));

        Thread t;
        t = new Thread() {
            @Override
            public void run() {
                try {

                    String strSendTells = DiceMonitor.CHANNEL + "Send tells to me for ";
                    if (numberOfItems > 1) {
                        strSendTells += numberOfItems + "x ";
                    }

                    if (!EverQuest.isTesting)
                        inspectItem();

                    sendToEQ(strSendTells, "<LINK ITEM>" + itemName, ", 1 - " + MAXIMUM_BID + " DKP. " + (AUCTION_INTERVAL * 3) + " Seconds");
                    sleep(AUCTION_INTERVAL * 1000);

                    sendToEQ(strSendTells, "<LINK ITEM>" + itemName, ", 1 - " + MAXIMUM_BID + " DKP. " + (AUCTION_INTERVAL * 2) + " Seconds");
                    sleep(AUCTION_INTERVAL * 1000);

                    sendToEQ(strSendTells, "<LINK ITEM>" + itemName, ", 1 - " + MAXIMUM_BID + " DKP. " + (AUCTION_INTERVAL) + " Seconds");
                    sleep(AUCTION_INTERVAL * 1000);

                    sendToEQ(strSendTells, "<LINK ITEM>" + itemName, ", 1 - " + MAXIMUM_BID + " DKP. Closed");

                    while (getTimeInSeconds() < getCutoffTime()) {
                        sleep(200);
                    }

                    Table winner = null;
                    String name = "";
                    String bid = "";
                    String ra = "";

                    // Declare the winners
                    for (int i = 0; i < numberOfItems; i++) {
                        // Loop through all potential winners until a bid is found that is valid
                        while ((winner = getNextWinner()) != null) {
                            name = winner.select("Name");
                            bid = winner.select("Bid");
                            ra = winner.select("RA");

                            int bidCheck = Integer.parseInt(winner.select("Bid"));
                            int availableDKP = auctionSQL.getDKP(name);
                            if (bidCheck > availableDKP) {
                                EverQuest.sendTell(name, "You cannot afford to bid that much. You only have [" + availableDKP + "] DKP");
                                auctions.removeEntry(name);
                                winner = null; // Invalidate winner
                            } else {
                                break; // Winner found, stop looping.
                            }
                        }

                        // Placed after loop to fix bug if all bids were invalidated during loop
                        if (winner == null) {
                            sendToEQ(DiceMonitor.CHANNEL + "No Bids.");
                            closeItem();
                            continue;
                        }

                        // Declare the winner
                        int roll = 0;
                        try {
                            roll = Integer.parseInt(winner.select("Roll"));
                        } catch (Exception e) {
                            System.out.println(e);
                        }

                        if (roll == 0) {
                            sendToEQ(DiceMonitor.CHANNEL + "Gratz " + name + " on ", "<LINK ITEM>" + itemName, " for " + bid + " DKP");
                        } else {
                            sendToEQ(DiceMonitor.CHANNEL + "Gratz " + name + " on ", "<LINK ITEM>" + itemName, " for " + bid + " DKP, with a roll of " + roll);
                        }

                        // Add the won item to the database
                        if (!EverQuest.isTesting) {
                            auctionSQL.addItem(name, itemName, Integer.parseInt(bid));
                        }

                        // Remove player from the database
                        auctions.removeEntry(name);
                    }

                    if (!EverQuest.isTesting)
                        closeItem();

                    DiceMonitor.isBidding = false;
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
     *
     * @return Time taken for request
     */
    public static double placeBid(String playerName, int bid) {
        StopWatch timer = new StopWatch();

        // Work out how many tells the person has placed
        int recievedTells = 0;
        if (ignoreList.where("Name", playerName).isEmpty()) {
            ignoreList.addEntry(playerName, "0");
        } else {
            recievedTells = Integer.parseInt(ignoreList.where("Name", playerName).select("Tells"));
        }

        // If player is on the ignore list, exit immediately
        if (recievedTells >= 5) {
            return timer.stop();
        }

        // Add to the cutoff time enough time to process this bid
        setCutoffTime(getTimeInSeconds() + 1);

        // If player is at 4 tells, warn them they will be ignored next tell
        if (recievedTells == 4) {
            EverQuest.sendTell(playerName, "Warning: Future tells during this auction will be ignored.");
        }

        // Increase the tell counter from this player
        ignoreList.updateEntry("Name", playerName, "Tells", "" + (recievedTells + 1));

        // Check if the bid is valid
        if (bid < 0) {
            EverQuest.sendTell(playerName, "A negative bid? Really? Wow, I can't believe you would think Raidbot would be that flawed ...");
            return timer.stop();
        }

        // Check for a cancelling bid
        if (bid == 0) {
            // Do they have a current bid?
            if (auctions.where("Name", playerName).isEmpty()) {
                // They don't have a bid... Odd...
                EverQuest.sendTell(playerName, "You cannot place a bid of 0 DKP.");
            } else {
                // Remove their current bid
                auctions.removeEntry(playerName);
                EverQuest.sendTell(playerName, "Your bid has been removed.");
            }
            return timer.stop();
        }

        // Make sure bid is less then maximum
        if (bid > MAXIMUM_BID) {
            EverQuest.sendTell(playerName, "You cannot be more then " + MAXIMUM_BID);
            return timer.stop();
        }

        // Make sure they have enough DKP to pay for it
        // int availableDKP = auctionSQL.getDKP(playerName);
        // if (bid > availableDKP) {
        //    EverQuest.sendTell(playerName, "You cannot afford to bid that much. You only have [" + availableDKP + "] DKP");
        //   return timer.stop();
        // }
        // Warn players against bidding multiple times if they have
        if (recievedTells >= 1) {
            EverQuest.sendTell(playerName, "Please avoid placing multiple bids in the future.");
        }

        // Check if player has made previous bids
        //   int ra = auctionSQL.getRA(playerName, SQL.DAYS_30);
        if (auctions.where("Name", playerName).isEmpty()) {
            // No previous bids
            auctions.addEntry(playerName, "" + bid, "", "", "");
        } else {
            // Has a previous bid
            auctions.updateEntry("Name", playerName, "Bid", "" + bid);
        }

        // Thank them for the bid 
        EverQuest.sendTell(playerName, "Thank you for your bid!");

        // Set cutoff time to this bid end + 3 seconds
        setCutoffTime(getTimeInSeconds() + 2);

        return timer.stop();
    }

    public static Table getNextWinner() {
        return getNextRandomWinner();
    }

    /**
     * Returns the next winner and erases them from the auction list.
     *
     * Multiple calls returns the next winner.
     *
     * @return Current winner of the auction
     */
    public static Table getNextRAWinner() {
        // Get the maximum bid made
        int maxBid = (int) auctions.max("Bid");

        // Get a table of people who made the maximum bid possible
        Table maxBidders = auctions.where("Bid", "" + maxBid);

        // If the table is empty, return nothing
        if (maxBidders.isEmpty()) {
            return null;
        }

        // If there is only one winner, return them
        if (maxBidders.size() == 1) {
            return maxBidders;
        }

        // Otherwise find the bidder with the most Raid Attendance
        int maxRA = (int) maxBidders.max("RA");
        Table maxBidAndRA = maxBidders.where("RA", "" + maxRA);

        // If there is only one winner, return them
        if (maxBidAndRA.size() == 1) {
            return maxBidAndRA;
        }

        // Otherwise find the bidder with the most total DKP
        int maxDKP = (int) maxBidAndRA.max("DKP");
        Table maxBidRAAndDKP = maxBidAndRA.where("DKP", "" + maxDKP);

        // If there is only one winner, return them
        if (maxBidRAAndDKP.size() == 1) {
            return maxBidRAAndDKP;
        }

        // Otherwise... er... return the first winner? This is pretty unlikely.
        return maxBidRAAndDKP.where("Name", maxBidRAAndDKP.getEntry(0)[0]);
    }

    public static Table getNextRandomWinner() {
        // Get the maximum bid made
        int maxBid = (int) auctions.max("Bid");

        // Get a table of people who made the maximum bid possible
        Table maxBidders = auctions.where("Bid", "" + maxBid);

        // If the table is empty, return nothing
        if (maxBidders.isEmpty()) {
            return null;
        }

        // If there is only one winner, return them
        if (maxBidders.size() == 1) {
            return maxBidders;
        }

        System.out.println(maxBidders);

        int largestRandom = 0;
        int winnerIndex = -1;

        ArrayList<String[]> bidEntries = maxBidders.getEntries();
        for (int i = 0; i < bidEntries.size(); i++) {
            String name = bidEntries.get(i)[0];
            System.out.println(i + ": " + name);

            Table bidder = maxBidders.where("Name", name);

            int random = 0;
            try {
                random = Integer.parseInt(bidder.select("Roll"));
            } catch (Exception e) {
            }

            if (random == 0) {
                int maxRandom = getScore(bidder);

                random = (int) (Math.random() * maxRandom);

                EverQuest.sendTell(name, "You were involved in a " + maxBidders.size()
                        + " way tie. Your bonuses allowed you to roll up to " + maxRandom
                        + " but this time you rolled a " + random + ".");

                maxBidders.updateEntry("Name", name, "Roll", "" + random);
                auctions.updateEntry("Name", name, "Roll", "" + random);
            }

            if (random > largestRandom) {
                largestRandom = random;
                winnerIndex = i;
            }
        }

        if (winnerIndex == -1) {
            return null;
        }

        String winnerName = maxBidders.getEntry(winnerIndex)[0];
        return maxBidders.where("Name", winnerName);
    }

    public static int getScore(Table bidder) {
        int score = 1000;
        String name = bidder.select("Name");

        // Grant bonuses based on classes -- Commented out for speed
        //   String playerClass = auctionSQL.getPlayerClass(name);
        //   if (playerClass.equals(SQL.WARRIOR)) score += 500;
        //   if (playerClass.equals(SQL.SHADOWKNIGHT)) score += 300;
        //   if (playerClass.equals(SQL.PALADIN)) score += 300;
        //   if (playerClass.equals(SQL.CLERIC)) score += 100;
        // Add penalty based on level
        // int level = auctionSQL.getLevel(name);
        // int levelPenalty = 50 * (100 - level);
        // score -= levelPenalty;
        // Add bonus from Raid Attendance
        int ra = auctionSQL.getRA(name, SQL.DAYS_30);
        score += ra * 100;

        // Add bonus from Total DKP Spendable
        //  int dkp = Integer.parseInt(bidder.select("DKP"));
        //  score += dkp * 10;
        // Add bonus for days since last item won up to 30 days
        int days = auctionSQL.getDaysSinceLastWonItem(name);
        score += Math.min(30, days) * 10;

        return score;
    }

    /**
     * Manually adds a winner to the list of winners.
     *
     * @param name Player name that won the item
     * @param dkp Amount of DKP they paid for it
     * @param item Name of the item they won
     */
    public static void addWinner(String name, String dkp, String item) {
        winners.addEntry(name, dkp, item);
    }

    /**
     * Removes all winners from the list of winners.
     */
    public static void clearWinners() {
        winners.removeAllEntries();
    }

    /**
     * Sends a tell to the given player name with a list of all people who have
     * won items.
     *
     * @param playerName Name of player to send tells to
     */
    public static void speakWinners(String playerName) {
        for (String[] entry : winners.getEntries()) {
            String name = entry[0];
            String dkp = entry[1];
            String item = entry[2];

            EverQuest.sendTell(playerName, name + " won " + item + " for " + dkp + " DKP.");
        }
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
     * Sets the cutoff time in seconds for when bids should no longer be
     * processed.
     *
     * @param time Cutoff time in seconds
     */
    public static synchronized void setCutoffTime(long time) {
        if (time > cutoffTime) {
            cutoffTime = time;
        }
    }

}
