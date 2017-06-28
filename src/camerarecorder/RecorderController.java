package camerarecorder;

import camerarecorder.utils.Camera;
import com.github.sarxos.webcam.Webcam;
import com.jfoenix.controls.JFXCheckBox;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;

public class RecorderController implements Initializable {
    
    @FXML
    private ImageView webcamIV_0, webcamIV_1, webcamIV_2, webcamIV_3;
    
    @FXML
    private JFXCheckBox checkBox_0, checkBox_1, checkBox_2, checkBox_3;
    
    private List<ImageView> imageViews = new ArrayList<>();
    private List<JFXCheckBox> checkBoxes = new ArrayList<>();
    private List<CameraContainer> containers = new ArrayList<>();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        this.initLists();
        
        List<Webcam> webcams = Webcam.getWebcams();
        
        for (int i = 0; i < webcams.size(); i++) {
            
            if (i < 4) {
                
                Camera camera = new Camera(webcams.get(i));
                
                ImageView imageView = imageViews.get(i);
                JFXCheckBox checkBox = checkBoxes.get(i);
                
                imageView.imageProperty().bind(camera.imageProperty());
                
                CameraContainer cameraContainer = new CameraContainer(imageView, checkBox, camera);
                
                cameraContainer.getCheckBox().selectedProperty().addListener((observable, oldValue, newValue) -> {
                    if (!newValue) {
                        cameraContainer.getCamera().stopCamera();
                    } else {
                        cameraContainer.getCamera().initializeWebCam();
                    }
                });
                
                containers.add(cameraContainer);
                
            }
            
        }
        
    }
    
    private void initLists() {
        
        this.imageViews.addAll(
                Arrays.asList(
                        this.webcamIV_0, this.webcamIV_1,
                        this.webcamIV_2, this.webcamIV_3
                )
        );
        
        this.checkBoxes.addAll(
                Arrays.asList(
                        this.checkBox_0, this.checkBox_1,
                        this.checkBox_2, this.checkBox_3
                )
        );
        
    }
    
    @FXML
    private void startRecord() {
        
        for (CameraContainer cameraContainer : containers) {
            
            cameraContainer.getCamera().startRecord();
            
        }
        
    }
    
    @FXML
    private void stopRecord() {
        
        for (CameraContainer cameraContainer : containers) {
            
            cameraContainer.getCamera().stopRecord();
            
        }
        
    }
    
    class CameraContainer {
        
        private ImageView imageView;
        private JFXCheckBox checkBox;
        private Camera camera;
        
        CameraContainer() {
            
        }

        CameraContainer(ImageView imageView, JFXCheckBox checkBox, Camera camera) {
            this.imageView = imageView;
            this.checkBox = checkBox;
            this.camera = camera;
        }

        public ImageView getImageView() {
            return imageView;
        }

        public void setImageView(ImageView imageView) {
            this.imageView = imageView;
        }

        public JFXCheckBox getCheckBox() {
            return checkBox;
        }

        public void setCheckBox(JFXCheckBox checkBox) {
            this.checkBox = checkBox;
        }

        public Camera getCamera() {
            return camera;
        }

        public void setCamera(Camera camera) {
            this.camera = camera;
        }
        
    }
    
}