package secure;

import database.Cache;
import database.Database;
import database.Table;
import java.io.File;
import java.util.Date;

/**
 * This class claims responsibility for account operations.
 * 
 * Must be secure against tampering and many forms of data loss.
 * 
 * @author Aaron
 */
public class Account {
    private final static String EVENT_ACCOUNT_CREATED = "Account Created";
    private final static String EVENT_DEPOSIT = "Desposited Plat";
    private final static String EVENT_WITHDRAW = "Withdrawed Plat";
    private final static String EVENT_REMOVE_ALL_PLAT = "All plat was removed";
    private final static String EVENT_ACCOUNT_BANNED = "Account Banned";
    
    /**
     * Table containing account transaction logs for the user
     */
    private Table account;
    
    /**
     * Name of the user's account
     */
    private final String name;
    
    /**
     * Current ban status of this account
     */
    private boolean isBanned = false;

    /**
     * Constructs the account with the given name.
     * 
     * @param name 
     */
    public Account(String name) {
        this.name = name;
        File folder = new File("accounts/");
        if (!folder.isDirectory()) folder.mkdir();
        
        account = Database.getTable("accounts/"+name);
        if (account == null) {
            account = Database.addTable("accounts/"+name);
            account.addRows("Timestamp", "Source", "Event", "Data");
            
            account.addEntry(new Date().toString(), "DiceMonitor", EVENT_ACCOUNT_CREATED, "Account Created");
        }
    }
    
    /**
     * Returns the current balance of the account.
     * 
     * @return 
     */
    public long getBalance() {
        if (isBanned) return 0;
        
        try {
            String cacheBalance = Cache.get("Account"+name);

            if (cacheBalance.isEmpty()) {
                long balance = 0;
                
                int eventIndex = account.getRowIndex("Event");
                int dataIndex = account.getRowIndex("Data");
                
                for (int i = 0; i < account.size(); i++) {
                    try {
                        String[] entry = account.getEntry(i);

                        String event = entry[eventIndex];
                        String data = entry[dataIndex];

                        switch (event) {
                            case EVENT_DEPOSIT:
                                balance += Long.parseLong(data);
                                break;
                            case EVENT_WITHDRAW:
                                balance -= Long.parseLong(data);
                                break;
                            case EVENT_REMOVE_ALL_PLAT:
                                balance = 0;
                                break;
                            case EVENT_ACCOUNT_BANNED:
                                isBanned = true;
                                return 0;
                        }
                        
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                
                return balance;
            } else {
                return Long.parseLong(cacheBalance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    public boolean deposit(String source, long amount) {
        // Validate amount
        if (amount <= 0) return false;
        
        if (isBanned) return false;
        
        try {
            // Get current amount
            long balance = getBalance();

            // Deposite the amount into the account
            account.addEntry(new Date().toString(), source, EVENT_DEPOSIT, ""+amount);

            // Update cache
            Cache.add("Account"+name, ""+(balance + amount), Cache.WEEK);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        
        return true;
    }
    
    public boolean withdraw(String destination, long amount) {
        // Validate amount
        if (amount <= 0) return false;
        
        if (isBanned) return false;
        
        try {
            // Get current amount
            long balance = getBalance();

            // Can only withdraw if sufficient balance
            if (balance < amount) return false;
            
            // Deposite the amount into the account
            account.addEntry(new Date().toString(), destination, EVENT_WITHDRAW, ""+amount);

            // Update cache
            Cache.add("Account"+name, ""+(balance - amount), Cache.WEEK);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        
        return true;
    }
    
    public void clearBalance(String adminName) {
        account.addEntry(new Date().toString(), adminName, EVENT_REMOVE_ALL_PLAT, "Balance set to 0");
        
        // Update cache
        Cache.add("Account"+name, "0", Cache.WEEK);
    }
    
    public void banAccount(String adminName, String reason) {
        isBanned = true;
        
        account.addEntry(new Date().toString(), adminName, EVENT_ACCOUNT_BANNED, "Reason Given: " + reason);
    }
}
