package menu.dkp;

import menu.Command;
import menu.User;

/**
 * @author Aaron
 */
public class CheckOthersDKP extends Command {

    @Override
    public void init() {
        setAccessLevelToOfficer();
        
        add("What is the player's name?");
    }

    @Override
    public String getDescription() {
        return "Lookup someone else's DKP";
    }

    @Override
    public void activate(User user) {
        String name = getNextInput();
        
        int dkp = data.getDKP(name);
        int ra = data.getRA(name);
        
        sendTell(user, "Stats for " + name);
        sendTell(user, "DKP: " + dkp);
        sendTell(user, "RA: " + ra + "%");
    }

}
