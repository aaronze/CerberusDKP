package menu.raidbot;

import ui.DiceMonitor;
import menu.Command;
import menu.User;

/**
 * @author Aaron
 */
public class ResetRaidbot extends Command {

    @Override
    public void init() {
        setAccessLevelToSeniorOfficer();
    }

    @Override
    public void activate(User user) {
        DiceMonitor.resetRaidbot(user.name);
    }

    @Override
    public String getDescription() {
        return "Fully Reset Raidbot";
    }

}
