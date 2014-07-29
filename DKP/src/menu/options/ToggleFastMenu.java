package menu.options;

import menu.Command;
import menu.User;

/**
 * @author Aaron
 */
public class ToggleFastMenu extends Command {
    private String text = "Turn Fast Menu On";
    
    @Override
    public void init() {
        setAccessLevelToUser();
        
        text = "Turn Fast Menu ";
        
        if (isFastMenu) 
            text += "Off";
        else
            text += "On";
    }

    @Override
    public void activate(User user) {
        isFastMenu = !isFastMenu;
        
        text = "Turn Fast Menu ";
        if (isFastMenu) 
            text += "Off";
        else
            text += "On";
        
        String reply = "Fast menu was turned ";
        if (isFastMenu)
            reply += "On";
        else
            reply += "Off";
        sendTell(user, reply);
    }

    @Override
    public String getDescription() {
        return text;
    }
    
}
