package camerarecorder;

import camerarecorder.utils.Camera;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.jfoenix.controls.JFXCheckBox;
import java.awt.Dimension;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javax.swing.SwingUtilities;

public class RecorderController implements Initializable {
    
    @FXML
    private BorderPane borderPane_0, borderPane_1, borderPane_2, borderPane_3;
    
    @FXML
    private ImageView webcamIV_0, webcamIV_1, webcamIV_2, webcamIV_3;
    
    @FXML
    private JFXCheckBox checkBox_0, checkBox_1, checkBox_2, checkBox_3;
    
    private List<ImageView> imageViews = new ArrayList<>();
    private List<BorderPane> borderPanes = new ArrayList<>();
    private List<JFXCheckBox> checkBoxes = new ArrayList<>();
    private List<CameraContainer> containers = new ArrayList<>();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        this.initLists();
        
        List<Webcam> webcams = Webcam.getWebcams();
        
        for (int i = 0; i < webcams.size(); i++) {
            
            if (i < 4) {
                
                Webcam webcam = webcams.get(i);
                BorderPane borderPane = this.borderPanes.get(i);
                JFXCheckBox checkBox = checkBoxes.get(i);
                
                final SwingNode swingNode = new SwingNode();
                
                WebcamPanel webcamPanel = new WebcamPanel(webcam, false);
                
                webcamPanel.setFPSDisplayed(true);
                webcamPanel.setDisplayDebugInfo(true);
                webcamPanel.setImageSizeDisplayed(true);
                webcamPanel.setPreferredSize(new Dimension(320, 240));
                
                swingNode.setContent(webcamPanel);
                
                CameraContainer cameraContainer = new CameraContainer(webcamPanel, checkBox);
                
                cameraContainer.getCheckBox().selectedProperty().addListener((observable, oldValue, newValue) -> {
                    if (!newValue) {
                        cameraContainer.getWebcamPanel().stop();
                    } else {
                        cameraContainer.getWebcamPanel().start();
                    }
                });
                
                this.containers.add(cameraContainer);
                
                SwingUtilities.invokeLater(() -> {
                    
                    borderPane.setCenter(swingNode);
                    
                    webcamPanel.getWebcam().setViewSize(WebcamResolution.VGA.getSize());
                        
                    webcamPanel.start();
                    
                });
                
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
        
        this.borderPanes.addAll(
                Arrays.asList(
                        this.borderPane_0,
                        this.borderPane_1,
                        this.borderPane_2,
                        this.borderPane_3
                )
        );
        
    }
    
    @FXML
    private void startRecord() {
        
        containers.forEach((cameraContainer) -> {
            cameraContainer.getWebcamPanel().start();
        });
        
    }
    
    @FXML
    private void stopRecord() {
        
        containers.forEach((cameraContainer) -> {
            cameraContainer.getWebcamPanel().stop();
        });
        
    }
    
    class CameraContainer {
        
        private WebcamPanel webcamPanel;
        private JFXCheckBox checkBox;
        
        CameraContainer() {
            System.out.println("Default constructor");
        }

        CameraContainer(WebcamPanel webcamPanel, JFXCheckBox checkBox) {
            this.checkBox = checkBox;
            this.webcamPanel = webcamPanel;
        }

        public JFXCheckBox getCheckBox() {
            return checkBox;
        }

        public void setCheckBox(JFXCheckBox checkBox) {
            this.checkBox = checkBox;
        }

        public WebcamPanel getWebcamPanel() {
            return webcamPanel;
        }

        public void setWebcamPanel(WebcamPanel webcamPanel) {
            this.webcamPanel = webcamPanel;
        }
        
    }
    
}