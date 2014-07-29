package deprecated;

import bot.Auctions;
import bot.DiceRolls;
import util.Data;
import database.Table;
import ui.DiceMonitor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;
import util.EverQuest;
import static util.EverQuest.sendToEQ;
import util.SQL;

/**
 * @deprecated Deprecated by menu.Session 9th June 2014 - Aaron
 * 
 * @author Aaron
 */
public class MainMenu {
    private final static ArrayList<MainMenu> sessions = new ArrayList<>();
     
    public final static int SILENCED =      0x0000;
    public final static int USER =          0x000F;
    public final static int OFFICER =       0x00FF;
    public final static int SENIOR =        0x0FFF;
    public final static int SUPER_USER =    0xFFFF;
    
    public final static int DEFAULT = USER;
    
    private final Stack<String> stack = new Stack<>();
    private final ArrayList<String> options = new ArrayList<>();
    private final String user;
    private final Stack<String> params = new Stack<>();
    private final SQL sql;
    
    public static void reset() {
        for (MainMenu session : sessions) {
            try {
                session.sql.closeConnection();
                session.stack.clear();
            } catch (Exception e) {
                System.out.println("MainMenu reset failed: " + e);
            }
        }
        
        sessions.clear();
    }
    
    public static boolean hasPriveledges(String name, int priveledges) {
        int rank = getPriveledges(SQL.getAlias(name));
        
        return (rank & priveledges) == priveledges;
    }
    public static int getPriveledges(String name) {
        String strRank = Data.users.where("Name", SQL.getAlias(name)).select("Rank");
        
        int rank = DEFAULT;
        if (!strRank.isEmpty()) rank = Integer.parseInt(strRank);
        
        return rank;
    }
    
    public static MainMenu getSession(String name) {
        for (MainMenu s : sessions) {
            if (s.getName().equalsIgnoreCase(name))
                return s;
        }
        
        MainMenu session = new MainMenu(name);
        sessions.add(session);
        
        return session;
    }
    public static void closeSession(String name) {
        MainMenu session = null;
        
        for (MainMenu s : sessions) {
            if (s.getName().equalsIgnoreCase(name)) {
                session = s;
                break;
            }
        }
        
        if (session != null) 
            sessions.remove(session);
    }
    
    private MainMenu(String name) {
        user = name;
        sql = new SQL();
        
        for (String[] entry : Data.preferences.where("Name", user).getEntries()) {
            String option = entry[1];
            String value = entry[2];
            
            if (option.equals("fastmenu")) isFastMenu = Boolean.parseBoolean(value);
        }
    }
    
    private void savePreferences() {
        Data.preferences.addEntry(user, "fastmenu", ""+isFastMenu);
    }
    
    public String getName() {
        return user;
    }
    
    public static String ON = " on";
    public static String OFF = " off";
    
    public static String MAIN_MENU = "MAIN MENU";
    public static String DKP_MENU = "DKP Menu";
    public static String RAID_LOOT_MENU = "Raid Loot";
    public static String USER_MANAGEMENT_MENU = "Manage User Access";
    public static String RAIDBOT_SETTINGS_MENU = "Raidbot Control";
    
    public static String SET_USER_PRIVELEDGE = "Set Player Priveledges";
    public static String SILENCE_USER = "Ignore Player";
    
    public static String CHECK_DKP = "Check Player Stats";
    public static String DKP_SPENDABLE = "DKP (Spendable)";
    public static String RAID_ATTENDANCE = "Raid Attendance (30 Days)";
    public static String RESET_CACHE = "Validate Cache";
    public static String BUILD_CACHE = "Build DKP Cache for super-fast access";
    public static String AUCTION_ITEM = "Auction an item";
    public static String AUCTION_MULTIPLE = "Auction multiple items";
    public static String AUCTION_RANDOM_NUMBERS = "Auction an item using random rolls";
    public static String AUCTION_RANDOM_NUMBERS_MULTIPLE = "Auction multiple items using random rolls";
    public static String GET_NEXT_WINNER = "Declare the next winner of current auction";
    
    public static String SET_CHANNEL = "Set Auction Channel";
    public static String LIST_WINNERS = "List raid loot winners";
    public static String CLEAR_WINNERS = "Clear all winners";
    public static String ADD_WINNER = "Manually add a winner";
    public static String DECLARE_ANOTHER_WINNER = "Declare another winner for the last auctioned item";
    public static String TAKE_DKP_GUILD = "Take DKP (Guild)";
    public static String TAKE_DKP_RAID = "Take DKP (Raid)";
    public static String RECONCILE_RAIDERS = "Check raid attendance and promote/demote raiders";
    
    public static String JOIN_GROUP = "Join my group (Send invite first)";
    public static String JOIN_RAID = "Join my raid (Send raidinvite first)";
    public static String DROP_RAID = "Leave raid";
    public static String TOGGLE_TEST_MODE = "Turn test mode";
    public static String OPTIONS_MENU = "My Preferences";
    
    public static String RESET_RAIDBOT = "Full Reset";
    public static String BUFFBOT_MENU = "Buffbot Control";
    public static String REMOTE_EXIT = "Make a buffbot exit";
    public static String LOAD_SCRIPT = "Load new buffbot script";
    public static String RELOAD_SCRIPT = "Reload buffbot script";
    
    public static String MANAGE_ALIAS_MENU = "Manage Player Aliases";
    public static String ADD_ALIAS = "Add a player alias";
    public static String REMOVE_ALIAS = "Removes an alias from a main";
    public static String CHANGE_MAIN = "Change a main (and included aliases)";
    
    public static String TOGGLE_FAST_MENU = "Turn fast menu";
    
    public static String TEST_CASE = "TEST";
    
    public String menu = MAIN_MENU;
    public boolean isACommand = false;
    private boolean sudoSU = false;
    private boolean isFastMenu = true;
    
    /**
     * Creates the menu for display
     */
    private void speakMenu() {
        options.clear();
        speakHeader(menu);
        
        if (menu.equals(MAIN_MENU)) {
            if (hasPriveledges(USER)) addOptions(DKP_MENU, OPTIONS_MENU);
            if (hasPriveledges(OFFICER)) addOptions(RAID_LOOT_MENU, USER_MANAGEMENT_MENU);
            if (hasPriveledges(SENIOR)) addOptions(RAIDBOT_SETTINGS_MENU);
        }
        
        if (menu.equals(DKP_MENU)) {
            if (hasPriveledges(USER)) addOptions(DKP_SPENDABLE, RAID_ATTENDANCE);
            if (hasPriveledges(OFFICER)) addOptions(CHECK_DKP, TAKE_DKP_GUILD, TAKE_DKP_RAID);
     //       if (hasPriveledges(SUPER_USER)) addOptions(BUILD_CACHE,RESET_CACHE);
        }
        
        if (menu.equals(RAID_LOOT_MENU)) {
            if (hasPriveledges(OFFICER)) addOptions(AUCTION_ITEM, AUCTION_MULTIPLE, DECLARE_ANOTHER_WINNER);
            if (hasPriveledges(OFFICER)) addOptions(AUCTION_RANDOM_NUMBERS, AUCTION_RANDOM_NUMBERS_MULTIPLE);
        }
        
        if (menu.equals(USER_MANAGEMENT_MENU)) {
            if (hasPriveledges(OFFICER)) addOptions(SET_USER_PRIVELEDGE, SILENCE_USER);
            if (hasPriveledges(OFFICER)) addOptions(MANAGE_ALIAS_MENU, RECONCILE_RAIDERS);
        }
        
        if (menu.equals(MANAGE_ALIAS_MENU)) {
            if (hasPriveledges(OFFICER)) addOptions(ADD_ALIAS, REMOVE_ALIAS, CHANGE_MAIN);
        }
        
        if (menu.equals(RAIDBOT_SETTINGS_MENU)) {
            if (hasPriveledges(SENIOR)) addOptions(RESET_RAIDBOT, JOIN_GROUP, JOIN_RAID, DROP_RAID);
            if (hasPriveledges(SUPER_USER)) addOptions(SET_CHANNEL);
        }
        
        if (menu.equals(BUFFBOT_MENU)) {
            if (hasPriveledges(SENIOR)) addOptions(LOAD_SCRIPT, RELOAD_SCRIPT, REMOTE_EXIT);
        }
        
        if (menu.equals(OPTIONS_MENU)) {
            if (hasPriveledges(USER)) {
                if (isFastMenu) addOptions(TOGGLE_FAST_MENU + OFF);
                else addOptions(TOGGLE_FAST_MENU + ON);
            }
        }
        
        speakOptions();
    }
    
    /**
     * Deals with user input that they send to Raidbot.
     * 
     * @param s User input
     * @return Response to user input
     */
    public String parse(String s) {
        // Return the user to the main menu
        if (s.equalsIgnoreCase("m") || s.toLowerCase().contains("menu")) {
            menu = MAIN_MENU;
            stack.clear();
            options.clear();
            params.clear();
            speakMenu();
            isACommand = false;
            return "";
        }
        
        // If the user has previously selected a command, reroute their input through the command system
        if (isACommand) {
            stack.add(s);
            
            if (params.isEmpty())
                executeCommand();
            else 
                askForParam();
            
            return "";
        }

        // Grant the user temporary access to everything if they use "adminacc"
        if (s.toLowerCase().contains("adminacc")) {
            sudoSU = !sudoSU;
            speak("You are the Batman");
            return "";
        }
        
        int option;
        if ((option = getInt(s)) != 0) {
            String command = options.get(option-1);
            menu = command;
            isACommand = true;
            params.clear();
            
            if (command.equals(RESET_RAIDBOT)) {
                DiceMonitor.resetRaidbot(user);
                return mainMenu();
            }
            
            if (command.equals(CHECK_DKP)) return askForParams("Which Player?");
            if (command.equals(ADD_ALIAS)) return askForParams("What is their main?", "What is their box?");
            if (command.equals(REMOVE_ALIAS)) return askForParams("What alias to remove?");
            if (command.equals(CHANGE_MAIN)) return askForParams("What is their new main?", "What is their old name?");
            if (command.equals(SILENCE_USER)) return askForParams("Which Player?");
            if (command.equals(AUCTION_ITEM)) return askForParams("Link the item:");
            if (command.equals(AUCTION_MULTIPLE)) return askForParams("Link the item:", "How many?");
            if (command.equals(AUCTION_RANDOM_NUMBERS)) return askForParams("Link the item:");
            if (command.equals(AUCTION_RANDOM_NUMBERS_MULTIPLE)) return askForParams("Link the item:", "How many?");
            if (command.equals(SET_CHANNEL)) return askForParams("Set channel to what? Example: \"/rs\"");
                
            if (command.equals(ADD_WINNER)) return askForParams("Link the item:", "What was the DKP cost?", "Which Player?");
            
            if (command.equals(SET_USER_PRIVELEDGE)) {
                String ranks = "(";
                if (hasPriveledges(USER)) ranks += "1. User";
                if (hasPriveledges(OFFICER)) ranks += ", 2. Officer";
                if (hasPriveledges(SENIOR)) ranks += ", 3. Senior Officer";
                if (hasPriveledges(SUPER_USER)) ranks += ", 4. Super User";
                ranks += ")";
                
                return askForParams("Set what rank? " + ranks, "Which Player?");
            }
            if (command.equals(DKP_SPENDABLE)) {
                speak("Spendable DKP: " + sql.getDKP(user));
                return mainMenu();
            }
            if (command.equals(RAID_ATTENDANCE)) {
                int ra = sql.getRA(user, SQL.DAYS_30);
                speak("Raid Attendance (30 Days): " + ra + "%");
                return mainMenu();
            }
            if (command.equals(LIST_WINNERS)) {
                Auctions.speakWinners(user);
                return mainMenu();
            }
            if (command.equals(CLEAR_WINNERS)) {
                Auctions.clearWinners();
                return mainMenu();
            }
            if (command.equals(JOIN_GROUP)) {
                EverQuest.sendToEQ("/invite " + user);
                return mainMenu();
            }
            if (command.equals(GET_NEXT_WINNER)) {
                Table winner = Auctions.getNextWinner();
                String name = winner.select("Name");
                String bid = winner.select("Bid");
                String ra = winner.select("RA");
                
                EverQuest.sendToEQ(DiceMonitor.CHANNEL + "Gratz " + name + " on " + 
                        Auctions.nameOfItem + " for " + bid + " DKP, " + ra + "% RA!");
            }
            if (command.equals(TOGGLE_FAST_MENU + ON)) {
                isFastMenu = true;
                savePreferences();
                return mainMenu();
            }
            if (command.equals(TOGGLE_FAST_MENU + OFF)) {
                isFastMenu = false;
                savePreferences();
                return mainMenu();
            }
            if (command.equals(TAKE_DKP_GUILD)) return askForParams("What type? (1. On-Time, 2. Hourly, 3. Raid End, 4. Progression On-Time, 5. Progression Hourly, 6. Progression Raid End)");
            if (command.equals(TAKE_DKP_RAID)) return askForParams("What type? (1. On-Time, 2. Hourly, 3. Raid End, 4. Progression On-Time, 5. Progression Hourly, 6. Progression Raid End)");
            if (command.equals(JOIN_RAID)) {
                EverQuest.sendToEQ("/raidaccept");
                return mainMenu();
            }
            if (command.equals(DROP_RAID)) {
                EverQuest.sendToEQ("/raiddisband");
                return mainMenu();
            }
            if (command.equals(RESET_CACHE)) {
                sql.forceValidateCache(user);
                return mainMenu();
            }
            if (command.equals(BUILD_CACHE)) {
                speak("Building cache, Please wait...");
                sql.buildCache();
                speak("Cache loaded.");
                return mainMenu();
            }
            //if (command.equals(TEST_CASE)) {
               // speak("This is a test.");
            //    int dkpTest = 0;       
            //    dkpTest = sql.testCase(dkpTest);
            //    speakInt(dkpTest);
             //   return mainMenu();
           // }
            
            if (command.equals(DECLARE_ANOTHER_WINNER)) {
                Table winner = Auctions.getNextWinner();
                
                if (winner == null) {
                    sendToEQ(DiceMonitor.CHANNEL + "No Bids.");
                } else {
                    String name = winner.select("Name");
                    String bid = winner.select("Bid");
                    String ra = winner.select("RA");

                    // Declare the winner
                    sendToEQ(DiceMonitor.CHANNEL + "Gratz " + name + " on " + Auctions.nameOfItem + " for " + bid + " DKP, " + ra + "% RA!");

                    // Add the won item to the database
                    if (!EverQuest.isTesting)
                        sql.addItem(name, Auctions.nameOfItem, Integer.parseInt(bid));

                    // Remove player from the database
                    Auctions.auctions.removeEntry(name);
                }
            }
            if (command.equals(RECONCILE_RAIDERS)) {
                try {
                    EverQuest.guildDump();
                    
                    Thread.sleep(500);
                    
                    BufferedReader reader = new BufferedReader(new FileReader(new File("dump.txt")));
                    String line;
                    
                    ArrayList<String> duplicate = new ArrayList<>();
                    while ((line = reader.readLine()) != null) {
                        String[] str = line.split("\t");
                        
                        String name = str[0];
                        String level = str[1];
                        String pClass = str[2];
                        String rank = str[3];
                        String altFlag = str[4];
                        
                        // Ignore if dealt with already
                        if (duplicate.contains(name)) continue;
                        
                        // Ignore any Alt Flagged
                        if (altFlag.equalsIgnoreCase("a")) continue;
                        
                        // Only concern ourselves with rank of Member or Raider
                        if (rank.equalsIgnoreCase("member")) {
                            int ra = sql.getRA(name, SQL.DAYS_30);
                            
                            // Promote if RA reaches above 15%
                            if (ra >= 15) {
                                EverQuest.promote(name);
                            }
                        }
                        if (rank.equalsIgnoreCase("raider")) {
                            int ra = sql.getRA(name, SQL.DAYS_30);
                            
                            // Demote if it drops below 13% (Ignore 0% as it could be a database error)
                            if (ra != 0 && ra < 13) {
                                EverQuest.demote(name);
                            }
                        }
                        
                        duplicate.add(name);
                        
                    }
                } catch (IOException | InterruptedException e) {
                    System.out.println("Reconcile raiders failed: " + e);
                }
                return mainMenu();
            }
            
            isACommand = false;
        }
        
        speakMenu();
        
        return s;
    }
    
    private void executeCommand() {
        if (menu.equals(ADD_ALIAS)) {
            String main = stack.pop();
            String alt = stack.pop();
            
            Data.alias.addEntry(alt, main);
            speak(alt + " is now an alias of " + main);
        }
        if (menu.equals(REMOVE_ALIAS)) {
            String name = stack.pop();
            
            if (!Data.alias.where("Alias", name).isEmpty()) {
                Data.alias.removeEntry(name);
            }
        }
        if (menu.equals(CHANGE_MAIN)) {
            String toName = stack.pop();
            String fromName = stack.pop();
            
            for (int i = 0; i < Data.alias.size(); i++) {
                String[] entry = Data.alias.getEntry(i);

                if (entry[1].equalsIgnoreCase(fromName)) {
                    Data.alias.updateEntry("Name", fromName, "Name", toName);
                }
            }
        }
        if (menu.equals(CHECK_DKP)) {
            String name = stack.pop();
            
            int dkp = sql.getDKP(name);
            int ra = sql.getRA(name, SQL.DAYS_30);
            
            speak("Player Name: " + name);
            speak("Spendable DKP: " + dkp);
            speak("Raid Attendance: " + ra + "%");
        }
        if (menu.equals(ADD_WINNER)) {
            String link = stack.pop();
            String dkp = stack.pop();
            String name = stack.pop();
            
            Auctions.addWinner(name, dkp, link);
            
            speak("Added: " + name + " paid " + dkp + " DKP for " + link);
        }
        if (menu.equals(SET_USER_PRIVELEDGE)) {
            String rank = stack.pop();
            String name = stack.pop();
            
            int priv = DEFAULT;
            if (rank.equalsIgnoreCase("1")) priv = USER;
            if (rank.equalsIgnoreCase("2")) priv = OFFICER;
            if (rank.equalsIgnoreCase("3")) priv = SENIOR;
            if (rank.equalsIgnoreCase("4")) priv = SUPER_USER;
            
            if (setPriveledges(name, priv)) {
                String s = "";
                if (priv == USER) s += "User";
                if (priv == OFFICER) s += "Officer";
                if (priv == SENIOR) s += "Senior Officer";
                if (priv == SUPER_USER) s += "Super User";
                speak("Done. I have made " + name + " a " + s + ".");
            }
        }
        if (menu.equals(SILENCE_USER)) {
            String name = stack.pop();
            
            if (setPriveledges(name, SILENCED)) {
                speak("Done. I have silenced " + name);
            }
        }
        if (menu.equals(AUCTION_ITEM)) {
            String link = stack.pop();
            if (link.equals("m") || link.equals("menu") || DiceMonitor.isNumeric(link)) {
                EverQuest.sendTell(user, "I'm not sure you want to actually auction that. Cancelled Auction.");
            }
            
            Auctions.startAuction(link, 1);
            
            
        }
        if (menu.equals(AUCTION_MULTIPLE)) {
            String link = stack.pop();
            if (link.equals("m") || link.equals("menu") || DiceMonitor.isNumeric(link)) {
                EverQuest.sendTell(user, "I'm not sure you want to actually auction that. Cancelled Auction.");
            }
            
            String num = stack.pop();
            if (!DiceMonitor.isNumeric(num)) {
                EverQuest.sendTell(user, "Umm, you know... I didn't understand that number. Cancelled Auction.");
            }
            
            Auctions.startAuction(link, Integer.parseInt(num));
        }
        if (menu.equals(AUCTION_RANDOM_NUMBERS)) {
            String link = stack.pop();
            if (link.equals("m") || link.equals("menu") || DiceMonitor.isNumeric(link)) {
                EverQuest.sendTell(user, "I'm not sure you want to actually auction that. Cancelled Auction.");
            }
            
            DiceRolls.startAuction(link, 1);
        }
        if (menu.equals(AUCTION_RANDOM_NUMBERS_MULTIPLE)) {
            String link = stack.pop();
            if (link.equals("m") || link.equals("menu") || DiceMonitor.isNumeric(link)) {
                EverQuest.sendTell(user, "I'm not sure you want to actually auction that. Cancelled Auction.");
            }
            
            String num = stack.pop();
            if (!DiceMonitor.isNumeric(num)) {
                EverQuest.sendTell(user, "Umm, you know... I didn't understand that number. Cancelled Auction.");
            }
            
            DiceRolls.startAuction(link, Integer.parseInt(num));
        }
        if (menu.equals(SET_CHANNEL)) {
            String channel = stack.pop();
            
            DiceMonitor.CHANNEL = channel + " ";
        }
        if (menu.equals(TAKE_DKP_GUILD)) {
            String type = stack.pop();
            
            EverQuest.guildDump();
            EverQuest.sendToEQ("/gu <Taking DKP>");

            int event;
            if (type.equalsIgnoreCase("1")) {
                Auctions.CURRENT_HOUR = 0;
                event = SQL.EVENT_ON_TIME;
            }
            else if (type.equalsIgnoreCase("2")) {
                event = SQL.EVENT_HOUR_1 - Auctions.CURRENT_HOUR;
                Auctions.CURRENT_HOUR++;
                if (Auctions.CURRENT_HOUR > Auctions.MAX_HOURS)
                    Auctions.CURRENT_HOUR = Auctions.MAX_HOURS;
            }
            else if (type.equalsIgnoreCase("3")) {
                Auctions.CURRENT_HOUR = 0;
                event = SQL.EVENT_RAID_ENDED;
            }
            else if (type.equalsIgnoreCase("4")) {
                Auctions.CURRENT_HOUR = 0;
                event = SQL.PROG_EVENT_ON_TIME;
            }
            else if (type.equalsIgnoreCase("5")) {
                event = SQL.PROG_EVENT_HOUR_1 - Auctions.CURRENT_HOUR;
                Auctions.CURRENT_HOUR++;
                if (Auctions.CURRENT_HOUR > Auctions.MAX_HOURS)
                    Auctions.CURRENT_HOUR = Auctions.MAX_HOURS;
            }
            else if (type.equalsIgnoreCase("6")) {
                Auctions.CURRENT_HOUR = 0;
                event = SQL.PROG_EVENT_RAID_ENDED;
            }
            else {
                EverQuest.sendToEQ("Invalid Option. Cancelling Take DKP.");
                mainMenu();
                return;
            }
            
            ArrayList<String> names = new ArrayList<>();
            try {
                BufferedReader reader = new BufferedReader(new FileReader(new File("dump.txt")));
                
                String line;

                ArrayList<String> lines = new ArrayList<>();
                ArrayList<String> zones = new ArrayList<>();
                ArrayList<Integer> count = new ArrayList<>();

                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }

                for (String s : lines) {
                    String[] str = s.split("\t");
                    String zone = str[6];

                    if (zones.contains(zone)) {
                        int ind = zones.indexOf(zone);
                        count.set(ind, count.get(ind) + 1);
                    } else {
                        zones.add(zone);
                        count.add(1);
                    }
                }

                int highestCount = 0;
                String raidZone = "";

                for (int i = 0; i < zones.size(); i++) {
                    int c = count.get(i);
                    if (c > highestCount) {
                        highestCount = c;
                        raidZone = zones.get(i);
                    }
                }

                for (String s : lines) {
                    String[] str = s.split("\t");
                    String name = str[0];
                    String zone = str[6];

                    if (zone.equals(raidZone)) {
                        String alias = SQL.getAlias(name);
                        if (!names.contains(alias))
                            names.add(alias);
                    }
                }
            } catch (IOException e) {
                System.out.println("Take DKP failed: " + e);
            }
            sql.addRaid(event, names);
            
            if (!SQL.notRegistered.isEmpty()) {
                String s = "The following players are not registered for DKP: ";
                
                for (int i = 0; i < SQL.notRegistered.size(); i+=20) {
                    for (int j = i; j < i+20 && j < SQL.notRegistered.size(); j++) {
                        s += SQL.notRegistered.get(j);
                        
                        if (j != SQL.notRegistered.size()-1)
                            s += ", ";
                    }
                    EverQuest.sendToEQ("/gu " + s);
                    s = "";
                }
            }
            
            speak("Raid Added with " + names.size() + " raiders!");
            EverQuest.sendToEQ("/gu <DKP Taken: Added " + names.size() + " raiders>");
            
            mainMenu();
        }
        
        
    
        if (menu.equals(TAKE_DKP_RAID)) {
            String type = stack.pop();
            
            EverQuest.raidDump();
            EverQuest.sendToEQ("/gu <Taking DKP>");

            int event;
            if (type.equalsIgnoreCase("1")) {
                Auctions.CURRENT_HOUR = 0;
                event = SQL.EVENT_ON_TIME;
            }
            else if (type.equalsIgnoreCase("2")) {
                event = SQL.EVENT_HOUR_1 - Auctions.CURRENT_HOUR;
                Auctions.CURRENT_HOUR++;
                if (Auctions.CURRENT_HOUR > Auctions.MAX_HOURS)
                    Auctions.CURRENT_HOUR = Auctions.MAX_HOURS;
            }
            else if (type.equalsIgnoreCase("3")) {
                Auctions.CURRENT_HOUR = 0;
                event = SQL.EVENT_RAID_ENDED;
            }
            else if (type.equalsIgnoreCase("4")) {
                Auctions.CURRENT_HOUR = 0;
                event = SQL.PROG_EVENT_ON_TIME;
            }
            else if (type.equalsIgnoreCase("5")) {
                event = SQL.PROG_EVENT_HOUR_1 - Auctions.CURRENT_HOUR;
                Auctions.CURRENT_HOUR++;
                if (Auctions.CURRENT_HOUR > Auctions.MAX_HOURS)
                    Auctions.CURRENT_HOUR = Auctions.MAX_HOURS;
            }
            else if (type.equalsIgnoreCase("6")) {
                Auctions.CURRENT_HOUR = 0;
                event = SQL.PROG_EVENT_RAID_ENDED;
            }
            else {
                EverQuest.sendToEQ("Invalid Option. Cancelling Take DKP.");
                mainMenu();
                return;
            }
            
            ArrayList<String> raidNames = new ArrayList<>();
            try {
                BufferedReader reader = new BufferedReader(new FileReader(new File("dump.txt")));
                
                String line;

                ArrayList<String> raidLines = new ArrayList<>();
                ArrayList<String> raidZones = new ArrayList<>();
                ArrayList<Integer> raidCount = new ArrayList<>();

                while ((line = reader.readLine()) != null) {
                    raidLines.add(line);
                }

                for (String s : raidLines) {
                    String[] str = s.split("\t");
                    String playerChar = str[2];

                    if (raidZones.contains(playerChar)) {
                        int ind = raidZones.indexOf(playerChar);
                        raidCount.set(ind, raidCount.get(ind) + 1);
                    } else {
                        raidZones.add(playerChar);
                        raidCount.add(1);
                    }
                }
                    for (String s : raidLines) {
                        String[] str = s.split("\t");
                        String name = str[1];
                        String alias = SQL.getAlias(name);
                        if (!raidNames.contains(alias)){
                            raidNames.add(alias);
                        }
                    
                }
            } catch (IOException e) {
                System.out.println("Take DKP failed: " + e);
            }
            sql.addRaid(event, raidNames);
            
            if (!SQL.notRegistered.isEmpty()) {
                String s = "The following players are not registered for DKP: ";
                
                for (int i = 0; i < SQL.notRegistered.size(); i+=20) {
                    for (int j = i; j < i+20 && j < SQL.notRegistered.size(); j++) {
                        s += SQL.notRegistered.get(j);
                        
                        if (j != SQL.notRegistered.size()-1)
                            s += ", ";
                    }
                    EverQuest.sendToEQ("/gu " + s);
                    s = "";
                }
            }
            
           
            
            speak("Raid Added with " + raidNames.size() + " raiders!");
            EverQuest.sendToEQ("/gu <DKP Taken: Added " + raidNames.size() + " raiders>");
        }
        
        mainMenu();
    }
    
    private String mainMenu() {
        menu = MAIN_MENU;
        stack.clear();
        params.clear();
        isACommand = false;
        return "";
    }
    
    private void askForParam() {
        speak(params.pop());
    }
    
    private String askForParams(String... params) {
        addParams(params);
        askForParam();
        return "";
    }
    
    private void addOptions(String ... s) {
        options.addAll(Arrays.asList(s));
    }
    
    private void addParams(String ... s) {
        params.addAll(Arrays.asList(s));
    }
    
    private void speakHeader(String ... headers) {
        if (!isFastMenu) {
            String line = "";
            for (int i = 0; i < headers.length-1; i++) {
                line += "| " + headers[i] + "\t";
            }
            line += "| " + headers[headers.length-1] + " |";

            String dashes = "";
            for (int i = 0; i < line.length()*1.5; i++) {
                dashes += "-";
            }
            
            int dkp = sql.getDKP(user);
            int ra = sql.getRA(user, SQL.DAYS_30);

            speak(dashes);
            speak(line);
            if (headers[0].equals(MAIN_MENU)) {
                String dkpString = "| DKP: " + dkp;
                String raString = "| RA : " + ra + "%";
                
                speak(dkpString);
                speak(raString);
            }
            speak(dashes);
        }
    }
    
    private void speakOptions() {
        speakOptions(options.toArray(new String[0]));
    }
    
    private void speakOptions(String ... options) {
        if (isFastMenu) {
            String s = menu;
            for (int i = 0; i < options.length; i++) 
                s += "\t| " + (i+1) + ". " + options[i];
            speak(s);
        } else {
            for (int i = 0; i < options.length; i++) {
                speak("| " + (i+1) + ". " + options[i]);
            }
        }
    }
    
    private void speak(String s) {
        EverQuest.sendToEQ("/Tell " + user + " " + s);
    }
    private void speakInt(int s) {
        EverQuest.sendToEQ("/Tell " + user + " " + s);
    }
    private int getInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
        }
        return 0;
    }
    
    private boolean hasPriveledges(int priveledges) {
        if (sudoSU) return true;
        
        return hasPriveledges(user, priveledges);
    }
    private int getUserPriveledges() {
        if (sudoSU) return SUPER_USER;
        
        return getPriveledges(user);
    }
    
    private boolean setPriveledges(String name, int priveledges) {
        if (!sudoSU) {
            if (getPriveledges(name) >= getUserPriveledges()) {
                speak("You can only alter the access level of players who do not out-rank you.");
                return false;
            }

            if (getUserPriveledges() < priveledges) {
                speak("You can not promote player's access level to above your own");
                return false;
            }
        }
        
        
        if (Data.users.where("Name", name).isEmpty()) {
            Data.users.addEntry(name, ""+priveledges);
        } else {
            Data.users.updateEntry("Name", name, "Rank", ""+priveledges);
        }
        
        return true;
    }
}
