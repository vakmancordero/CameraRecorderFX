package camerarecorder.utils;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import javafx.concurrent.Task;

/**
 *
 * @author VakSF
 */
public class Camera extends WebcamPanel {

    private volatile boolean recording = false;
    
    private Webcam webcam;
    private BufferedImage obtainedImage;
    private IMediaWriter writer;
    
    private final FilesUtil filesUtil;
    private File file;
    
    public Camera(Webcam webcam, boolean start) {
        
        super(webcam, start);
        
        setFPSDisplayed(true);
        setDisplayDebugInfo(true);
        setImageSizeDisplayed(true);
        
        this.webcam = webcam;
        this.webcam.setViewSize(WebcamResolution.VGA.getSize());
        
        this.filesUtil = new FilesUtil();
    }
    
    private synchronized void initWriter(String fileName) {
        
        this.file = new File(this.filesUtil.getPath() + fileName);
        this.writer = ToolFactory.makeWriter(this.file.getAbsolutePath());
        
        Dimension size = WebcamResolution.VGA.getSize();
        this.writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG4, size.width, size.height);
        
    }
    
    private synchronized void record(BufferedImage image, long start) {
        
        BufferedImage bufferedImage = ConverterFactory.convertToType(image, BufferedImage.TYPE_3BYTE_BGR);
        IConverter converter = ConverterFactory.createConverter(bufferedImage, IPixelFormat.Type.YUV420P);
        
        IVideoPicture frame = converter.toPicture(bufferedImage, (System.currentTimeMillis() - start) * 1000);
        frame.setQuality(0);
        
        this.writer.encodeVideo(0, frame);
        
    }
    
    public synchronized void startRecord() {
        
        this.initWriter(NamingUtil.getVideoName(".mp4"));
        
        this.recording = true;
        
        Task<Void> recordTask = new Task<Void>() {
            
            @Override
            protected Void call() throws Exception {
                
                long start = System.currentTimeMillis();
                
                while (recording) {
                    
                    try {
                        
                        obtainedImage = webcam.getImage();
                        
                        if (obtainedImage != null) {
                            
                            record(obtainedImage, start);
                            
                        }
                        
                        obtainedImage.flush();
                        
                    } catch (Exception ex) {
                        System.out.println("Error: " + ex.toString());
                    }
                    
                }
                
                writer.close();
                
                return null;
            }
            
        };
        
        Thread thread = new Thread(recordTask);
        thread.setDaemon(true);
        thread.start();
        
    }
    
    public synchronized void stopRecord() {
        this.recording = false;
    }
    
}
