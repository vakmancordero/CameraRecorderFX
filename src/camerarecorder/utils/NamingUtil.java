package camerarecorder.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author VakSF
 */
public class NamingUtil {
    
    public static String getVideoName(String extension) {
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy-MM-dd HH-mm-ss.SSS");
        
        return formatter.format(LocalDateTime.now()).concat(extension);
        
    }
    
    public static LocalDateTime getLocalDateTime(String fileName) {
        
        String withOutExt = FilenameUtils.removeExtension(fileName);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy-MM-dd HH-mm-ss.SSS");
        
        return LocalDateTime.parse(withOutExt, formatter);
        
    }
    
    public static String getExtension(String fileName) {
        return FilenameUtils.getExtension(fileName);
    }
    
}
