package menu.loot;

import bot.Auctions;
import ui.DiceMonitor;
import menu.Command;
import menu.User;

/**
 * @author Aaron
 */
public class MultipleAuction extends Command {

    @Override
    public void init() {
        setAccessLevelToOfficer();
        
        add("How many?");
        add("Link the item:");
    }

    @Override
    public void activate(User user) {
        int amount = getNextInt();

        // If not a valid amount
        if (amount == 0) {
            sendTell(user, "Umm, you know... I didn't understand that number. Cancelled Auction.");
            return;
        }
        
        String item = getNextInput();
        
        // Check if item looks valid
        if (item.equals("m") || item.equals("menu") || DiceMonitor.isNumeric(item)) {
            sendTell(user, "I'm not sure you want to actually auction that. Cancelled Auction.");
            return;
        }
        
        // Auction the single item
        Auctions.startAuction(item, amount);
    }

    @Override
    public String getDescription() {
        return "Auction multiple of an item";
    }

}
