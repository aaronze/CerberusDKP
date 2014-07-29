package menu.options;

import menu.Menu;
import menu.User;

/**
 * @author Aaron
 */
public class OptionMenu extends Menu {

    public OptionMenu(User user) {
        super(user);
        
        setAccessLevelToUser();
        
        add(new ToggleFastMenu());
    }

    @Override
    public String getDescription() {
        return "My Preferences ...";
    }

}
