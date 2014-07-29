package menu.raidbot;

import menu.Command;
import menu.User;
import util.EverQuest;

/**
 * @author Aaron
 */
public class JoinMyRaid extends Command {

    @Override
    public void init() {
        setAccessLevelToOfficer();
    }

    @Override
    public void activate(User user) {
        EverQuest.sendToEQ("/raidaccept");
        
        sendTell(user, "Done.");
    }

    @Override
    public String getDescription() {
        return "Join my raid (Send an invite first)";
    }

}
