package menu.users;

import menu.Menu;
import menu.User;
import menu.users.alias.AliasMenu;

/**
 * @author Aaron
 */
public class UserMenu extends Menu {

    public UserMenu(User user) {
        super(user);
        
        setAccessLevelToOfficer();

        add(new AliasMenu(user));
        add(new SetUserAccess());
        add(new SilenceUser());
        add(new ReconcileRaiders());
    }

    @Override
    public String getDescription() {
        return "User Managerment ...";
    }

}
