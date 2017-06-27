package camerarecorder;

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
    private ImageView webcamIV;
    
    private ObjectProperty<Image> imageProperty = new SimpleObjectProperty<>();
    
    private Webcam selWebCam;
    private BufferedImage grabbedImage;
    private boolean stopCamera = false;
    
    private File file;
    private IMediaWriter writer;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        this.file = new File("output.mp4");
        
        this.writer = ToolFactory.makeWriter(file.getName());
        
        Dimension size = WebcamResolution.VGA.getSize();
        
        this.writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG4, size.width, size.height);
        
    }
    
    @FXML
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
    
    protected void startWebCamStream() {
        
        this.initialize(null, null);
        
        this.stopCamera = false;
        
        Task<Void> task = new Task<Void>() {
            
            @Override
            protected Void call() throws Exception {
                
                long start = System.currentTimeMillis();
                
                while (!stopCamera) {
                    
                    try {
                        
                        if ((grabbedImage = selWebCam.getImage()) != null) {
                            
                            Platform.runLater(() -> {
                                
                                imageProperty.set(
                                        SwingFXUtils.toFXImage(grabbedImage, null));
                                
                            });
                            
                            BufferedImage image = ConverterFactory.convertToType(grabbedImage, BufferedImage.TYPE_3BYTE_BGR);
                            IConverter converter = ConverterFactory.createConverter(image, IPixelFormat.Type.YUV420P);
                            
                            IVideoPicture frame = converter.toPicture(image, (System.currentTimeMillis() - start) * 1000);
                            frame.setQuality(0);

                            writer.encodeVideo(0, frame);
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
        
        this.webcamIV.imageProperty().bind(this.imageProperty);
        
    }
    
    @FXML
    private void stopCamera(ActionEvent event) {
        this.stopCamera = true;
    }
    
}
