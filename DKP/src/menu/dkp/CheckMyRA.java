package menu.dkp;

import menu.Command;
import menu.User;

/**
 * @author Aaron
 */
public class CheckMyRA extends Command {

    @Override
    public void init() {
        setAccessLevelToUser();
    }

    @Override
    public String getDescription() {
        return "What's my Raid Attendence?";
    }

    @Override
    public void activate(User user) {
        int ra = data.getRA(user.name);
        
        sendTell(user, "You have " + ra + "% Raid Attendance for the last 30 days!");
    }

}
