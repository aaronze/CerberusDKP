package menu.dkp;

import menu.Menu;
import menu.User;

/**
 * @author Aaron
 */
public class DKPMenu extends Menu {

    public DKPMenu(User user) {
        super(user);
        
        setAccessLevelToUser();
        
        add(new CheckMyDKP());
        add(new CheckMyRA());
        add(new CheckOthersDKP());
        add(new TakeDKP());
    }

    @Override
    public String getDescription() {
        return "DKP ...";
    }

}
