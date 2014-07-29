package menu.raidbot;

import ui.DiceMonitor;
import menu.Command;
import menu.User;

/**
 * @author Aaron
 */
public class SetAuctionChannel extends Command {

    @Override
    public void init() {
        setAccessLevelToSeniorOfficer();
        
        add("Set channel to (Example: /rs):");
    }

    @Override
    public void activate(User user) {
        String channel = getNextInput();
        
        DiceMonitor.CHANNEL = channel + " ";
        
        sendTell(user, "Set channel to " + channel);
    }

    @Override
    public String getDescription() {
        return "Set current auction channel";
    }

}
