package camerarecorder.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author VakSF
 */
public class NamingUtil {
    
    public static String getVideoName(String extension) {
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy-MM-dd HH-mm-ss.SSS");
        
        return formatter.format(LocalDateTime.now()).concat(extension);
        
    }
    
}
