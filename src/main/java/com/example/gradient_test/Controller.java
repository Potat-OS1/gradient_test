package com.example.gradient_test;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

//import java.awt.*;


public class Controller extends Application {
    @Override
    public void start(Stage stage) {
        Pane p = new Pane();
        p.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
        Circle c = new Circle(100);
        //c.setFill(new ImagePattern(imageGradientToBlack(Color.BLUE, 200, 200, 80), 100, 100, 200, 200, false));
        Rectangle r = new Rectangle(200, 200);
        r.setFill(new ImagePattern(imageGradientToBlack(Color.BLUE, 200, 200, 80), 0, 0, 200, 200, false));


        //ImageView iv = new ImageView(imageGradientToBlack(Color.BLUE, 100, 100, 10));
        p.getChildren().add(r);
        r.setLayoutX(50);
        r.setLayoutY(50);




        Scene scene = new Scene(p, 400, 400);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    private static Image imageGradientToBlack (Color c, int width, int height, int steps) {
        int endSteps = (int) (steps / 4.0);
        int deadSteps = (int) (steps / 8.0);
        int normalSteps = steps - endSteps - deadSteps;

        System.out.println(endSteps+normalSteps+deadSteps);

        double deltaR = (c.getRed()) / (normalSteps);
        double deltaG = (c.getGreen()) / (normalSteps);
        double deltaB = (c.getBlue()) / (normalSteps);

        double centerX = Math.ceil(width/2.0);
        double centerY = Math.ceil(height/2.0);

        double maxLength = Math.sqrt(Math.pow(centerX - width, 2) + Math.pow(centerY - height, 2));
        double currentLength;

        double stepSize = maxLength / (steps * 1.1);
        int currentStep;

        WritableImage wi = new WritableImage(width, height);
        PixelWriter pw = wi.getPixelWriter();

        for (int x = 0; x < wi.getWidth(); x++) {
            for (int y = 0; y < wi.getHeight(); y++) {
                currentLength = Math.sqrt(Math.pow(centerX - (x+1), 2) + Math.pow(centerY - (y+1), 2));
                currentStep = (int) Math.floor(currentLength / stepSize);

                if (currentStep > normalSteps + deadSteps) {
                    pw.setColor(x, y, Color.BLACK);
                    continue;
                }
                if (currentStep < deadSteps) {
                    pw.setColor(x, y, Color.TRANSPARENT);
                }
                else {
                    currentStep = currentStep - deadSteps;

                    double red = (c.getRed() - (deltaR * currentStep));
                    double blue = (c.getBlue() - (deltaB * currentStep));
                    double green = (c.getGreen() - (deltaG * currentStep));
                    double alpha = ((1.0/normalSteps) * currentStep);
                    System.out.println(red + "  " + blue + "  " + green + "  " +alpha);

                    pw.setColor(x, y, new Color(red, green, blue, alpha));
                }

            }
        }
        return wi;
    }
}