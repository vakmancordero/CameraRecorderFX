package camerarecorder.utils;

import java.io.File;

/**
 *
 * @author VakSF
 */
public class FilesUtil {
    
    private final String path;
    
    public FilesUtil() {
        this.path = System.getProperty("user.home").concat("\\RecorderFX\\");
        
        this.buildPath();
    }
    
    private void buildPath() {
        
        File filesPath = new File(this.path);
        
        if (!filesPath.exists()) {
            
            if (filesPath.mkdir()) {
                
                System.out.println("FilesPath - Created!");
                
            } else {
                
                System.out.println("FilesPath - BAD!");
                
            }
            
        } else {
            
            System.out.println("FilesPath - Ok!");
            
        }
        
    }
    
    public File getDirectory() {
        return new File(this.path);
    }
    
    public String getPath() {
        return path;
    }
    
}
