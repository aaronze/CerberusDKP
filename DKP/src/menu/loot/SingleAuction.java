package menu.loot;

import bot.Auctions;
import ui.DiceMonitor;
import menu.Command;
import menu.User;

/**
 * @author Aaron
 */
public class SingleAuction extends Command {

    @Override
    public void init() {
        setAccessLevelToOfficer();
        
        add("Link the item:");
    }

    @Override
    public void activate(User user) {
        String item = getNextInput();
        
        // Check if item looks valid
        if (item.equals("m") || item.equals("menu") || DiceMonitor.isNumeric(item)) {
            sendTell(user, "I'm not sure you want to actually auction that. Cancelled Auction.");
            return;
        }
        
        // Auction the single item
        Auctions.startAuction(item, 1);
    }

    @Override
    public String getDescription() {
        return "Auction an item";
    }

}
