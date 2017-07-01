package camerarecorder.utils;

import com.github.sarxos.webcam.Webcam;
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
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

/**
 *
 * @author VakSF
 */
public class Camera {

    private volatile boolean stopCamera = false;
    private volatile boolean recording = false;
    
    private Webcam selWebCam;
    private BufferedImage grabbedImage;
    private IMediaWriter writer;
    
    private final FilesUtil filesUtil;
    private File file;
    
    private ObjectProperty<Image> imageProperty = new SimpleObjectProperty<>();
    
    public Camera(Webcam webcam) {
        
        this.selWebCam = webcam;
        
        this.filesUtil = new FilesUtil();
        this.initializeWebCam();
    }

    public ObjectProperty<Image> imageProperty() {
        return imageProperty;
    }
    
    private synchronized void initWriter(String fileName) {
        
        this.file = new File(this.filesUtil.getPath() + fileName);
        
        this.writer = ToolFactory.makeWriter(this.file.getAbsolutePath());
        
        Dimension size = WebcamResolution.VGA.getSize();
        
        this.writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG4, size.width, size.height);
        
    }
    
    public synchronized void initializeWebCam() {
        
        Task<Void> webCamInitializer = new Task<Void>() {
            
            @Override
            protected Void call() throws Exception {
                
//                Dimension[] nonStandardResolutions = new Dimension[] {
//                    WebcamResolution.PAL.getSize(),
//                    WebcamResolution.VGA.getSize(),
//                    new Dimension(2000, 1000),
//                    new Dimension(1000, 500),
//                };
//                
//                selWebCam.setCustomViewSizes(nonStandardResolutions);
                selWebCam.setViewSize(WebcamResolution.VGA.getSize());
                
                selWebCam.open();
                
                startWebCamStream();
                
                return null;
            }
            
        };
        
        new Thread(webCamInitializer).start();
    }
    
    private synchronized void startWebCamStream() {
        
        this.stopCamera = false;
        
        Task<Void> task = new Task<Void>() {
            
            @Override
            protected Void call() throws Exception {
                
                long start = System.currentTimeMillis();
                
                while (!stopCamera) {
                    
                    try {
                        
                        grabbedImage = selWebCam.getImage();
                        
                        if (grabbedImage != null) {
                            
                            Platform.runLater(() -> {
                                
                                imageProperty.set(
                                        SwingFXUtils.toFXImage(grabbedImage, null));
                                
                            });
                            
                            grabbedImage.flush();
                            
                            Thread.sleep(20);

                        }
                        
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    
                }
                
                writer.close();
                
                return null;
            }
            
        };
        
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
        
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
                
                while (recording && selWebCam.isOpen()) {
                    
                    try {
                        
                        if (grabbedImage != null) {
                            
                            record(grabbedImage, start);
                            
                        }
                        
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
    
    public synchronized void stopCamera() {
        this.stopCamera = true;
        this.selWebCam.close();
    }
    
}
