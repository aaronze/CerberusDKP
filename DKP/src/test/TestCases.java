package test;

import bot.Auctions;
import java.util.ArrayList;
import ui.DiceMonitor;
import util.EverQuest;

/**
 * This class deals with automatically testing Raidbot functionality
 * for bare operating minimum standards.
 * 
 * This class will be organized by priority and it will check each case 
 * independently and yield a function report.
 * 
 * Ideally, this test case will be run for each pull request and a copy
 * of successful cases be appended to the pull request description.
 * 
 * @author Aaron
 */
public class TestCases {
    private static ArrayList<String> logger = new ArrayList<>();
    private static ArrayList<String> report = new ArrayList<>();
    
    public static void main(String[] args) {
        System.out.println("Starting Test Cases, Please Wait");
        
        // Set up the testing environment
        EverQuest.isTesting = true;
        DiceMonitor.CHANNEL = "/gu ";
        
        // Start tests in a new thread
        Thread testingThread = new Thread() {
            public void run() {
                testAuctions();
            }
        };
        testingThread.start();
    }
    
    private static void testAuctions() {
        // Set up testing environment
        Auctions.AUCTION_INTERVAL = 1;
        
        int numberOfTests = 0;
        int testsPassed = 0;
        
        
        // Test single auction with one valid bid with standard naming
        logger.clear();
        Auctions.startAuction("test", 1);
        Auctions.placeBid("TestPlayer", 1);
        waitForTime(10);
        numberOfTests++;
        String winner = getLastLog();
        if (winner == null) {
            reportFailure("No winners found");
        } else if (!winner.contains("TestPlayer")) {
            reportFailure("Winner has incorrect name. Expected: TestPlayer, Found: " + winner);
        } else if (!winner.contains("1 DKP")) {
            reportFailure("Winner has the wrong bid amount. Expected: 1, Found: " + winner);
        } else {
            testsPassed++;
        }
        
        // Test single auction with two valid bids with standard naming
        logger.clear();
        Auctions.startAuction("test", 1);
        Auctions.placeBid("TestPlayer1", 5);
        Auctions.placeBid("TestPlayer2", 10);
        waitForTime(10);
        numberOfTests++;
        winner = getLastLog();
        if (winner == null) {
            reportFailure("No winners found");
        } else if (!winner.contains("TestPlayer2")) {
            reportFailure("Wrong winner. Expected: TestPlayer2, Found: " + winner);
        } else if (!winner.contains("10 DKP")) {
            reportFailure("Winner has the wrong bid amount. Expected: 10, Found: " + winner);
        } else {
            testsPassed++;
        }
        
        // Test single auction with two equal valid bids with standard naming
        logger.clear();
        Auctions.startAuction("test", 1);
        Auctions.placeBid("TestPlayer1", 10);
        Auctions.placeBid("TestPlayer2", 10);
        waitForTime(10);
        numberOfTests++;
        winner = getLastLog();
        if (winner == null) {
            reportFailure("No winners found");
        } else if (!winner.contains("roll")) {
            reportFailure("Winners did not tie and roll");
        } else {
            testsPassed++;
        }
        
        // Tally the tests
        System.out.println("Auction Tests Passed: " + testsPassed + " / " + numberOfTests);
        printReport();
    }
    
    private static void reportFailure(String message) {
        report.add("Test Failed: " + message);
        reportStackTrace(new Exception());
        report.add("---------------------");
    }
    
    private static void reportStackTrace(Exception e) {
        report.add(e.getStackTrace()[1].toString());
    }
    
    private static void printReport() {
        System.out.println("-------------------------");
        System.out.println("BEGIN REPORT");
        System.out.println("-------------------------");
        
        for (String s : report) {
            System.out.println(s);
        }
        
        if (report.isEmpty()) {
            System.out.println();
            System.out.println("ALL TESTS COMPLETED SUCCESSFULLY");
            System.out.println();
        }
        
        System.out.println("-------------------------");
        System.out.println("END REPORT");
        System.out.println("-------------------------");
        
        report.clear();
    }
    
    private static void waitForTime(double seconds) {
        try {
            Thread.sleep((long)(seconds * 1000));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void log(String s) {
        logger.add(s);
        System.out.println("> " + s);
    }
    
    public static String getLastLog() {
        return logger.get(logger.size()-1);
    }
}
