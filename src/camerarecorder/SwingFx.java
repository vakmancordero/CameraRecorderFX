package camerarecorder;

import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

public class SwingFx extends Application {
    
    public void start(Stage stage) {
        final SwingNode swingNode = new SwingNode();
        createAndSetSwingContent(swingNode);
        
        StackPane pane = new StackPane();
        pane.getChildren().add(swingNode);
        
        stage.setScene(new Scene(pane, 100, 50));
        stage.show();
    }
    
    private void createAndSetSwingContent(final SwingNode swingNode) {
        
        SwingUtilities.invokeLater(() -> {
            swingNode.setContent(new JButton("Click me!"));
        });
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}