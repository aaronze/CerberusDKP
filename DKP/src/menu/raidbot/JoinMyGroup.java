package menu.raidbot;

import menu.Command;
import menu.User;
import util.EverQuest;

/**
 * @author Aaron
 */
public class JoinMyGroup extends Command {

    @Override
    public void init() {
        setAccessLevelToOfficer();
    }

    @Override
    public void activate(User user) {
        EverQuest.sendToEQ("/invite " + user);
        
        sendTell(user, "Done.");
    }

    @Override
    public String getDescription() {
        return "Join my group (Send an invite first)";
    }

}
