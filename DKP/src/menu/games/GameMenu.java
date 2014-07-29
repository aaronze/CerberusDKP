package menu.games;

import menu.Menu;
import menu.User;

/**
 * @author Aaron
 */
public class GameMenu extends Menu {

    public GameMenu(User user) {
        super(user);
        
        add(new Blackjack());
    }

    @Override
    public String getDescription() {
        return "Play a Game ...";
    }
    
    

}
