package camerarecorder.beans;

import java.io.File;
import java.time.LocalDateTime;

public class Video {
    
    private String name;
    private String date;
    private String time;
    private String format;
    
    private File file;

    public Video() {
        
    }
    
    public Video(String name, LocalDateTime dateTime, String format, File file) {
        this.name = name;
        this.date = dateTime.toLocalDate().toString();
        this.time = dateTime.toLocalTime().toString();
        this.format = format;
        this.file = file;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return "Video{" + "name=" + name + 
                        ", date=" + date + 
                        ", time=" + time + 
                        ", format=" + format + 
                        ", file=" + file + 
                '}';
    }
    
}
