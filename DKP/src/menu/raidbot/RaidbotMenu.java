package menu.raidbot;

import menu.Menu;
import menu.User;

/**
 * @author Aaron
 */
public class RaidbotMenu extends Menu {

    public RaidbotMenu(User user) {
        super(user);
        
        setAccessLevelToOfficer();
        
        add(new JoinMyGroup());
        add(new JoinMyRaid());
        add(new DropRaid());
        add(new SetAuctionChannel());
        add(new ResetRaidbot());
    }

    @Override
    public String getDescription() {
        return "Raidbot Control ...";
    }

}
