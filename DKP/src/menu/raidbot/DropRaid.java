package menu.raidbot;

import menu.Command;
import menu.User;
import util.EverQuest;

/**
 * @author Aaron
 */
public class DropRaid extends Command {

    @Override
    public void init() {
        setAccessLevelToOfficer();
    }

    @Override
    public void activate(User user) {
        EverQuest.sendToEQ("/raiddisband");
        
        sendTell(user, "Done.");
    }

    @Override
    public String getDescription() {
        return "Remove Current Raid";
    }

}
