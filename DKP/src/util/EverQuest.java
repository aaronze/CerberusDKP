package util;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import secure.Account;
import deprecated.MainMenu;
import test.TestCases;

/**
 * Plug for EverQuest functions.
 * 
 * @author Aaron
 */
public class EverQuest {
    /**
     * Is true when a message is currently being sent to EverQuest.
     */
    public static volatile boolean isSpeaking = false;
    
    /**
     * Is true when being tested
     */
    public static volatile boolean isTesting = false;
    
    /**
     * Is true when raid is locked
     */
    public static boolean isRaidLocked = false;
    
    /**
     * Is true when the raid window is open
     */
    public static boolean raidWindowOpen = false;
    
    public static void reset() {
        isSpeaking = false;
        isTesting = false;
        isRaidLocked = false;
        raidWindowOpen = false;
    }
    
    /**
     * Sends a single message to EverQuest composed of the strings passed in.
     * 
     * The messages will be added linearly and then sent to EverQuest in one hit.
     * If one of the strings is the keyword: <LINK ITEM> then it will place the item
     * link instead. The item link will be taken from the most recently inspected item.
     * 
     * @param lines 
     */
    public static synchronized void sendToEQ(String... lines) {
        if (isTesting) {
            String s = "";
            for (String line : lines)
                s += line;
            TestCases.log(s);
            return;
        }
        
        while (isSpeaking) try {Thread.sleep(10);} catch (Exception e) {}
        
        isSpeaking = true;
        
        try {
            Robot robot = new Robot();
            
            robot.delay(40);
            robot.keyPress(KeyEvent.VK_SLASH);
            robot.delay(60);
            robot.keyRelease(KeyEvent.VK_SLASH);
            
            robot.delay(40);
            robot.keyPress(KeyEvent.VK_BACK_SPACE);
            robot.delay(60);
            robot.keyRelease(KeyEvent.VK_BACK_SPACE);   
                        
            for (String line : lines) {
                if (line.toLowerCase().contains("<link item>")) {
                    if (!linkItem()) {
                        // Link item failed
                        line = line.substring(line.toLowerCase().indexOf("<link item>") + "<link item>".length());
                    } else {
                        continue;
                    }
                }
                
                StringSelection stringSelection = new StringSelection(line);
                Clipboard clpbrd = Toolkit.getDefaultToolkit ().getSystemClipboard ();
                clpbrd.setContents (stringSelection, null);

                robot.keyPress(KeyEvent.VK_CONTROL);
                robot.delay(10);
                robot.keyPress(KeyEvent.VK_V);
                robot.delay(100);
                robot.keyRelease(KeyEvent.VK_CONTROL);
                robot.delay(10);
                robot.keyRelease(KeyEvent.VK_V);
                robot.delay(60);
            }
            
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.delay(60);
            robot.keyRelease(KeyEvent.VK_ENTER);
            robot.delay(40);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        isSpeaking = false;
    }
    
    /**
     * Sends a tell to a player in EverQuest with a message.
     * 
     * @param name Player to send the tell to
     * @param message What to send to the player
     */
    public static void sendTell(String name, String message) {
        String[] str = new String[] { "/tell " + name + " ", message };
        
        sendToEQ(str);
    }
    
    /**
     * Inspects the item last sent to the bot in a tell window at the specified screen position.
     * 
     * Remember to close the item after you finish using it.
     */
    public static void inspectItem() {
        if (isTesting)
            return;
        
        int tellPositionX = 440;
        int tellPositionY = 600;
        
        try {
            Robot robot = new Robot();
            
            robot.mouseMove(tellPositionX, tellPositionY);
            robot.delay(40);
            robot.mousePress(MouseEvent.BUTTON1_MASK);
            robot.delay(40);
            robot.mouseRelease(MouseEvent.BUTTON1_MASK);
            robot.delay(500);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Places the link item inside the tell message.
     * 
     * @return True if the item was successfully linked
     */
    public static boolean linkItem() {
        if (isTesting)
            return false;
        
        ScreenSearch.buildNewScreenshot();
        ArrayList<Point> minimisePoint = ScreenSearch.find(ScreenSearch.MINIMISE_ITEM_IMAGE, null);
        
        if (minimisePoint.isEmpty()) 
            return false;
        
        int itemPositionX = minimisePoint.get(0).x + 30;
        int itemPositionY = minimisePoint.get(0).y + 80;
        
        try {
            Robot robot = new Robot();
            
            robot.mouseMove(itemPositionX, itemPositionY);
            robot.delay(40);
            robot.mousePress(MouseEvent.BUTTON1_MASK);
            robot.delay(40);
            robot.mouseRelease(MouseEvent.BUTTON1_MASK);
            robot.delay(100);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return true;
    }
    
    /**
     * Closes the inspected item.
     */
    public static void closeItem() {
        if (isTesting)
            return;
        
        ScreenSearch.buildNewScreenshot();
        ArrayList<Point> itemClose = ScreenSearch.find(ScreenSearch.CLOSE_ITEM_IMAGE, null);
        
        if (itemClose.isEmpty())
            return;
        
        Point itemClosePoint = itemClose.get(0);
        
        int itemPositionX = itemClosePoint.x + 20;
        int itemPositionY = itemClosePoint.y + 8;
        
        while (EverQuest.isSpeaking) {
            try { Thread.sleep(20);} catch (Exception e) {}
        }
        
        EverQuest.isSpeaking = true;
        
        try {
            Robot robot = new Robot();
            
            robot.mouseMove(itemPositionX, itemPositionY);
            robot.delay(200);
            robot.mousePress(MouseEvent.BUTTON1_MASK);
            robot.delay(40);
            robot.mouseRelease(MouseEvent.BUTTON1_MASK);
            robot.delay(40);
           
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        EverQuest.isSpeaking = false;
    }
    
    public static boolean toggleInventory() {
        if (isTesting)
            return false;
        
        try {
            Robot robot = new Robot();
            
            robot.delay(40);
            robot.keyPress(KeyEvent.VK_I);
            robot.delay(40);
            robot.keyRelease(KeyEvent.VK_I);
            robot.delay(100);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return true;
    }
    
    public static void castSpell(int spellSlot) {
        if (isTesting) {
            System.out.println("Casting " + spellSlot);
            return;
        }
        
        sendToEQ("/cast " + spellSlot);
    }
    
    /**
     * Clicks the button located at x, y.
     * 
     * @param x x location of button
     * @param y y location of button
     */
    public static void clickButton(int x, int y) {
        try {
            Robot robot = new Robot();
            
            robot.delay(100);
            
            robot.mouseMove(x, y);
            
            robot.delay(200);
            
            robot.mousePress(MouseEvent.BUTTON1_MASK);
            
            robot.delay(100);
            
            robot.mouseRelease(MouseEvent.BUTTON1_MASK);
            
            robot.delay(100);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static ArrayList<String> getCurrentRaid() {
        ArrayList<String> raid = new ArrayList<>();
        
        try {
            File latestRaid = new File("C:/dump.txt");
            
            BufferedReader reader = new BufferedReader(new FileReader(latestRaid));
            
            String line;
            
            while ((line = reader.readLine()) != null) {
                String[] str = line.split("\t");
                
                String name = str[1];
                
                raid.add(name);
            }
            
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return raid;
    }
    
    public static boolean hasInspectedItemOpen() {
        try {
            ArrayList<Point> ps = ScreenSearch.find(ScreenSearch.MINIMISE_ITEM_IMAGE, null);
            
            return !ps.isEmpty();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    public static ArrayList<String> getClassesThatCanUse() {
        try {
            inspectItem();
            
            Thread.sleep(1000);
            
            ArrayList<String> array = ScreenSearch.findClasses();
            
            closeItem();
            
            return array;
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return new ArrayList<>();
    }
    
    public static void promote(String name) {
        System.out.println("Promote " + name);
        EverQuest.sendToEQ("/guildpromote " + name);
    }
    
    public static void demote(String name) {
        System.out.println("Demote " + name);
        EverQuest.sendToEQ("/guilddemote " + name);
    }
    
    public static void clickInFront() {
        clickButton(Toolkit.getDefaultToolkit().getScreenSize().width/2, Toolkit.getDefaultToolkit().getScreenSize().height/2);
    }
    
    public static void selectAPlayer() {
        try {
            Robot robot = new Robot();
            
            robot.keyPress(KeyEvent.VK_ESCAPE);
            robot.delay(40);
            robot.keyRelease(KeyEvent.VK_ESCAPE);
            robot.delay(200);
            
            clickInFront();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void givePlayerPP(long amount) {
        try {
            toggleInventory();
        
            ScreenSearch.buildNewScreenshot();
            Point ppPos = ScreenSearch.find(ScreenSearch.platinumCoin, null).get(0);
            clickButton(ppPos.x + 6, ppPos.y + 6);
            
            Robot robot = new Robot();
            
            // Delete all number of pp
            for (int i = 0; i < 10; i++) {
                robot.keyPress(KeyEvent.VK_BACK_SPACE);
                robot.delay(40);
                robot.keyRelease(KeyEvent.VK_BACK_SPACE);
                robot.delay(40);
            }
            
            // Write the number of pp to give
            String s = ""+amount;
            for (char c : s.toCharArray()) {
                int keyEvent = 0;
                if (c == '0') keyEvent = KeyEvent.VK_0;
                if (c == '1') keyEvent = KeyEvent.VK_1;
                if (c == '2') keyEvent = KeyEvent.VK_2;
                if (c == '3') keyEvent = KeyEvent.VK_3;
                if (c == '4') keyEvent = KeyEvent.VK_4;
                if (c == '5') keyEvent = KeyEvent.VK_5;
                if (c == '6') keyEvent = KeyEvent.VK_6;
                if (c == '7') keyEvent = KeyEvent.VK_7;
                if (c == '8') keyEvent = KeyEvent.VK_8;
                if (c == '9') keyEvent = KeyEvent.VK_9;
                
                robot.keyPress(keyEvent);
                robot.delay(100);
                robot.keyRelease(keyEvent);
                robot.delay(100);
            }
            
            // Press Enter to accept the quantity
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.delay(100);
            robot.keyRelease(KeyEvent.VK_ENTER);
            robot.delay(100);
            
            toggleInventory();
            
            // Start a new trade
            clickInFront();
            
            // Accept the trade
            Thread.sleep(2000);
            ScreenSearch.buildNewScreenshot();
            Point tradeAcceptPos = ScreenSearch.find(ScreenSearch.tradeAccept, null).get(0);
            
            if (tradeAcceptPos != null) {
                // Found a trade window, hit accept
                clickButton(tradeAcceptPos.x, tradeAcceptPos.y);
            }
            else {
                // No trade window found. Cancel giving. Give a warning
                sendToEQ("/say I guess you didn't want your money. Trade Cancelled.");
                
                // Put the pp back into raidbot
                toggleInventory();
                Thread.sleep(200);
                clickInFront();
                Thread.sleep(200);
                toggleInventory();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static String[] getTradeInfo() {
        ScreenSearch.buildNewScreenshot();
        Point tradeWindow = ScreenSearch.find(ScreenSearch.tradeWindow, null).get(0);
        
        ScreenSearch.buildRedTextScreenshot();
        String name = ScreenSearch.findPattern(new Rectangle(tradeWindow.x - 75, tradeWindow.y + 16, 84, 20));
        
        ScreenSearch.buildTextScreenshot();
        String amount = ScreenSearch.findNumber(new Rectangle(tradeWindow.x - 62, tradeWindow.y + 216, 64, 20));
        
        return new String[] { name, amount };
    }
    
    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void deposit(String name, long amount) {
        Account account = new Account(name);
        account.deposit(name, amount);
    }
    
    public static void withdraw(String name, long amount) {
        Account account = new Account(name);
        
        if (account.withdraw(name, amount)) {
            givePlayerPP(amount);
        }
    }
    
    public static void guildDump() {
        guildDump(null);
    }
    
    public static void guildDump(File path) {
        if (path == null)
            path = new File("dump.txt");
        
        sendToEQ("/outputfile guild " + path.getAbsolutePath());
    }
    
    public static void raidDump() {
        raidDump(null);
    }
    
    public static void raidDump(File path) {
        if (path == null)
            path = new File("dump.txt");
        
        sendToEQ("/outputfile raid " + path.getAbsolutePath());
    }
    
    public static void resetInputBar() {
        try {
            Robot robot = new Robot();
            
            robot.keyPress(KeyEvent.VK_SLASH);
            robot.delay(100);
            robot.keyRelease(KeyEvent.VK_SLASH);
            robot.delay(100);
            
            robot.keyPress(KeyEvent.VK_BACK_SPACE);
            robot.delay(100);
            robot.keyRelease(KeyEvent.VK_BACK_SPACE);
            robot.delay(100);
            
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.delay(100);
            robot.keyRelease(KeyEvent.VK_ENTER);
            robot.delay(100);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
   
}
