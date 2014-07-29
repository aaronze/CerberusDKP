package menu.dkp;

import menu.Command;
import menu.User;

/**
 * @author Aaron
 */
public class CheckMyDKP extends Command {

    @Override
    public void init() {
        setAccessLevelToUser();
    }

    @Override
    public String getDescription() {
        return "What's my DKP?";
    }

    @Override
    public void activate(User user) {
        int dkp = data.getDKP(user.name);
        
        sendTell(user, "You have " + dkp + " DKP!");
    }

}
