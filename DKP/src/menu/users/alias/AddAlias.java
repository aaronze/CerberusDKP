package menu.users.alias;

import util.Data;
import menu.Command;
import menu.User;

/**
 * @author Aaron
 */
public class AddAlias extends Command {

    @Override
    public void init() {
        setAccessLevelToOfficer();
        
        add("What is their main?");
        add("What is thier box?");
    }

    @Override
    public void activate(User user) {
        String main = getNextName();
        
        String box = getNextName();
        
        Data.alias.addEntry(box, main);
        
        sendTell(user, "Connected " + box + " to " + main + ".");
    }

    @Override
    public String getDescription() {
        return "Add a new alias";
    }

}
