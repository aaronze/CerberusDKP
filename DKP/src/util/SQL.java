package util;
 
import database.Cache;
import database.Database;
import database.Table;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import test.TestCases;
 
/**
 * DbManager hosts all connections to the SQL Database hosted online
 */
public class SQL {
    /**
     * Defines common descriptions to be used globally
     */
    public final static String BARD = "Bard";
    public final static String BEASTLORD = "Beastlord";
    public final static String BERZERKER = "Berserker";
    public final static String CLERIC = "Cleric";
    public final static String DRUID = "Druid";
    public final static String ENCHANTER = "Enchanter";
    public final static String MAGICIAN = "Magician";
    public final static String MONK = "Monk";
    public final static String NECROMANCER = "Necromancer";
    public final static String PALADIN = "Paladin";
    public final static String RANGER = "Ranger";
    public final static String ROGUE = "Rogue";
    public final static String SHADOWKNIGHT = "Shadow Knight";
    public final static String SHAMAN = "Shaman";
    public final static String WARRIOR = "Warrior";
    public final static String WIZARD = "Wizard";
    
    public final static long DAYS_30 = 2592000; // The amount of seconds in 30 days
    public final static long DAYS_60 = 2592000 * 2; // The amount of seconds in 60 days
    public final static long DAYS_90 = 2592000 * 3; // The amount of seconds in 90 days
    
    /**
     * On time event and hourly events for Take DKP
     */
    public final static int EVENT_ON_TIME = 9;
    public final static int EVENT_HOUR_1 = 35;
    public final static int EVENT_HOUR_2 = 34;
    public final static int EVENT_HOUR_3 = 33;
    public final static int EVENT_HOUR_4 = 32;
    public final static int EVENT_HOUR_5 = 31;
    public final static int EVENT_RAID_ENDED = 15;
    public final static int PROG_EVENT_ON_TIME = 25;
    public final static int PROG_EVENT_HOUR_1 = 29;
    public final static int PROG_EVENT_HOUR_2 = 28;
    public final static int PROG_EVENT_HOUR_3 = 27;
    public final static int PROG_EVENT_HOUR_4 = 26;
    public final static int PROG_EVENT_RAID_ENDED = 30;
    public final static int EVENT_TEST = 1;
   
    /**
     * Contains a list of players that Take DKP failed to include (Most likely they are not registered).
     */
    public static ArrayList<String> notRegistered = new ArrayList<>();
    
    /**
     * Currently used connections that need to be closed at shutdown for proper cleanup.
     */
    public static ArrayList<SQL> openConnections = new ArrayList<>();
    
    /**
     * Resets everything SQL related.
     */
    public static void reset() {
        try {
            for (SQL sql : openConnections) {
                sql.closeConnection();
            }
            openConnections.clear();
            notRegistered.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Connection used by local instance.
     */
    private Connection connection;
    
    /**
     * Attempts to rebuild the cache using the current raiders.
     */
    public void buildCache() {
        EverQuest.guildDump();
        
        ArrayList<String> names = new ArrayList<>();
        try {
            Thread.sleep(1000);
            
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
                    names.add(SQL.getAlias(name));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        for (String name : names) {
            try {
                System.out.println(name + ": " + getID(name) + ": " + getDKP(name) + ", " + getRA(name, DAYS_30));
            } catch (Exception e) {
                System.out.println(name + " failed.");
            }
        }
    }
    
    public void forceValidateCache(final String name) {
        // Start in a new thread so it doesn't interfere with anything
        Thread workerThread = new Thread() {
            @Override
            public void run() {
                EverQuest.sendTell(name, "Validating the Cache, feel free to do other stuff... I'll send you a tell when it's finished.");  
                
                // Get everything from the cache and validate it
                revalidateCacheTable(name, Cache.getTable());
                
                EverQuest.sendTell(name, "Finally, I've finished sorting through all that crap. Thanks for that by the way.");
            }
        };
        
        // Activate the thread, and stand far, far back. I ain't touching that.
        workerThread.start();
    }
    //Does not currently work -- work in progress
    private void deleteCache(final String playerName, final Table table) {
        // Get everything from the cache
        ArrayList<String[]> entries = table.getEntries();
        
        // Delete cache
        table.removeAllEntries();
    }
    private void revalidateCacheTable(final String playerName, final Table table) {
        // Get everything from the cache
        ArrayList<String[]> entries = table.getEntries();
        
        // Delete cache
        table.removeAllEntries();
        
        long validationPeriod = Cache.WEEK*52;
        
        for (String[] entry : entries) {
            String storeName = entry[0];
            
            if (storeName.length() >= 2) {
                String command = storeName.substring(0, 2);
                String calcValue = "";
                
                if (command.equalsIgnoreCase("ID")) {
                    String name = storeName.substring(2);
                //    Cache.add("ID"+name, ""+getID(name), validationPeriod);
                    calcValue = ""+getID(name);
                }
                if (command.equalsIgnoreCase("NA")) {
                    String id = storeName.substring(4);
                 //   Cache.add("NAME"+id, ""+getName(id), validationPeriod);
                    calcValue = ""+getName(id);
                }
                if (command.equalsIgnoreCase("RA")) {
                    if (storeName.toUpperCase().contains("RAID_VALUE")) {
                        String name = storeName.substring(10);
                   //     Cache.add("RAID_VALUE"+name, ""+getRaidValue(name), validationPeriod);
                        calcValue = ""+getRaidValue(name)+".00";
                    } else {
                        String name = storeName.substring(2);
                   //     Cache.add("RA"+name, ""+getRA(name), validationPeriod);
                        calcValue = ""+getRA(name);
                    }
                }
                if (command.equalsIgnoreCase("IT")) {
                    String name = storeName.substring(10);
               //     Cache.add("ITEM_VALUE"+name, ""+getItemValue(name), validationPeriod);
                    calcValue = ""+getItemValue(name)+".00";
                }
                
                if (calcValue.isEmpty()) {
                 //   Cache.add(entry[0], entry[1], validationPeriod);
                    continue;
                }
                
                if (!calcValue.equalsIgnoreCase(entry[1])) {
                    EverQuest.sendTell(playerName, 
                            "Fixed a cache entry: [" + storeName
                            + "], where value was: [" + entry[1]
                            + "], but the website tells me its: [" + calcValue 
                            + "]");
                }
            }
        }
    }
    
    /**
     * Substitutes alts with their main's name. Not destructive.
     * 
     * Example:
     * 
     * Boogalooga -> returns Aledark
     * Aledark -> returns Aledark
     * 
     * @param name 
     * @return 
     */
    public static String getAlias(String name) {
        try {
            Table alias = Database.getTable("alias");

            if (alias == null) {
                alias = Database.addTable("alias");
                alias.addRows("Alias", "Name");
            }

            Table connectedAlias = alias.where("Alias", name);
            if (!connectedAlias.isEmpty()) {
                return connectedAlias.select("Name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return name;
    }
    
    /**
     * Returns the current ID used by the website to describe a player's name.
     * 
     * @param name
     * @return 
     */
    public int getID(String name) {
        int id = -1;
        
        name = SQL.getAlias(name);
        
        try {
            String qry = "SELECT member_id FROM eqdkp20_members WHERE member_name='"+name+"';";
            
            id = Integer.parseInt(cacheSqlQuery("ID"+name, qry, Cache.WEEK*52));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (id == -1) throw new NumberFormatException("User not found: " + name);
        
        return id;
    }
    
    /**
     * Returns the name from the give ID that the website displays.
     * 
     * @param id
     * @return 
     */
    public String getName(String id) {
        String name = "";
        
        try {
            String qry = "SELECT member_name FROM eqdkp20_members WHERE member_id="+id+";";
            
            name = cacheSqlQuery("NAME"+id, qry, Cache.WEEK*52);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return SQL.getAlias(name);
    }
    
    /**
     * Gets the listed level of the player on the website (Not real-time synced and is player updated).
     * 
     * @param name
     * @return 
     */
    public int getLevel(String name) {
        String stm = "SELECT member_level FROM eqdkp20_members WHERE member_name='"+name+"';";
        
        String level = cacheSqlQuery("LEVEL"+name, stm, Cache.WEEK*52);
        
        try {
            return Integer.parseInt(level);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return 100;
    }
    
    /**
     * Returns the default RA% for the given player, which is RA over the last 30 days.
     * 
     * @param name
     * @return 
     */
    public int getRA(String name) {
        return getRA(name, DAYS_30);
    }
    
    /**
     * Returns the RA% for the given time period.
     * Time is in seconds.
     * 
     * @param name
     * @param timeAgo
     * @return 
     */
    public int getRA(String name, long timeAgo) {
        if (EverQuest.isTesting)
            return 100;
        
        long cutoffTime = (new Date().getTime() / 1000) - timeAgo;
        
        int totalRaids = getTotalRaids(cutoffTime);
        
        if (totalRaids == 0)
            return 0; // Avoid division by 0
        
        int attRaids = getRaidsAttended(SQL.getAlias(name), cutoffTime);

        return attRaids * 100 / totalRaids;
    }
    
    /**
     * Returns the RA% for the given time period from Afternoon raids only.
     * 
     * @param name
     * @param timeAgo
     * @return 
     */
    public int getRAAfternoon(String name, long timeAgo) {
        long cutoffTime = (new Date().getTime() / 1000) - timeAgo;
        
        int totalRaids = getTotalRaids(cutoffTime);
        
        if (totalRaids == 0)
            return 0; // Avoid division by 0
        
        int attRaids = getRaidsAttendedAfternoon(SQL.getAlias(name), cutoffTime);

        return attRaids * 100 / (totalRaids / 2);
    }
    
    /**
     * Returns the RA% for the given time period from Night raids only.
     * 
     * @param name
     * @param timeAgo
     * @return 
     */
    public int getRANight(String name, long timeAgo) {
        long cutoffTime = (new Date().getTime() / 1000) - timeAgo;
        
        int totalRaids = getTotalRaids(cutoffTime);
        
        if (totalRaids == 0)
            return 0; // Avoid division by 0
        
        int attRaids = getRaidsAttendedNight(SQL.getAlias(name), cutoffTime);

        return attRaids * 100 / (totalRaids / 2);
    }
    
    /**
     * Returns the current spendable DKP for the given player.
     * 
     * @param name
     * @return 
     */
    public int getDKP(String name) {
        int dkp = 0;
        
        if (EverQuest.isTesting) {
            return name.length()*10;
        }
        
        dkp += getRaidValue(SQL.getAlias(name));
        dkp -= getItemValue(SQL.getAlias(name));
        
        return dkp;
    }
    
    /**
     * Returns the total amount of raids for the given time period.
     * 
     * @param sinceTime
     * @return 
     */
    public int getTotalRaids(long sinceTime) {
        try {
            String qryRaids = "SELECT COUNT(raid_id) FROM eqdkp20_raids WHERE eqdkp20_raids.raid_date > " + sinceTime + ";";
            
            return Integer.parseInt(cacheSqlQuery("TotalRaids", qryRaids, Cache.WEEK*52));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Returns the number of days since the given player has last won an item.
     * 
     * @param name
     * @return 
     */
    public int getDaysSinceLastWonItem(String name) {
        if (EverQuest.isTesting)
            return 1;
        
        int id = getID(SQL.getAlias(name));
        
        String qry = "SELECT MAX(item_date) FROM eqdkp20_items WHERE member_id = " + id + ";";
        
        long lastDate = 0;
        try {
            lastDate = Long.parseLong(cacheSqlQuery("LD"+name, qry, 2*Cache.WEEK*52));
        } catch (Exception e) {
        }
        long currentDate = new Date().getTime() / 1000;
        
        long dif = currentDate - lastDate;
        
        int days = (int)(dif / Cache.DAY);
        
        return days;
    }
    
    /**
     * Returns a time period from 30 days ago in seconds.
     * 
     * @return 
     */
    public static long get30DaysAgo() {
        return (new Date().getTime() / 1000) - DAYS_30;
    }
    
    /**
     * Returns the amount of raids attended for the given time period for the given player.
     * 
     * @param name
     * @param cutoffTime
     * @return 
     */
    public int getRaidsAttended(String name, long cutoffTime) {
        try {
            String qryRaids = "SELECT COUNT(raid_value) \n" +
                        "FROM ( SELECT * FROM eqdkp20_members WHERE member_name='"+SQL.getAlias(name)+"' ) S \n" +
                        "INNER JOIN eqdkp20_raid_attendees \n" +
                        "ON S.member_id=eqdkp20_raid_attendees.member_id \n" +
                        "INNER JOIN eqdkp20_raids\n" +
                        "ON eqdkp20_raid_attendees.raid_id=eqdkp20_raids.raid_id AND eqdkp20_raids.raid_date > " + cutoffTime +";";
            
            return Integer.parseInt(cacheSqlQuery("RA"+SQL.getAlias(name), qryRaids, Cache.WEEK*52));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Returns the amount of raids attended for the given time period for the given player for Afternoon raids.
     * 
     * @param name
     * @param cutoffTime
     * @return 
     */
    public int getRaidsAttendedAfternoon(String name, long cutoffTime) {
        try {
            String qryRaids = "SELECT COUNT(raid_value) \n" +
                        "FROM ( SELECT * FROM eqdkp20_members WHERE member_name='"+SQL.getAlias(name)+"' ) S \n" +
                        "INNER JOIN eqdkp20_raid_attendees \n" +
                        "ON S.member_id=eqdkp20_raid_attendees.member_id \n" +
                        "INNER JOIN eqdkp20_raids\n" +
                        "ON eqdkp20_raid_attendees.raid_id=eqdkp20_raids.raid_id " +
                        "AND eqdkp20_raids.raid_date > " + cutoffTime + " " +
                        "AND ((eqdkp20_raids.raid_date % 86400) / 3600) > 12";
            
            return Integer.parseInt(cacheSqlQuery("RAM"+SQL.getAlias(name), qryRaids, Cache.WEEK*52));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Returns the amount of raids attended for the given time period for the given player for Night raids.
     * 
     * @param name
     * @param cutoffTime
     * @return 
     */
    public int getRaidsAttendedNight(String name, long cutoffTime) {
        try {
            String qryRaids = "SELECT COUNT(raid_value) \n" +
                        "FROM ( SELECT * FROM eqdkp20_members WHERE member_name='"+SQL.getAlias(name)+"' ) S \n" +
                        "INNER JOIN eqdkp20_raid_attendees \n" +
                        "ON S.member_id=eqdkp20_raid_attendees.member_id \n" +
                        "INNER JOIN eqdkp20_raids\n" +
                        "ON eqdkp20_raid_attendees.raid_id=eqdkp20_raids.raid_id " +
                        "AND eqdkp20_raids.raid_date > " + cutoffTime + " " +
                        "AND ((eqdkp20_raids.raid_date % 86400) / 3600) < 12";
            
            return Integer.parseInt(cacheSqlQuery("RAA"+SQL.getAlias(name), qryRaids, Cache.WEEK*52));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Returns the total DKP earned from raiding for the given player.
     * 
     * @param name
     * @return 
     */
    public int getRaidValue(String name) {
        int dkp = 0;
        
        try {
            String qryRaids = "SELECT SUM(raid_value) \n" +
                        "FROM ( SELECT * FROM eqdkp20_members WHERE member_name='"+SQL.getAlias(name)+"' ) S \n" +
                        "INNER JOIN eqdkp20_raid_attendees \n" +
                        "ON S.member_id=eqdkp20_raid_attendees.member_id \n" +
                        "INNER JOIN eqdkp20_raids\n" +
                        "ON eqdkp20_raid_attendees.raid_id=eqdkp20_raids.raid_id;";
            
            dkp = (int)Double.parseDouble(cacheSqlQuery("RAID_VALUE"+SQL.getAlias(name), qryRaids, Cache.WEEK*52));
        } catch (Exception e) {
            // Havent attended raids
        }
        
        return dkp;
    }
  //  public int testCase(int value1) {
    //    int dkpTest = 0;
        
      //  try {
        //    String qryRaids = "SELECT COUNT(raid_value) \n" +
          //              "FROM ( SELECT * FROM eqdkp20_members WHERE member_name='Methadone' ) S \n" +
            //            "INNER JOIN eqdkp20_raid_attendees \n" +
              //          "ON S.member_id=eqdkp20_raid_attendees.member_id \n" +
                //        "INNER JOIN eqdkp20_raids\n" +
                 //       "ON eqdkp20_raid_attendees.raid_id=eqdkp20_raids.raid_id;";
           // dkpTest = (int)Double.parseDouble(cacheSqlQuery("RAID_VALUE"+SQL.getAlias(value1), qryRaids, Cache.WEEK*52));
       // } catch (Exception e) {
        //    // Havent attended raids
       // }
        
      //  return dkpTest;
    //}
    /**
     * Returns the total amount the given player has spent on buying raid items.
     * 
     * @param name
     * @return 
     */
    public int getItemValue(String name) {
        int value = 0;
        try {
            String qryItems = "SELECT SUM(item_value) \n" +
                        "FROM ( SELECT * FROM eqdkp20_members WHERE member_name='"+SQL.getAlias(name)+"' ) S \n" +
                        "INNER JOIN eqdkp20_items \n" +
                        "ON S.member_id=eqdkp20_items.member_id;";

            value = (int)Double.parseDouble(cacheSqlQuery("ITEM_VALUE"+SQL.getAlias(name), qryItems, Cache.WEEK*52));
        } catch (Exception e) {
            // Havent purchased items
        }
        return value;
    }
    
    /**
     * Adds a raid using the given event and a list of attendees.
     * 
     * Creates a raid in the database with the given event and then
     * updates the raid attendance database with a list of all players
     * in attendance.
     * 
     * Updates the cached values for spendable DKP.
     * 
     * @param event Event ID (Events are found in SQL.*)
     * @param attendees 
     */
    public void addRaid(int event, ArrayList<String> attendees) {
        // Clear the list of players who are not registered
        notRegistered.clear();
        
        // Add the raid and use the new raid's ID
        int raidId = addNewRaid(event);
        
        // Calculate the DKP amount earned for this raid. Default is 1.
        int dkpAdded = 1;
        if (event == EVENT_ON_TIME) dkpAdded = 2;
        if (event == PROG_EVENT_ON_TIME) dkpAdded = 4;
        if (event == PROG_EVENT_HOUR_1) dkpAdded = 2;
        if (event == PROG_EVENT_HOUR_2) dkpAdded = 2;
        if (event == PROG_EVENT_HOUR_3) dkpAdded = 2;
        if (event == PROG_EVENT_HOUR_4) dkpAdded = 2;
        if (event == PROG_EVENT_RAID_ENDED) dkpAdded = 2;
        
        // Update the cached value for total number of raids by 1
      //  Cache.add("TotalRaids", ""+(getTotalRaids(get30DaysAgo()) + 1), Cache.WEEK*52);
        
        String stm = "";
        for (String att : attendees) {
            try {
                int attId = getID(att);
                if (attId <= 0) {
                    // Player is not registered on the website
                    notRegistered.add(att);
                    continue;
                }
                
                String values = "(" + raidId + ", " + attId + ")";
                
                if (stm == null || stm.isEmpty()) {
                    stm = "INSERT HIGH_PRIORITY INTO eqdkp20_raid_attendees (raid_id, member_id) VALUES " + values;
                } else {
                    stm += " ," + values;
                }
                
           //     Cache.add("RAID_VALUE"+att, ""+(getRaidValue(att)+dkpAdded), Cache.WEEK*52);
            //    Cache.add("RA"+att, ""+(getRaidsAttended(att, get30DaysAgo()) + 1), Cache.WEEK*52);
            } catch (Exception e) {
                notRegistered.add(att);
            }
        }
        runSqlStatement(stm + ";");
    }
    
    /**
     * Sends a request to the SQL server to add a given item to the database.
     * 
     * @param winner Player who won the item
     * @param item Name of the item that was won
     * @param cost How much the player paid the for item
     */
    public void addItem(String winner, String item, int cost) {
        int memberId = getID(winner);
        int raidId = getLastRaidID();
        long itemDate = new Date().getTime()/1000;
        String addedBy = "'Raidbot'";
        String itemGroupKey = getRandomGroupKey();
        int itempoolId = 1;
        
        String stm = "INSERT HIGH_PRIORITY INTO eqdkp20_items "
                + "(item_name, member_id, raid_id, item_value, item_date, item_added_by, item_group_key, itempool_id) "
                + "VALUES (\" "+item+" \", "+memberId+", "+raidId+", "+cost+", "+itemDate+", "+addedBy+", '"+itemGroupKey+"', "+itempoolId+");";
             
    
        runSqlStatement(stm);
        
        // Update the cached value if a value exists
        int itemValue = getItemValue(winner) + cost;
      //  Cache.add("ITEM_VALUE"+winner, ""+itemValue, Cache.WEEK*52);
     //   Cache.add("LD"+winner, ""+itemDate, Cache.WEEK*52);
    }
    
    /**
     * Sends a SQL request to the server to add a new raid to the database using the given event id.
     * 
     * @param eventId
     * @return Raid ID from the generated raid.
     */
    private int addNewRaid(int eventId) {
        long raidDate = new Date().getTime()/1000;
        
        int value = 1;
        if (eventId == EVENT_ON_TIME) value = 2;
        if (eventId == PROG_EVENT_ON_TIME) value = 4;
        if (eventId == PROG_EVENT_HOUR_1) value = 2;
        if (eventId == PROG_EVENT_HOUR_2) value = 2;
        if (eventId == PROG_EVENT_HOUR_3) value = 2;
        if (eventId == PROG_EVENT_HOUR_4) value = 2;
        if (eventId == PROG_EVENT_RAID_ENDED) value = 2;
        String addedBy = "Raidbot";
        
        String stm = "INSERT HIGH_PRIORITY INTO eqdkp20_raids (event_id, raid_date, raid_note, raid_value, raid_added_by) "
                + "VALUES (" + eventId + ", " + raidDate + ", '', " + value + ", '" + addedBy + "')";
        
        runSqlStatement(stm);
        
        String qry = "SELECT raid_id FROM eqdkp20_raids WHERE raid_date = " + raidDate + ";";
        
        int raidId = Integer.parseInt(runSqlQuery(qry));
                
        return raidId;        
    }
    
    /**
     * @return The Raid ID from the last raid added. 
     */
    public int getLastRaidID() {
        String maxRaidID = runSqlQuery("SELECT MAX(raid_id) FROM eqdkp20_raids;");
        return Integer.parseInt(maxRaidID);
    }
    
    /**
     * Creates a connection with the MySQL Database.
     */
    private void createConnection() {
        if (EverQuest.isTesting)
            return;
        
        try {
            // If the connection is still alive but NOT valid
            if (connection != null && !connection.isValid(1)) {
                System.out.println("Open and not valid");
                // Attempt to close the connection nicely
                if (!connection.isClosed())
                    closeConnection();
                
                // Force a reset of the connection no matter what
                connection = null;
                openConnections.remove(this);
            }
            
            // If connection was closed (thus set to null) or no connection esablished yet, make a new connection
            if (connection == null) {
                String host = "jdbc:mysql://www.cerberusguild.com:3306/jenninna_dkp";
                String username = "jenninna_druad";
                String password = "P4rtyT1m3!";
                String driver = "com.mysql.jdbc.Driver";
                Class.forName(driver);
                connection = DriverManager.getConnection(host, username, password);
                openConnections.add(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void closeConnection() {
        if (EverQuest.isTesting)
            return;
        
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        connection = null;
    }
    
    public void runSqlStatement(String string) {
        if (EverQuest.isTesting) {
            TestCases.log(string);
            return;
        }
        
        createConnection();
        
        System.out.println("SQL STM: ["+string+"]");
        
        try {
            Statement statement = connection.createStatement();
            boolean rs = statement.execute(string);
        } catch (Exception e) {
            e.printStackTrace();
            Data.failedSQL.addEntry("Statement", string);
        }
        
        closeConnection();
    }
 
    public String runSqlQuery(String string) {
        if (EverQuest.isTesting) {
            TestCases.log(string);
            return "";
        }
        
        createConnection();
        
        String result = "";
        
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(string);

            if (rs.next())
                result = rs.getString(1);
        } catch (Exception e) {
            e.printStackTrace();
            Data.failedSQL.addEntry("Query", string);
        }
        
        System.out.println("SQL QRY: " + result + ": ["+string+"]");
        
        closeConnection();
        
        return result;
    }
    
    public String cacheSqlQuery(String cache, String query, long timeToLive) {
        // Check if cached value exists
        String cachedValue = Cache.get(cache);
        if (!cachedValue.isEmpty()) return cachedValue;
        
        // Run Query
        String result = runSqlQuery(query);
        
        // Cache the value
     //   Cache.add(cache, result, timeToLive);
        
        return result;
    }
    
    /**
     * Generates a group key for an item.
     * 
     * A group key allows items to be placed in the same bundle
     * (Same item name, same price, multiple buyers)
     * 
     * This can potentially fail if 2 separate items roll the same group key.
     * 
     * The odds of this failing is a Birthday Paradox problem.
     * See: http://en.wikipedia.org/wiki/Birthday_problem
     * 
     * Thus the chance of this failing is around 1 in 22,000,000,000,000,000,000
     * Which, thankfully at our rate our winning items will take our guild
     * approximately 964,912,280,701,754 years to happen.
     * 
     * I'm not too worried.
     * 
     * @return 
     */
    public String getRandomGroupKey() {
        String s = "";
        for (int i = 0; i < 32; i++) {
            int v = (int)(Math.random()*16);
            switch (v) {
                case 10: s += "a"; break;
                case 11: s += "b"; break;
                case 12: s += "c"; break;
                case 13: s += "d"; break;
                case 14: s += "e"; break;
                case 15: s += "f"; break;
                default: s += ""+v; break;
            }
        }
        return s;
    }
    
    /**
     * Returns the class of the player based on the website's character.
     * 
     * This method could be manipulated into false classes by passing parsed aliases.
     * 
     * @param name
     * @return 
     */
    public String getPlayerClass(String name) {
        String stm = "SELECT member_class_id FROM eqdkp20_members WHERE member_name='"+name+"';";
        
        String classID = cacheSqlQuery("CLASS"+name, stm, Cache.WEEK*52);
        
        if (classID.equals("1")) return BARD;
        if (classID.equals("2")) return BEASTLORD;
        if (classID.equals("3")) return BERZERKER;
        if (classID.equals("4")) return ENCHANTER;
        if (classID.equals("5")) return MAGICIAN;
        if (classID.equals("7")) return NECROMANCER;
        if (classID.equals("8")) return PALADIN;
        if (classID.equals("9")) return RANGER;
        if (classID.equals("10")) return ROGUE;
        if (classID.equals("11")) return SHADOWKNIGHT;
        if (classID.equals("12")) return SHAMAN;
        if (classID.equals("13")) return WARRIOR;
        if (classID.equals("14")) return WIZARD;
        if (classID.equals("15")) return CLERIC;
        if (classID.equals("16")) return DRUID;
        
        return ""+classID;
    }
}
