package menu.users.alias;

import menu.Menu;
import menu.User;

/**
 * @author Aaron
 */
public class AliasMenu extends Menu {

    public AliasMenu(User user) {
        super(user);
        
        setAccessLevelToOfficer();
        
        add(new AddAlias());
        add(new RemoveAlias());
        add(new ChangeMain());
    }

    @Override
    public String getDescription() {
        return "Alias Management ...";
    }

}
