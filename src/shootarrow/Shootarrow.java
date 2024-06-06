package shootarrow;

import com.sun.glass.ui.Screen;
import static java.lang.Math.round;
import java.util.ArrayList;
import java.util.Random;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.media.*;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.swing.UIManager;

/**
 *
 * @author Administrator
 */
public class Shootarrow extends Application {

    // declaring all the variables
    public static Scene scene;
    double x = 0.0; // x movement in projectile motion
    double y = 0.0; // y movement in projectile motion
    double t = 0.0; // current time
    double xVelocity; 
    double yVelocity;
    double totalTime; // total time of flight
    double timeIncrement; // time increment for the timer
    double xIncrement; // x coordinate is changing
    double angle; // initial angle of shooting
    double anglesubstract = 1; // rotate the arrow according to time
    double inix;  // initial x coor
    double iniy;  // initial y coor 
    double pointdistance;  
    boolean rotate;

    public static final double ACCELERATION = -9.8;
    private Image arrowimage;  
    private ImageView background;
    private Node body, body2;  // body,arrow,hand and bow of 2 players
    private Node[] arrow = new Node[2];
    private Node[] hands = new Node[2];
    private Node[] bow = new Node[2];
    private ArrayList<Node> arrows = new ArrayList<Node>(); // shot arrow
    private Group board;
    ScrollPane pane;
    ProgressBar[] power = new ProgressBar[2]; // to display the degree of shooting power
    ProgressBar[] healthbar = new ProgressBar[2]; 
    Text[] angletext = new Text[2]; // display angle before shooting
    Text[] powertext = new Text[2]; 
    Text[] healthtext = new Text[2];
    Text[] damagetext = new Text[2]; // display damage when arrow hit opponent
    Text[] windtext = new Text[2]; // display the wind factor
    int winddirection;
    int windforce;
    private int[] health = {100, 100}; // default health is 100 pt
    boolean shooting, shooting2;
    int rand = new Random().nextInt(1500); // min distance between players is 1000meter, final distance will 1000+random(1500) meter
                                          // resultant distance = 1000 to 2500
    //AudioClip ac = new AudioClip(this.getClass().getClassLoader().getResource("injured.mp3").toString());
    // sound effect when being hit

    @Override
    public void start(Stage stage) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        arrowimage = new Image("arrow.png", 85, 25, false, false);
        
        arrow[0] = new ImageView(arrowimage);  // set the arrow image to Node arrow for player 1
        body = new ImageView(new Image("body.png")); // same process here
        hands[0] = new ImageView(new Image("hands.png"));
        bow[0] = new ImageView(new Image("bow.png"));

        arrow[1] = new ImageView(arrowimage);
        body2 = new ImageView(new Image("body.png")); // this is for player 2
        hands[1] = new ImageView(new Image("hands.png"));
        bow[1] = new ImageView(new Image("bow.png"));

        background = new ImageView(new Image("background.jpg"));
        background.setLayoutX(-3500);
        background.setLayoutY(460); 
        
        // initialize all the components of the game
        for (int i = 0; i < 2; i++) {
            power[i] = new ProgressBar(1.0);
            power[i].setPrefWidth(150);
            power[i].setPrefHeight(20);
            power[i].setProgress(0);
            power[i].setStyle("-fx-accent: orange;");
            healthbar[i] = new ProgressBar(3.0);
            healthbar[i].setPrefWidth(250);
            healthbar[i].setPrefHeight(70);
            healthbar[i].setProgress(1.0);
            healthbar[i].setStyle("-fx-accent: red;");
            if (i == 1) {
                power[i].relocate(1030 + rand, 123);
                healthbar[i].relocate(1030 + rand, 33);
                healthbar[i].setPadding(new Insets(50, 0, 0, 0));
                powertext[i] = new Text(950 + rand, 140, "Power: ");
                angletext[i] = new Text(1190 + rand, 140, "Angle: " + angle);
                healthtext[i] = new Text(950 + rand, 100, "Health: ");
                damagetext[i] = new Text(900 + rand, 0, "-damage ");
                windtext[i] = new Text(1100 + rand, 180, "");
            } else {
                power[i].relocate(130, 123);
                healthbar[i].relocate(130, 33);
                healthbar[i].setPadding(new Insets(50, 0, 0, 0));
                powertext[i] = new Text(50, 140, "Power: ");
                angletext[i] = new Text(290, 140, "Angle: " + angle);
                healthtext[i] = new Text(50, 100, "Health: ");
                damagetext[i] = new Text(0, 0, "-damage ");
                windtext[i] = new Text(200, 180, "");
            }
            powertext[i].setFont(Font.font("verdana", 20));
            angletext[i].setFont(Font.font("verdana", 20));
            healthtext[i].setFont(Font.font("verdana", 20));
            damagetext[i].setFont(Font.font("verdana", 20));
            windtext[i].setFont(Font.font("verdana", 20));
            damagetext[i].setFill(Color.RED);
            damagetext[i].setVisible(false);
            windtext[i].setVisible(false);
        }
        board = new Group();

        board.getChildren().addAll(background, healthtext[0], healthtext[1], powertext[0], powertext[1], angletext[0], angletext[1], damagetext[0], damagetext[1],
                healthbar[0], healthbar[1], body, hands[0], bow[0], arrow[0], body2, hands[1], bow[1], arrow[1], power[0], power[1],
                windtext[0], windtext[1]);// all components is added to board to be display in scene
        pane = new ScrollPane(board); // make it scrollable
        pane.setContent(board);
        pane.setOnScroll(new EventHandler<ScrollEvent>() {   // allow mouse wheel scrolling
                @Override
                public void handle(ScrollEvent event) {
                    pane.setHvalue(pane.getHvalue()-event.getDeltaY()/3000.0);
                }
            });
        
        scene = new Scene(pane, 1366, 611);
        // setting the x and y layout of player 1 components at one location
        arrow[0].relocate(x + 140, scene.getHeight() / 1.57 - y);
        arrow[0].rotateProperty().set(-20);
        body.relocate(x + 50, scene.getHeight() / 2.2 - y);
        damagetext[1].relocate(body.getLayoutX() + 30, body.getLayoutY() - 50);
        hands[0].relocate(x + 78, scene.getHeight() / 1.6 - y);
        bow[0].relocate(x + 116, scene.getHeight() / 1.78 - y);

         // setting the x and y layout of player 2 components at one location as random distance
        arrow[1].relocate(x + 1000 + rand, scene.getHeight() / 1.57 - y);
        arrow[1].scaleXProperty().set(-1); // laterally invert the image
        arrow[1].setDisable(true); // arrow of player 2 cannot be shoot because now is player 1 turn
        arrow[1].rotateProperty().set(20);
        body2.relocate(x + 1015 + rand, scene.getHeight() / 2.2 - y);
        body2.scaleXProperty().set(-1); // laterally invert the image
        damagetext[0].relocate(body2.getLayoutX(), body2.getLayoutY() - 50);
        hands[1].relocate(x + 1005 + rand, scene.getHeight() / 1.6 - y);
        hands[1].scaleXProperty().set(-1); // laterally invert the image
        bow[1].relocate(x + 993 + rand, scene.getHeight() / 1.78 - y);
        bow[1].scaleXProperty().set(-1); // laterally invert the image

        gotoplayer1(); // the window focus on player 1 because he is going to shoot

        for (int i = 0; i < 2; i++) {
            arrow[i].setOnMouseEntered(new EventHandler<MouseEvent>() { // making the arrow look like clickable 
                @Override
                public void handle(MouseEvent event) {
                    scene.setCursor(Cursor.HAND);
                }
            });
        }        
        for (int i = 0; i < 2; i++) {
            arrow[i].setOnMouseExited(new EventHandler<MouseEvent>() {  // making the arrow not clickable when cursor exited 
                @Override
                public void handle(MouseEvent event) {
                    scene.setCursor(Cursor.DEFAULT);
                }
            });
        }
        
        for (int i = 0; i < 2; i++) {
            Node temp = arrow[i];
            Node temphands = hands[i];
            Node tempbow = bow[i];
            int number = i;
            temp.setOnMousePressed(new EventHandler<MouseEvent>() { // action perform when arrow being clicked
                @Override
                public void handle(MouseEvent event) {
                    damagetext[0].setVisible(false); // previous damage will disappear
                    damagetext[1].setVisible(false); 
                    inix = event.getSceneX(); // get the x coordinate of the cursor
                    iniy = scene.getHeight() - event.getSceneY();
                    temp.setOnMouseDragged(new EventHandler<MouseEvent>() { // action perform when mouse dragged
                        @Override
                        public void handle(MouseEvent event) {
                            double angle;
                            // calculate the angle of the shooting
                            if (number == 0) { // number 0 mean player 1, number 1 is player 2 
                                angle = (double) Math.toDegrees(Math.atan2(iniy - scene.getHeight() + event.getSceneY(), inix - event.getSceneX()));
                            } else {
                                angle = -1.0 * (double) Math.toDegrees(Math.atan2(-iniy + scene.getHeight() - event.getSceneY(), -inix + event.getSceneX()));
                            }
                            angletext[number].setText("Angle: " + round(angle));
                            if (number == 0) { //player 1 movement
                                temphands.rotateProperty().set(-angle + 20);
                                tempbow.rotateProperty().set(-angle + 20);  // make body,hands and arrow rotate together with cursor
                                temp.rotateProperty().set(-angle);
                            } else { // player 2 movement
                                temphands.rotateProperty().set(angle - 20);
                                tempbow.rotateProperty().set(angle - 20);
                                temp.rotateProperty().set(angle);
                            }
                            pointdistance = Math.sqrt((iniy - scene.getHeight() + event.getSceneY()) * (iniy - scene.getHeight() + event.getSceneY())
                                    + (inix - event.getSceneX()) * (inix - event.getSceneX())); 
                            if (pointdistance > 100) {
                                pointdistance = 100; // set limit for the shooting power
                            }
                            power[number].setProgress(pointdistance / 100.0); // power bar will display the power dynamically
                        }
                    });

                }
            });
        }
        for (int i = 0; i < 2; i++) {
            Node temp = arrow[i];
            Node temphands = hands[i];
            Node tempbow = bow[i];
            int number = i;
            temp.setOnMouseReleased(new EventHandler<MouseEvent>() { // arrow will be shot when cursor pressed and released
                @Override
                public void handle(MouseEvent event) {
                    double finx = event.getSceneX(); // x coordinate when mouse released
                    double finy = scene.getHeight() - event.getSceneY();
                    power[number].setProgress(0);
                    angletext[number].setText("Angle: " + 0);
                    double angle1; // calcute the angle for shooting to be used for calculate velocity later
                    if (number == 0) {
                        angle1 = (double) Math.toDegrees(Math.atan2(iniy - finy, inix - finx));
                    } else {
                        angle1 = -1.0 * (double) Math.toDegrees(Math.atan2(-iniy + finy, -inix + finx));
                    }
                    temphands.rotateProperty().set(0); // after shooting the arrow,hands and bow will go back its original position
                    tempbow.rotateProperty().set(0);
                    if (number == 0) {
                        temp.rotateProperty().set(- 20);
                    } else {
                        temp.rotateProperty().set(20);
                    }
                    // another new arrow cannot be shot before the shooting arrow reach the ground
                    if ((shooting || shooting2) && y > -50)
                    ; else {
                        x = 0.0; // set x,y movement and current time to 0
                        y = 0.0;
                        t = 0.0;
                        double velocity;
                        // calculate initial velocity according to wind factor and the randomized distance factor
                        if (winddirection == 1) {
                            if (angle1 >= -90 && angle1 <= 90) {
                                velocity = 35 + 110 * pointdistance / 100.0 + rand / 70 + windforce * 3;
                            } else {
                                velocity = 35 + 110 * pointdistance / 100.0 + rand / 70 - windforce * 3;
                            }
                        } else {
                            if (angle1 >= -90 && angle1 <= 90) {
                                velocity = 35 + 110 * pointdistance / 100.0 + rand / 70 - windforce * 3;
                            } else {
                                velocity = 35 + 110 * pointdistance / 100.0 + rand / 70 + windforce * 3;
                            }
                        }
                        pointdistance = 0;
                        angle = Math.toRadians(angle1);
                        xVelocity = velocity * Math.cos(angle);// calculate x and y component velocity
                        yVelocity = velocity * Math.sin(angle);
                        double finalv = Math.sqrt((Math.pow(yVelocity, 2) + 2.0 * ACCELERATION * (-50)));
                        // calculate final y velocity before hitting ground
                        totalTime = (-1.0 * finalv - yVelocity) / ACCELERATION;
                        // use final y velocity to find the flight time
                        int steps = (int) (round(totalTime) * 6) + 3;// define the fps of the motion
                        timeIncrement = totalTime / steps; // define the fps of the motion
                        xIncrement = xVelocity * timeIncrement; // determine x increment per frame

                        ImageView anarrow = new ImageView(arrowimage);
                        Node newarrow = anarrow; 
                        arrows.add(newarrow); // New arrow is added as a node to the arraylist of arrows so that arrow will not disappear
                        board.getChildren().add(newarrow);
                        if (number == 0) {
                            shooting = true; // display the shooting motion of player 1 arrow
                        } else {
                            shooting2 = true;// display the shooting motion of player 2 arrow
                        }
                    }
                }
            });
        }
        stage.setScene(scene);
        stage.show();
        stage.setTitle("arrow shooting");
        stage.setMaxWidth(Screen.getMainScreen().getWidth());
        stage.centerOnScreen();
        stage.setResizable(false);
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // timer will perform action when either player1 and player2 release the arrow
                if (shooting || shooting2) {
                    t += timeIncrement; // current time always increase
                    if (shooting) {
                        x += xIncrement; // player 1 arrow move to right 
                    } else {
                        x -= xIncrement; // player 2 arrow move to left
                    }
                    y = yVelocity * t + 0.5 * ACCELERATION * t * t; // y displacement is always recalculate the each time
                    // calculate how the arrow will rotate during the flight
                    if (Math.toDegrees(angle) <= 90 && Math.toDegrees(angle) > 0) {
                        anglesubstract = (Math.toDegrees(angle)) * (t / totalTime) * 2;
                    } else if (Math.toDegrees(angle) >= - 90 && Math.toDegrees(angle) <= 0) {
                        anglesubstract = 5 * (t / totalTime) * 2;
                    } else if (Math.toDegrees(angle) < - 90 && Math.toDegrees(angle) >= -180) {
                        anglesubstract = -5 * (t / totalTime) * 2;
                    } else {
                        anglesubstract = (180 - (Math.toDegrees(angle))) * (t / totalTime) * -2;
                    }
                    // arrow flight motion
                    projectilemotion();
                    // check the arrow hit opponent
                    checkhit();
                    // if arrow reached ground, it will become green color
                    if (round(y) <= -50) {
                        ColorAdjust c = new ColorAdjust();
                        Color target = Color.GREEN;
                        c.setBrightness(0.6); // setting the brightness of the color.   
                        c.setContrast(0.2); // setting the contrast of the color  
                        c.setHue(target.getHue() + 0.1); // setting the hue of the color  
                        c.setSaturation(1); // setting the saturation of the color. 
                        arrows.get(arrows.size() - 1).setEffect(c);
                        ColorAdjust c1 = new ColorAdjust();
                        c1.setContrast(1); // setting the contrast of the color  
                        // set previous green color arrow to original color
                        if (arrows.size() >= 2) {
                            arrows.get(arrows.size() - 2).setEffect(c1);
                        }
                        Timeline timeline;
                        // this is a delay event, when a player turn is finished, the focus will go to another player position
                        if (shooting) {
                            shooting = false;
                            timeline = new Timeline(new KeyFrame(Duration.millis(500),
                                    ae -> gotoplayer2())); // player 1 finish shooting, now player 2 will shoot
                            timeline.play();
                        }
                        if (shooting2) {
                            shooting2 = false;
                            timeline = new Timeline(new KeyFrame(Duration.millis(500),
                                    ae -> gotoplayer1()));  // player 2 finish shooting, now player 1 will shoot
                            timeline.play();
                        }
                    }
                }
                 // check the player health reach 0
                if (health[0] <= 0 || health[1] <= 0) {
                    Text GameOver;
                    // game over text will display on top of the loser
                    if (health[0] == 0) {
                        GameOver = new Text(body.getLayoutX() + 100, 300, "Player 1 Lose !");
                    } else {
                        GameOver = new Text(body2.getLayoutX() - 100, 300, "Player 2 Lose !");
                    }
                    windtext[0].setVisible(false);
                    windtext[1].setVisible(false);
                    GameOver.setFill(Color.RED);
                    GameOver.setFont(Font.font("Verdana", 50));
                    board.getChildren().add(GameOver);
                    arrow[0].setDisable(true);
                    arrow[1].setDisable(true); // stop the game
                }
            }
        };

        timer.start();
    }

    private void projectilemotion() {
        if (shooting) { // player 1
            arrows.get(arrows.size() - 1).relocate(x + 140, scene.getHeight() / 1.57 - y); // arrow movement change in each frame
            pane.setHvalue(1.0 * (3600) / (10000) + (1.0 * x) / (10000)); // screen focus move with the flight
            if (arrows.get(arrows.size() - 1).rotateProperty().getValue() != 90) {
                // arrow rotate durng flight
                arrows.get(arrows.size() - 1).rotateProperty().setValue(Math.toDegrees(-angle) + anglesubstract);
            }
        }

        if (shooting2) { //player 2
            arrows.get(arrows.size() - 1).relocate(x + 1000 + rand, scene.getHeight() / 1.57 - y); // arrow movement change in each frame
            pane.setHvalue(1.0 * (4500 + rand) / (10000) + (1.0 * rand / 100000) + (1.0 * x) / (10000)); // screen focus move with the flight
            if (arrows.get(arrows.size() - 1).rotateProperty().getValue() != 90) {
                // arrow rotate durng flight
                arrows.get(arrows.size() - 1).rotateProperty().setValue(Math.toDegrees(angle) - anglesubstract + 180);
            }
        }

    }

    private void checkhit() {
        if (shooting) {// player 1
            if (arrows.get(arrows.size() - 1).getBoundsInParent().intersects(body2.getBoundsInParent())
                    || arrows.get(arrows.size() - 1).getBoundsInParent().intersects(hands[1].getBoundsInParent())) {
                board.getChildren().remove(arrows.get(arrows.size() - 1));
                arrows.remove(arrows.size() - 1); // the arrow will be remove from screen if hit opponent
                //ac.play(300); // injured sound playing
                // calculate damage according to velocity
                double damage = 0.5 * 0.0025 * ((xVelocity * xVelocity) + (yVelocity * yVelocity)) + 3;
                if (health[1] < damage) {
                    health[1] = 0; 
                } else {
                    health[1] -= damage; // health of opponent will decrease
                }
                healthbar[1].setProgress(health[1] / 100.0 + 0.02);
                shooting = false; // stop the flight of arrow 
                damagetext[0].setText("-damage " + round(damage));
                damagetext[0].setVisible(true); // display damage
                Timeline timeline = new Timeline(new KeyFrame(Duration.millis(500),
                        ae -> gotoplayer2())); // another player will take over the control
                timeline.play();
            }
        }
        if (shooting2) { // same goes to player 2
            if (arrows.get(arrows.size() - 1).getBoundsInParent().intersects(body.getBoundsInParent())
                    || arrows.get(arrows.size() - 1).getBoundsInParent().intersects(hands[0].getBoundsInParent())) {
                board.getChildren().remove(arrows.get(arrows.size() - 1));
                arrows.remove(arrows.size() - 1);
                //ac.play(300);
                double damage = 0.5 * 0.0025 * ((xVelocity * xVelocity) + (yVelocity * yVelocity)) + 3;
                if (health[0] < damage) {
                    health[0] = 0;
                } else {
                    health[0] -= damage;
                }
                healthbar[0].setProgress(health[0] / 100.0 + 0.02);
                shooting2 = false;
                damagetext[1].setText("-damage " + round(damage));
                damagetext[1].setVisible(true);
                Timeline timeline = new Timeline(new KeyFrame(Duration.millis(500),
                        ae -> gotoplayer1()));
                timeline.play();
            }
        }
    }

    private void gotoplayer1() {
        pane.setHvalue(1.0 * (3600) / (10000));
        winddirection = new Random().nextInt(2);
        windforce = new Random().nextInt(6); 
        windtext[0].setVisible(true); // display the random wind factor
        windtext[1].setVisible(false);
        arrow[1].setDisable(true); // if now is player 1 turn, player 2 will not allow to shoot anymore
        arrow[0].setDisable(false); 
        if (windforce == 0) {
            windtext[0].setText(" Wind force: 0");
        } else if (winddirection == 1) { // down the wind
            windtext[0].setText(" Wind force: => " + windforce * 10);
        } else { // against the wind
            windtext[0].setText(" Wind force: <= " + windforce * 10);
        }
    }

    private void gotoplayer2() { // same goes to player 2
        pane.setHvalue(1.0 * (4500 + rand) / (10000) + (1.0 * rand / 100000));
        winddirection = new Random().nextInt(2);
        windforce = new Random().nextInt(6);
        windtext[1].setVisible(true);
        windtext[0].setVisible(false);
        arrow[0].setDisable(true);
        arrow[1].setDisable(false);
        if (windforce == 0) {
            windtext[1].setText(" Wind force: 0");
        } else if (winddirection == 1) {
            windtext[1].setText(" Wind force: <= " + windforce * 10);
        } else {
            windtext[1].setText(" Wind force: => " + windforce * 10);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
