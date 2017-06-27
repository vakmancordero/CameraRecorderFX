package camerarecorder;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;


@SuppressWarnings("serial")
public class ManyCamerasAtOnceExample extends JFrame {
    
    private List<WebcamPanel> panels = new ArrayList<WebcamPanel>();
    
    public ManyCamerasAtOnceExample() {
        
        setLayout(new FlowLayout());
        
        for (Webcam webcam : Webcam.getWebcams()) {
            
            webcam.setViewSize(WebcamResolution.QVGA.getSize());
            WebcamPanel panel = new WebcamPanel(webcam, false);
            
            panel.setPreferredSize(new Dimension(640, 480));
            
            panels.add(panel);
            add(panel);
        }
        
        setTitle("Few Cameras At Once");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
        
        SwingUtilities.invokeLater(new Runnable() {
            
            @Override
            public void run() {
                for (WebcamPanel panel : panels) {
                    panel.start();
                }
            }
        });
    }
    
    public static void main(String[] args) {
        new ManyCamerasAtOnceExample();
    }
}
