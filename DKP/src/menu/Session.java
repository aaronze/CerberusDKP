package menu;

import java.util.ArrayList;
import java.util.Scanner;
import util.EverQuest;

/**
 * @author Aaron
 */
public class Session {
    private static final ArrayList<Session> sessions = new ArrayList<>();
    
    private final User user;
    private final Menu MAIN_MENU;
    private Menu menu;
    
    public static void main(String[] args) {
        Session session = new Session("Druad");
        
        Scanner kb = new Scanner(System.in);
        
        while (kb.hasNextLine()) {
            session.parse(kb.nextLine());
        }
    }
    
    public static void reset() {
        for (Session s : sessions) {
            s.menu.data.closeConnection();
            s.menu.user = null;
            
            s.MAIN_MENU.data.closeConnection();
            s.MAIN_MENU.user = null;
        }
    }
    
    public static Session getSession(String name) {
        for (Session s : sessions) {
            if (s.user.name.equalsIgnoreCase(name))
                return s;
        }
        
        Session session = new Session(name);
        
        sessions.add(session);
        
        return session;
    }
    
    private Session(String name) {
        user = new User(name);
        
        MAIN_MENU = new MainMenu(user);
        menu = MAIN_MENU;
    }
    
    /**
     * Deals with user input that they send to Raidbot.
     * 
     * @param s User input
     * @return Response to user input
     */
    public String parse(String s) {
        if (s.isEmpty() || s.contains("/tell "))
            return "";
        
        // Return the user to the main menu
        if (s.equalsIgnoreCase("m") || s.toLowerCase().contains("menu")) {
            menu = MAIN_MENU;
        }
        
        // If the user has previously selected a command, reroute their input through the command system
        if (menu instanceof Command) {
            Command command = (Command)menu;

            if (command.input(s)) {
                // Command is ready to be executed
                command.activate(user);
                menu = MAIN_MENU;
            } else {
                askForParam();
            }
            return "";
        }

        // Grant the user temporary access to everything if they use "sudo su"
        if (s.toLowerCase().contains("sudo su")) {
            user.isSudoSu = !user.isSudoSu;
            EverQuest.sendTell(user.name, "You are the Batman");
            return "";
        }
        
        int option;
        if ((option = getInt(s)) != 0) {
            Menu command = menu.subMenus.get(option-1);
            
            if (command instanceof Command) {
                ((Command)command).init();
            }

            if (menu.hasPriveledges(user, command.priveledgeLevel)) {
                menu = command;

                if (menu instanceof Command) {
                    // If command requires no parameters, activate immediately
                    if (((Command)command).parameters.isEmpty()) {
                        ((Command)command).activate(user);
                        menu = MAIN_MENU;
                    } else {
                        // Request a param
                        askForParam();
                    }
                    
                    return "";
                }
            }
        }
        
        menu.speak();
        
        return s;
    }
    
    private void askForParam() {
        sendTell(user, ((Command)menu).getNextParameter());
    }
    
    private int getInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
        }
        return 0;
    }
    
    private void sendTell(User user, String message) {
        EverQuest.sendTell(user.name, message);
    }
}
