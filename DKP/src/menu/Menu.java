package menu;

import util.Data;
import java.util.ArrayList;
import util.EverQuest;
import util.SQL;

/**
 * @author Aaron
 */
public abstract class Menu {
    public final static int SILENCED =      0x0000;
    public final static int USER =          0x000F;
    public final static int OFFICER =       0x00FF;
    public final static int SENIOR =        0x0FFF;
    public final static int SUPER_USER =    0xFFFF;
    
    public final static int DEFAULT = USER;
    
    private Menu MAIN_MENU;
    final ArrayList<Menu> subMenus = new ArrayList<>();
    int priveledgeLevel = DEFAULT;
    private boolean isSudoSu = false;
    private String text;
    protected SQL data;
    public User user;
    protected boolean isFastMenu = false;
    
    public Menu(User user) {
        data = new SQL();
        this.user = user;
    }
    
    protected void speak() {
        // Speak header
        if (!isFastMenu) {
            String line = "| " + getDescription()+ " |";

            String dashes = "";
            for (int i = 0; i < line.length()*1.5; i++) {
                dashes += "-";
            }
            
            int dkp = data.getDKP(user.name);
            int ra = data.getRA(user.name);

            sendTell(user, dashes);
            sendTell(user, line);
            if (this.equals(MAIN_MENU)) {
                String dkpString = "| DKP: " + dkp;
                String raString = "| RA : " + ra + "%";
                
                sendTell(user, dkpString);
                sendTell(user, raString);
            }
            sendTell(user, dashes);
        }
        
        // Speak content
        if (isFastMenu) {
            String s = getDescription();
            for (int i = 0; i < subMenus.size(); i++)
                s += "\t| " + (i+1) + ". " + subMenus.get(i).getDescription();
            sendTell(user, s);
        } else {
            for (int i = 0; i < subMenus.size(); i++) {
                sendTell(user, "| " + (i+1) + ". " + subMenus.get(i).getDescription());
            }
        }
    }
    
    public ArrayList<Menu> getSubMenus() {
        return subMenus;
    }
    
    public final void add(Menu menu) {
        subMenus.add(menu);
    }
    
    public void setPriveledgeLevel(int priv) {
        priveledgeLevel = priv;
    }
    
    public void setAccessLevelToUser() {
        priveledgeLevel = USER;
    }
    
    public void setAccessLevelToOfficer() {
        priveledgeLevel = OFFICER;
    }
    
    public void setAccessLevelToSeniorOfficer() {
        priveledgeLevel = SENIOR;
    }
    
    public void setAccessLevelToSuperUser() {
        priveledgeLevel = SUPER_USER;
    }
    
    public void sendTell(User user, String message) {
        EverQuest.sendTell(user.name, message);
    }
    
    public boolean isAUser(User user) {
        return hasPriveledges(user, USER);
    }
    
    public boolean isAnOfficer(User user) {
        return hasPriveledges(user, OFFICER);
    }
    
    public boolean isASeniorOfficer(User user) {
        return hasPriveledges(user, SENIOR);
    }
    
    public boolean isASuperUser(User user) {
        return hasPriveledges(user, SUPER_USER);
    }
    
    boolean hasPriveledges(User user, int priveledges) {
        if (isSudoSu) return true;
        
        int rank = getPriveledges(user);
        
        return (rank & priveledges) == priveledges;
    }
    private int getUserPriveledges(User user) {
        if (isSudoSu) return SUPER_USER;
        
        return getPriveledges(user);
    }
    private int getPriveledges(User user) {
        String strRank = Data.users.where("Name", SQL.getAlias(user.name)).select("Rank");
        
        int rank = DEFAULT;
        if (!strRank.isEmpty()) rank = Integer.parseInt(strRank);
        
        return rank;
    }
    protected boolean setPriveledges(User user, String name, int priveledges) {
        if (!isSudoSu) {
            if (getPriveledges(new User(name)) >= getUserPriveledges(user)) {
                sendTell(user, "You can only alter the access level of players who do not out-rank you.");
                return false;
            }

            if (getUserPriveledges(user) < priveledges) {
                sendTell(user, "You can not promote player's access level to above your own");
                return false;
            }
        }
        
        
        if (Data.users.where("Name", name).isEmpty()) {
            Data.users.addEntry(name, ""+priveledges);
        } else {
            Data.users.updateEntry("Name", name, "Rank", ""+priveledges);
        }
        
        return true;
    }
    
    public abstract String getDescription();
}
