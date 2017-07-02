package camerarecorder.beans;

import camerarecorder.utils.Camera;
import com.jfoenix.controls.JFXCheckBox;

public class CameraContainer {
    
    private Camera camera;
    private JFXCheckBox checkBox;
    
    public CameraContainer() {
        System.out.println("Default constructor");
    }
    
    public CameraContainer(Camera camera, JFXCheckBox checkBox) {
        this.checkBox = checkBox;
        this.camera = camera;
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