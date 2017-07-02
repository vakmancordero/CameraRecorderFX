package camerarecorder.dialog.gallery;

import camerarecorder.beans.Video;
import camerarecorder.utils.FilesUtil;
import camerarecorder.utils.NamingUtil;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

public class GalleryController implements Initializable {
    
    @FXML
    private TableView<Video> videoTV;
    
    @FXML
    private TableColumn<Video, String> deleteTC;
    
    private ObservableList<Video> videoList = FXCollections.observableArrayList();
    
    private FilesUtil filesUtil;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        this.videoTV.setItems(this.videoList);
        
        this.filesUtil = new FilesUtil();
        
        for (File file : this.filesUtil.getDirectory().listFiles()) {
            
            String fileName = file.getName();
            
            Video video = new Video(
                    fileName, 
                    NamingUtil.getLocalDateTime(fileName), 
                    NamingUtil.getExtension(fileName),
                    file
            );
            
            this.videoList.add(video);
            
        }
        
        this.initTC();
        
    }
    
    private void initTC() {
        
        this.deleteTC.setCellFactory(new PropertyValueFactory("delete"));
        
        Callback<TableColumn<Video, String>, TableCell<Video, String>> cellFactory =
                (TableColumn<Video, String> value) -> {
                    
                    TableCell<Video, String> cell = new TableCell<Video, String>() {
                        
                        @Override
                        protected void updateItem(String item, boolean empty) {
                            
                            Button button = new Button("Eliminar");
                            
                            super.updateItem(item, empty);
                            
                            if (empty) {
                                
                                super.setGraphic(null);
                                
                                super.setText(null);
                                
                            } else {
                                
                                button.setOnAction((ActionEvent event) -> {
                                    
                                    Video video = getTableView().getItems().get(
                                            super.getIndex()
                                    );
                                    
                                    File file = video.getFile();
                                    
                                    if (file.exists()) {
                                        
                                        try {
                                            Desktop.getDesktop().open(file);
                                            
//                                        if (file.delete()) {
//                                            
//                                            videoList.remove(video);
//                                            
//                                            new Alert(
//                                                    AlertType.INFORMATION,
//                                                    "El vídeo ha sido removido exitosamente"
//                                            ).show();
//                                            
//                                        } else {
//                                            
//                                            new Alert(
//                                                    AlertType.ERROR,
//                                                    "El vídeo no ha podido ser eliminado"
//                                            ).show();
//                                            
//                                        }
                                        } catch (IOException ex) {
                                            
                                        }
                                        
                                    }
                                    
                                });
                                
                                super.setGraphic(button);
                                
                                super.setAlignment(Pos.CENTER);
                                
                                super.setText(null);
                                
                            }
                            
                        }
                        
                    };
                    
                    return cell;
                    
                };
        
        this.deleteTC.setCellFactory(cellFactory);
        
    }
    
}
