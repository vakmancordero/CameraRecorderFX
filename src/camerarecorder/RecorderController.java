package camerarecorder;

import camerarecorder.beans.CameraContainer;
import camerarecorder.dialog.gallery.GalleryController;
import camerarecorder.utils.Camera;
import com.github.sarxos.webcam.Webcam;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javax.swing.SwingUtilities;

public class RecorderController implements Initializable {
    
    @FXML
    private BorderPane borderPane_0, borderPane_1, borderPane_2, borderPane_3;
    
    @FXML
    private JFXCheckBox checkBox_0, checkBox_1, checkBox_2, checkBox_3;
    
    @FXML
    private JFXButton recordButton, stopButton, galleryButton;
    
    private final List<BorderPane> borderPanes = new ArrayList<>();
    private final List<JFXCheckBox> checkBoxes = new ArrayList<>();
    private final List<CameraContainer> containers = new ArrayList<>();
    
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
                
                Camera camera = new Camera(webcam, false);
                
                swingNode.setContent(camera);
                
                CameraContainer cameraContainer = new CameraContainer(camera, checkBox);
                
                this.initWebcam(cameraContainer, borderPane, swingNode, camera);
            }
            
        }
        
        this.stopButton.setDisable(true);
        
    }
    
    private void initWebcam(CameraContainer cameraContainer, BorderPane borderPane,  
            SwingNode swingNode, Camera camera) {
        
        final Task<Void> webCamIntilizer = new Task<Void>() {
            
            @Override
            protected Void call() throws Exception {
                
                cameraContainer.getCheckBox().setDisable(false);
                cameraContainer.getCheckBox().setSelected(true);
                
                cameraContainer.getCheckBox().selectedProperty().addListener((observable, oldValue, newValue) -> {
                    
                    Platform.runLater(() -> {
                        
                        if (!newValue) {
                            
                            cameraContainer.getCamera().pause();
                            cameraContainer.getCamera().setVisible(false);
                            
                        } else {
                            
                            cameraContainer.getCamera().resume();
                            cameraContainer.getCamera().setVisible(true);
                            
                        }
                    });
                    
                    checkCheckboxes();
                    
                });
                
                containers.add(cameraContainer);
                   
                SwingUtilities.invokeLater(() -> {
                    
                    Platform.runLater(() -> {
                        
                        borderPane.setCenter(swingNode);
                        
                        for (BorderPane pane : borderPanes) {
                            
                            Node center = pane.getCenter();
                            
                            String name = center.getClass().getSimpleName();
                            
                            if (name.equals("Label")) {
                                
                                Label label = (Label) center;
                                
                                label.setText("No encontrado");
                                
                            }
                            
                        }
                        
                        camera.start();
                        
                    });
                    
                });
                
                return null;
            }
            
        };
        
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(webCamIntilizer);
        
    }
    
    private void checkCheckboxes() {
        
        int counter = 0;
        
        for (CameraContainer container : this.containers)
            if (container.getCheckBox().isSelected()) counter ++;
        
        this.recordButton.setDisable(counter == 0);
        
    }
    
    private void initLists() {
        
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
        
        this.recordButton.setDisable(true);
        this.stopButton.setDisable(false);
        
        containers.forEach((cameraContainer) -> {
            
            if (cameraContainer.getCheckBox().isSelected()) {
                
                cameraContainer.getCamera().startRecord();
                
                cameraContainer.getCheckBox().setDisable(true);
                
            }
            
        });
        
    }
    
    @FXML
    private void stopRecord() {
        
        this.recordButton.setDisable(false);
        
        containers.forEach((cameraContainer) -> {
            
            cameraContainer.getCamera().stopRecord();
            
            cameraContainer.getCheckBox().setDisable(false);
            
        });
        
    }
    
    @FXML
    private void openGallery() throws IOException {
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource(
                "/camerarecorder/dialog/gallery/GalleryFXML.fxml"
        ));
        
        Stage stage = new Stage();
        stage.setScene(new Scene((Pane) loader.load()));
        
        stage.setTitle("Galería actual");
        
        GalleryController galleryController =
                loader.<GalleryController>getController();
        
        stage.showAndWait();
        
    }
    
}