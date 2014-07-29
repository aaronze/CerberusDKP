package menu.loot;

import menu.Menu;
import menu.User;

/**
 * @author Aaron
 */
public class LootMenu extends Menu {

    public LootMenu(User user) {
        super(user);
        
        setAccessLevelToOfficer();
        
        add(new SingleAuction());
        add(new MultipleAuction());
        add(new DeclareAnotherWinner());
        add(new SingleRandom());
        add(new MultipleRandom());
    }

    @Override
    public String getDescription() {
        return "Raid Loot ...";
    }

}
