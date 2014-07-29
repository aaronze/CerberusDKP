package menu.users;

import menu.Command;
import menu.User;

/**
 * @author Aaron
 */
public class SetUserAccess extends Command {

    @Override
    public void init() {
        setAccessLevelToOfficer();
        
        add("Which Player?");
        
        String ranks = "Set player to: (1. User, 2. Officer, 3. Senior Officer, 4. Super User)";
        add(ranks);
    }

    @Override
    public void activate(User user) {
        String name = getNextName();
        
        int rank = getNextInt();

        int priv = DEFAULT;
        if (rank == 1) priv = USER;
        if (rank == 2) priv = OFFICER;
        if (rank == 3) priv = SENIOR;
        if (rank == 4) priv = SUPER_USER;

        if (setPriveledges(user, name, priv)) {
            String s = "";
            if (priv == USER) s += "User";
            if (priv == OFFICER) s += "Officer";
            if (priv == SENIOR) s += "Senior Officer";
            if (priv == SUPER_USER) s += "Super User";
            sendTell(user, "Done. I have made " + name + " a " + s + ".");
        }
    }

    @Override
    public String getDescription() {
        return "Set user Access";
    }

}
