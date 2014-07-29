package menu.users.alias;

import util.Data;
import menu.Command;
import menu.User;

/**
 * @author Aaron
 */
public class RemoveAlias extends Command {

    @Override
    public void init() {
        setAccessLevelToOfficer();
        
        add("Which box alias?");
    }

    @Override
    public void activate(User user) {
        String box = getNextName();
        
        Data.alias.removeEntry(box);
        
        sendTell(user, "Removed alias " + box + ".");
    }

    @Override
    public String getDescription() {
        return "Remove a box alias";
    }
    
}
