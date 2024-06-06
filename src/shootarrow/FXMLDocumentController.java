/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shootarrow;

import static java.lang.Math.round;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.animation.PathTransition;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javax.swing.JLabel;
import static shootarrow.Shootarrow.scene;

/**
 *
 * @author Administrator
 */
import static shootarrow.Shootarrow.scene;

public class FXMLDocumentController implements Initializable {
    double x = 0.0;
    double y = 0.0;
    double t = 0.0;
    int k = 0;
    int duration = 0;
    

    ArrayList object = new ArrayList();
    int[] coor = new int[2];
    public static final double ACCELERATION = -9.81;
    @FXML
    private Label label;
    private Button button;
    @FXML
    private Image image = new Image("arrow.jpg");
    @FXML
    private ImageView iv1 = new ImageView();
    @FXML
    private Text status;
    
   

    @FXML
    private void handleButtonAction(ActionEvent event) {
        
        label.setText("==>");
        
        x = 0.0;
        y = 0.0;
        t = 0.0;
        Path path = new Path();
        path.getElements().add(new MoveTo((int) (x + 50), (int) (400 - y)));
        k++;
        double velocity = 59 + k * 10;

        double angle = Math.toRadians(55);

        int steps = 50;

        double xVelocity = velocity * Math.cos(angle);
        double yVelocity = velocity * Math.sin(angle);
        double totalTime = -2.0 * yVelocity / ACCELERATION;
        double timeIncrement = totalTime / steps;
        double xIncrement = xVelocity * timeIncrement;

        System.out.println("step\tx\ty\ttime");
        System.out.println("0\t0.0\t0.0\t0.0");

        for (int i = 1; i <= steps; i++) {

            t += timeIncrement;
            x += xIncrement;
            y = yVelocity * t + 0.5 * ACCELERATION * t * t;
            path.getElements().add(new LineTo((int) (x + 50), (int) (400 - y)));
            duration++;
            //Thread.sleep(1000 / 30);
            System.out.println(i + "\t" + round(x) + "\t" + round(y) + "\t" + round(t));
        }
//        iv1.setImage(image);iv1.setX(x+50);iv1.setY(400-y);iv1.setVisible(true);
//        pane.getChildren().add(iv1);
//        iv1.setFitHeight(20);iv1.setFitWidth(30);
//        iv1.setX(x+50);iv1.setY(400-y);iv1.setVisible(true);
//            label.setVisible(true);
//            panel.add(label);panel.repaint();
        PathTransition pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.millis(totalTime * 80));
        pathTransition.setPath(path);
        pathTransition.setNode(label);
        pathTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
        
        pathTransition.play();
        coor[0] = (int) x;
        coor[1] = (int) y;
        object.add(coor);

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }
}
