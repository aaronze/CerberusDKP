package menu.users.alias;

import util.Data;
import menu.Command;
import menu.User;

/**
 * @author Aaron
 */
public class ChangeMain extends Command {

    @Override
    public void init() {
        setAccessLevelToSeniorOfficer();
        
        add("What is the old main?");
        add("What is their new main?");
    }

    @Override
    public void activate(User user) {
        String oldName = getNextName();
        String newName = getNextName();
        
        // Avoid infinite loop case
        if (newName.equalsIgnoreCase(oldName)) {
            sendTell(user, "Cannot set a main to the same name.");
            return;
        }
        
        // Update aliases
        while (Data.alias.where("Name", oldName).size() > 0) {
            Data.alias.updateEntry("Name", oldName, "Name", newName);
        }
        
        sendTell(user, "Changed " + oldName + " to " + newName);
    }

    @Override
    public String getDescription() {
        return "Change a main";
    }

}
