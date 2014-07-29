package menu.users;

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
public class ReconcileRaiders extends Command {

    @Override
    public void init() {
        setAccessLevelToSeniorOfficer();
    }

    @Override
    public void activate(User user) {
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

                // Ignore if dealth with already
                if (duplicate.contains(name)) continue;

                // Ignore any Alt Flagged
                if (altFlag.equalsIgnoreCase("a")) continue;

                // Only concern ourselves with rank of Member or Raider
                if (rank.equalsIgnoreCase("member")) {
                    int ra = data.getRA(name, SQL.DAYS_30);

                    // Promote if RA reaches above 15%
                    if (ra >= 15) {
                        EverQuest.promote(name);
                    }
                }
                if (rank.equalsIgnoreCase("raider")) {
                    int ra = data.getRA(name, SQL.DAYS_30);

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
    }

    @Override
    public String getDescription() {
        return "Promote/Demote Raiders based on Raid Attendance";
    }

}
