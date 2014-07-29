package menu.games;

import games.Game;
import menu.Command;
import menu.User;
import secure.Account;

/**
 * @author Aaron
 */
public class Blackjack extends Command {
    private Game game;
    
    @Override
    public void init() {
        add("> ");
    }

    @Override
    public void activate(User user) {
        game = new games.cards.Blackjack(new Account(user.name));
        game.newGame();
    }

    @Override
    public String getDescription() {
        return "Play Blackjack";
    }

    @Override
    public boolean input(String in) {
        if (game != null && !game.isRunning()) {
            game = null;
            return false;
        }
        
        if (game == null) {
            game = new games.cards.Blackjack(new Account(user.name));
            game.newGame();
        }
        
        if (!game.isRunning()) {
            game = null;
            return false;
        }
        
        game.input(in);
        
        for (String s : game.output()) {
            sendTell(user, s);
        }
        
        return true;
    }

    
}
