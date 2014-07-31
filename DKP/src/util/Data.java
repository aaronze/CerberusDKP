package util;

import database.Database;
import database.Table;
import deprecated.MainMenu;

/**
 * Contains all the databases used by the application.
 * Ensures each table is initialized before use.
 * 
 * @author Aaron
 */
public class Data {
    public static Table whitelist;
    public static Table alias;
    public static Table users;
    public static Table preferences;
    public static Table failedSQL;
    public static Table profiles;
    
    static {
        // Initialize the databases, if the database isn't found, create it.
        if ((alias = Database.getTable("alias")) == null) {
            alias = Database.addTable("alias");
            alias.addRows("Alias", "Name");
        }
        
        if ((whitelist = Database.getTable("whitelist")) == null) {
            whitelist = Database.addTable("whitelist");
            whitelist.addRows("Name");
        }
        
        if ((users = Database.getTable("users")) == null) {
            users = Database.addTable("users");
            users.addRows("Name", "Rank");
            users.addEntry("Vahsa", ""+MainMenu.SUPER_USER);
            users.addEntry("Druad", ""+MainMenu.SUPER_USER);
            users.addEntry("Raidbot", ""+MainMenu.SUPER_USER);
            users.addEntry("Methadone", ""+MainMenu.SUPER_USER);
            users.addEntry("Elanor", ""+MainMenu.SENIOR);
            users.addEntry("Aledark", ""+MainMenu.SUPER_USER);
            users.addEntry("Mayfaire", ""+MainMenu.SENIOR);
            users.addEntry("Thobmage", ""+MainMenu.SENIOR);
            users.addEntry("Stormfire", ""+MainMenu.SENIOR);
            users.addEntry("Kisokally", ""+MainMenu.SENIOR);
        }
        
        if ((preferences = Database.getTable("preferences")) == null) {
            preferences = Database.addTable("preferences");
            preferences.addRows("Name", "Option", "Value");
        }
        
        if ((failedSQL = Database.getTable("failedSQL")) == null) {
            failedSQL = Database.addTable("failedSQL");
            failedSQL.addRows("Type", "SQL");
        }
        
        if ((profiles = Database.getTable("profiles")) == null) {
            profiles = Database.addTable("profiles");
            profiles.addRows("Name", "Log");
        }
    }
}
