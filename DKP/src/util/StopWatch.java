package util;

import java.util.Date;

/**
 * @author Aaron
 */
public class StopWatch {
    private long startTime;
    
    public StopWatch() {
        startTime = new Date().getTime();
    }
    
    public double stop() {
        long dif = new Date().getTime() - startTime;
        
        double secs = dif / 1000.0;
        
        return secs;
    }
    
    public void restart() {
        startTime = new Date().getTime();
    }
}
