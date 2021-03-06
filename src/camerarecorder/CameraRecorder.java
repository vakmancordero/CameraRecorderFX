package camerarecorder;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CameraRecorder extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        
        Parent root = FXMLLoader.load(getClass().getResource("RecorderFXML.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setOnCloseRequest((event) -> {
            
            System.exit(0);
            
        });
        
        stage.setTitle("Camera Recorder");
        
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
