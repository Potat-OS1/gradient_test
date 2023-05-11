package com.example.gradient_test;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;


public class Controller extends Application {

    @Override
    public void start(Stage stage) {
        ImageView iv = new ImageView(new Image(Objects.requireNonNull(Controller.class.getResourceAsStream("/image.jpg"))));
        Shape shadows = new Rectangle(400, 400);
        shadows.setOpacity(0.7);
        Pane p = new Pane(iv);

        Shape r = new Circle(200);
        Circle rCutout = new Circle(90);
        Circle r2 = new Circle(200);
        Pane lights = new Pane(r, r2);


        r.setFill(new ImagePattern(imageGradientToBlack(Color.GREEN, 400, 400, 240, 0.0), 200, 200, 400, 400, false));
        r2.setFill(new ImagePattern(imageGradientToBlack(Color.RED, 400,  400, 240, 0.0),  200, 200, 400, 400, false));
        //r.setBlendMode(BlendMode.ADD);
        lights.setBlendMode(BlendMode.ADD);

        rCutout.setLayoutX(100);
        rCutout.setLayoutY(100);
        shadows = Shape.subtract(shadows, rCutout);

        //BoxBlur bb = new BoxBlur(Math.pow(100, .95), Math.pow(100, .95), 5);
        //r.setEffect(bb);

        SnapshotParameters sp = new SnapshotParameters();
        sp.setFill(Color.TRANSPARENT);
        WritableImage snapshot = r.snapshot(sp, null);
        snapshot = r2.snapshot(sp, snapshot);

        WritableImage snapshot2 = r.snapshot(sp, null);
        snapshot2 = r.snapshot(sp, snapshot2);

        WritableImage image = new WritableImage(600, 600);
        PixelWriter pw = image.getPixelWriter();
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                pw.setColor(x, y, new Color(0,0,0,.7));
            }
        }



        combineImages(snapshot, image, pw, 150, 200);
        combineImages(snapshot2, image, pw, 150, 250);

        BoxBlur bb = new BoxBlur(10, 10, 2);

        ImageView test = new ImageView(image);
        test.setEffect(bb);

        p.getChildren().add(test);
        test.setLayoutX(-100);
        test.setLayoutY(-100);
//        p.getChildren().add(shadows);
//        p.getChildren().add(lights);
        //p.getChildren().add(r2);

        r.setLayoutX(100);
        r.setLayoutY(100);
        r2.setLayoutX(150);
        r2.setLayoutY(150);




        Scene scene = new Scene(p, 400, 400);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public void combineImages(Image overlayImage, Image lightImage, PixelWriter pw, int x, int y) {
        int baseWidth = (int) lightImage.getWidth();
        int baseHeight = (int) lightImage.getHeight();
        // Get the pixel reader for the base image
        PixelReader baseReader = lightImage.getPixelReader();

        // Get the pixel reader for the overlay image
        PixelReader overlayReader = overlayImage.getPixelReader();

        // Get the pixel writer for the result image

        double red;
        double blue;
        double green;
        double alpha;
        int overlayCol;
        int overlayRow;

        // Iterate over each pixel of the base image
        for (int a = 0; a < baseHeight; a++) {
            for (int b = 0; b < baseWidth; b++) {
                // Calculate the corresponding position in the overlay image
                overlayCol = b + x;
                overlayRow = a + y;

                try {
                    if (overlayReader.getColor(b, a).getOpacity() != 0.0) {
                        if (baseReader.getColor(b,a).getRed() != 0.0 && baseReader.getColor(b, a).getBlue() != 0.0 && baseReader.getColor(b, a).getGreen() != 0.0) {
                            alpha = Math.max(overlayReader.getColor(b, a).getOpacity(), baseReader.getColor(overlayCol, overlayRow).getOpacity());
                            System.out.println("aaaaa");
                            //alpha = (overlayReader.getColor(b, a).getOpacity() + baseReader.getColor(overlayCol, overlayRow).getOpacity())/2.0;
                        }
                        else {
                            alpha = Math.min(overlayReader.getColor(b, a).getOpacity(), .7);
                        }
                        red = Math.max(overlayReader.getColor(b, a).getRed(), baseReader.getColor(overlayCol, overlayRow).getRed());
                        blue = Math.max(overlayReader.getColor(b, a).getBlue(), baseReader.getColor(overlayCol, overlayRow).getBlue());
                        green = Math.max(overlayReader.getColor(b, a).getGreen(), baseReader.getColor(overlayCol, overlayRow).getGreen());


                        pw.setColor(overlayCol, overlayRow, new Color(red, green, blue, alpha));
                    }
                }
                catch (Exception ignored) {
                    //pw.setColor(b, a, Color.BLACK);
                }
            }
        }
    }

    public static void main(String[] args) {
        launch();
    }

    private static Image imageGradientToBlack (Color c, int width, int height, int steps, double deadPercent) {
        int endSteps = (int) (steps / 4.0);
        int deadSteps = (int) (steps * deadPercent);
        int normalSteps = steps - endSteps - deadSteps;

        double deltaR = (c.getRed()) / (normalSteps);
        double deltaG = (c.getGreen()) / (normalSteps);
        double deltaB = (c.getBlue()) / (normalSteps);

        double centerX = Math.ceil(width/2.0);
        double centerY = Math.ceil(height/2.0);

        //double maxLength = Math.sqrt(Math.pow(centerX - width, 2) + Math.pow(centerY - height, 2));
        double currentLength;

        double stepSize = Math.sqrt(Math.pow(centerX - width, 2) + Math.pow(centerY - height, 2)) / (steps * 1.1);
        int currentStep;

        WritableImage wi = new WritableImage(width, height);
        PixelWriter pw = wi.getPixelWriter();

        double red;
        double blue;
        double green;
        double alpha;

        for (int x = 0; x < wi.getWidth(); x++) {
            for (int y = 0; y < wi.getHeight(); y++) {
                currentLength = Math.sqrt(Math.pow(centerX - (x+1), 2) + Math.pow(centerY - (y+1), 2));
                currentStep = (int) Math.floor(currentLength / stepSize);

                if (currentStep > normalSteps + deadSteps) {
                    //pw.setColor(x, y, Color.BLACK);
                    continue;
                }
                if (currentStep < deadSteps) {
                    pw.setColor(x, y, Color.TRANSPARENT);
                }
                else {
                    currentStep = currentStep - deadSteps;

                    red = (c.getRed() - (deltaR * currentStep));
                    blue = (c.getBlue() - (deltaB * currentStep));
                    green = (c.getGreen() - (deltaG * currentStep));
                    alpha = 0.45 + ((0.55/normalSteps) * currentStep);

                    pw.setColor(x, y, new Color(red, green, blue, alpha));
                }

            }
        }
        return wi;
    }
}