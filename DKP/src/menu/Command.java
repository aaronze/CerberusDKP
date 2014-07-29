package menu;

import java.util.ArrayList;
import util.SQL;

/**
 * @author Aaron
 */
public abstract class Command extends Menu {
    public final ArrayList<String> parameters = new ArrayList<>();
    private final ArrayList<String> inputs = new ArrayList<>();
    private int parameterIndex = 0;
    private int inputIndex= 0;

    public Command() {
        super(new User(""));
    }
    
    public Command(User user) {
        super(user);
    }
    
    public abstract void init();
    public abstract void activate(User user);
    
    public void clear() {
        inputs.clear();
        parameterIndex = 0;
        inputIndex = 0;
    }
    
    public void add(String parameter) {
        parameters.add(parameter);
    }
    
    public String getNextParameter() {
        if (parameters.isEmpty())
            return "";
        
        String param = parameters.get(parameterIndex++);
        if (parameterIndex >= parameters.size())
            parameterIndex = 0;
        
        return param;
    }
    
    /**
     * Inputs data into the command.
     * 
     * Returns true when command is full of requested parameters
     * 
     * @param in
     * @return 
     */
    public boolean input(String in) {
        inputs.add(in);
        
        return inputs.size() == parameters.size();
    }
    
    public String getNextInput() {
        if (inputs.isEmpty())
            return "";
        
        String input = inputs.get(inputIndex++);
        if (inputIndex >= inputs.size())
            inputIndex = 0;
        
        return input;
    }
    
    public int getNextInt() {
        String input = getNextInput();
        
        try {
            return Integer.parseInt(input);
        } catch (Exception e) {
        }
        
        return 0;
    }
    
    public String getNextName() {
        return SQL.getAlias(getNextInput());
    }
}
