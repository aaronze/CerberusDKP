package menu.loot;

import bot.Auctions;
import database.Table;
import ui.DiceMonitor;
import menu.Command;
import menu.User;
import static util.EverQuest.sendToEQ;

/**
 * @author Aaron
 */
public class DeclareAnotherWinner extends Command {

    @Override
    public void init() {
        setAccessLevelToSeniorOfficer();
    }

    @Override
    public void activate(User user) {
        Table winner = Auctions.getNextWinner();
                
        if (winner == null) {
            sendToEQ(DiceMonitor.CHANNEL + "No Bids.");
        } else {
            String name = winner.select("Name");
            String bid = winner.select("Bid");
            String ra = winner.select("RA");

            // Declare the winner
            sendToEQ(DiceMonitor.CHANNEL + "Gratz " + name + " on " + Auctions.nameOfItem + " for " + bid + " DKP, " + ra + "% RA!");

            // Add the won item to the database
            data.addItem(name, Auctions.nameOfItem, Integer.parseInt(bid));

            // Remove player from the database
            Auctions.auctions.removeEntry(name);
        }
    }

    @Override
    public String getDescription() {
        return "Declare another winner from the previous auction";
    }

}
