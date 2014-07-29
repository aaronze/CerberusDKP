package menu;

import menu.dkp.DKPMenu;
import menu.games.GameMenu;
import menu.loot.LootMenu;
import menu.options.OptionMenu;
import menu.users.UserMenu;

/**
 * @author Aaron
 */
public class MainMenu extends Menu {

    public MainMenu(User user) {
        super(user);
        
        add(new DKPMenu(user));
        add(new LootMenu(user));
        //add(new OptionMenu(user));
        add(new UserMenu(user));
        add(new GameMenu(user));
    }

    @Override
    public String getDescription() {
        return "Main Menu ...";
    }

}
