package util;

import java.util.Stack;

/**
 * Parses strings into separate strings based on a key and adds the values to the stack.
 * 
 * @author Aaron
 */
public class StringParser {
    private final static Stack<String> stack = new Stack<>();
    
    public static boolean split(String s, String key) {
        if (hasKey(s, key)) {
            int ind = s.toLowerCase().indexOf(key.toLowerCase());
            
            stack.add(s.substring(0, ind));
            stack.add(s.substring(ind + key.length()));
            
            return true;
        } 
        
        return false;
    }
    
    public static boolean splitAfter(String s, String key) {
        if (hasKey(s, key)) {
            int ind = s.toLowerCase().indexOf(key.toLowerCase());
            
            stack.add(s.substring(ind + key.length()));
            
            return true;
        } 
        
        return false;
    }
    
    public static boolean splitBefore(String s, String key) {
        if (hasKey(s, key)) {
            int ind = s.toLowerCase().indexOf(key.toLowerCase());
            
            stack.add(s.substring(0, ind));
            
            return true;
        } 
        
        return false;
    }
    
    public static boolean hasKey(String s, String key) {
        return s.toLowerCase().contains(key.toLowerCase());
    }
    
    public static String getString() {
        return stack.pop();
    }
}
