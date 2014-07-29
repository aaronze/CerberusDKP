package menu.users;

import menu.Command;
import menu.User;

/**
 * @author Aaron
 */
public class SilenceUser extends Command {

    @Override
    public void init() {
        setAccessLevelToOfficer();
        
        add("Which player?");
    }

    @Override
    public void activate(User user) {
        String name = getNextName();
        
        setPriveledges(user, name, SILENCED);
        
        sendTell(user, "Done. I have silenced " + name);
    }

    @Override
    public String getDescription() {
        return "Silence User";
    }

}
