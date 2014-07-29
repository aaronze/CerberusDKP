package menu.dkp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import menu.Command;
import menu.User;
import util.EverQuest;
import util.SQL;

/**
 * @author Aaron
 */
public class TakeDKP extends Command {
    public static int CURRENT_HOUR = 1;

    @Override
    public void init() {
        setAccessLevelToOfficer();
        
        add("What type? (1. On-Time, 2. Hourly, 3. Raid End, 4. Progression On-Time, 5. Progression Hourly, 6. Progression Raid End)");
    }

    @Override
    public String getDescription() {
        return "Take DKP";
    }
    
    @Override
    public void activate(User user) {
        // Get first selected option
        String option = getNextInput();
        
        // Dump guild logs and declare taking DKP
        EverQuest.guildDump();
        EverQuest.sendToEQ("/gu [Taking DKP]");
        
        // Get the event id of the chosen event
        int event = getEventId(option);
        
        // Get the attendees for the current raid
        ArrayList<String> attendees = getAttendees();
        
        // Add the raid
        SQL sql = new SQL();
        sql.addRaid(event, attendees);
        sql.closeConnection();
        
        // Declare players who are not registered
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
        
        EverQuest.sendTell(user.name, "Raid Added with " + attendees.size() + " raiders!");
        EverQuest.sendToEQ("/gu <DKP Taken: Added " + attendees.size() + " raiders>");
    }
    
    public int getEventId(String option) {
        // Default to Hour 1 if weird input
        int eventId = SQL.EVENT_HOUR_1;
        
        // Work out what type of DKP they want to take
        if (option.equalsIgnoreCase("1")) {
            eventId = SQL.EVENT_ON_TIME;
            CURRENT_HOUR = 1;
        }
        if (option.equalsIgnoreCase("2")) {
            if (CURRENT_HOUR == 1) eventId = SQL.EVENT_HOUR_1;
            if (CURRENT_HOUR == 2) eventId = SQL.EVENT_HOUR_2;
            if (CURRENT_HOUR >= 3) eventId = SQL.EVENT_HOUR_3;
        }
        if (option.equalsIgnoreCase("3")) {
            eventId = SQL.EVENT_RAID_ENDED;
        }
        if (option.equalsIgnoreCase("4")) {
            eventId = SQL.PROG_EVENT_ON_TIME;
            CURRENT_HOUR = 1;
        }
        if (option.equalsIgnoreCase("5")) {
            if (CURRENT_HOUR == 1) eventId = SQL.PROG_EVENT_HOUR_1;
            if (CURRENT_HOUR == 2) eventId = SQL.PROG_EVENT_HOUR_2;
            if (CURRENT_HOUR >= 3) eventId = SQL.PROG_EVENT_HOUR_3;
        }
        if (option.equalsIgnoreCase("6")) {
            eventId = SQL.PROG_EVENT_RAID_ENDED;
        }
        return eventId;
    }
    
    public ArrayList<String> getAttendees() {
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
        
        return names;
    }    
}
