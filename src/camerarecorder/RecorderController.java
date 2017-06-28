package camerarecorder;

import camerarecorder.utils.FilesUtil;
import camerarecorder.utils.NamingUtil;
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
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class RecorderController implements Initializable {
    
    
    @FXML
    private ImageView webcamIV_0, webcamIV_1, webcamIV_2, webcamIV_3;
    
    private ObjectProperty<Image> imageProperty = new SimpleObjectProperty<>();
    
    private Webcam selWebCam;
    private BufferedImage grabbedImage;
    private boolean stopCamera = false;
    
    private File file;
    private IMediaWriter writer;
    private FilesUtil filesUtil;
    
    private volatile boolean recording = false;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        this.filesUtil = new FilesUtil();
        
        this.initializeWebCam();
        
    }
    
    private void initWriter(String fileName) {
        
        this.file = new File(this.filesUtil.getPath() + fileName);
        
        this.writer = ToolFactory.makeWriter(this.file.getAbsolutePath());
        
        Dimension size = WebcamResolution.VGA.getSize();
        
        this.writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG4, size.width, size.height);
        
    }
    
    private void initializeWebCam() {
        
        Task<Void> webCamInitializer = new Task<Void>() {
            
            @Override
            protected Void call() throws Exception {
                
                if (selWebCam == null) {
                    
                    selWebCam = Webcam.getWebcams().get(0);
                    
                    Dimension[] nonStandardResolutions = new Dimension[] {
                        WebcamResolution.PAL.getSize(),
                        WebcamResolution.VGA.getSize(),
                        new Dimension(2000, 1000),
                        new Dimension(1000, 500),
                    };
                    
                    selWebCam.setCustomViewSizes(nonStandardResolutions);
                    selWebCam.setViewSize(WebcamResolution.VGA.getSize());
                    
                    selWebCam.open();
                    
                } else {
                    
                    selWebCam = Webcam.getWebcams().get(0);
                    selWebCam.open();
                    
                }
                
                startWebCamStream();
                
                return null;
            }
            
        };
        
        new Thread(webCamInitializer).start();
    }
    
    private void startWebCamStream() {
        
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
        
        this.webcamIV_0.imageProperty().bind(this.imageProperty);
        this.webcamIV_1.imageProperty().bind(this.imageProperty);
        this.webcamIV_2.imageProperty().bind(this.imageProperty);
        this.webcamIV_3.imageProperty().bind(this.imageProperty);
        
    }
    
    private synchronized void record(BufferedImage image, long start) {
        
        BufferedImage bufferedImage = ConverterFactory.convertToType(image, BufferedImage.TYPE_3BYTE_BGR);
        IConverter converter = ConverterFactory.createConverter(bufferedImage, IPixelFormat.Type.YUV420P);
        
        IVideoPicture frame = converter.toPicture(bufferedImage, (System.currentTimeMillis() - start) * 1000);
        frame.setQuality(0);
        
        this.writer.encodeVideo(0, frame);
        
    }
    
    @FXML
    private void startRecord() {
        
        this.initWriter(NamingUtil.getVideoName(".mp4"));
        
        this.recording = true;
        
        Task<Void> recordTask = new Task<Void>() {
            
            @Override
            protected Void call() throws Exception {
                
                long start = System.currentTimeMillis();
                
                while (recording) {
                    
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
    
    @FXML
    private void stopRecord() {
        this.recording = false;
    }
    
    @FXML
    private void stopCamera(ActionEvent event) {
        this.stopCamera = true;
    }
    
}
