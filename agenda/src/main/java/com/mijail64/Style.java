package com.mijail64;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;

public class Style {
    public static void Label(Label label, int tam) {
        label.setStyle(
            "-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #77d6ffff, #ff98e2ff); " +
            "-fx-text-fill: #ffc2f5ff; " +
            "-fx-font-family: 'Arial Black'; " +
            "-fx-font-size: "+ tam +"px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 30px;" +
            "-fx-border-color: #ffc2f5ff; " +
            "-fx-border-width: 10px; " +
            "-fx-border-radius: 25px;" +
            "-fx-effect: dropshadow(three-pass-box, #ffb7dfff, 5, 0, 0, 0);"
        );
    }

    public static void Boton (Button btn, int tam) {
        btn.setStyle(
            "-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #77d6ffff, #ff98e2ff); " +
            "-fx-text-fill: white; " +
            "-fx-font-family: 'Arial Black'; " +
            "-fx-font-size: "+ tam +"px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 15px;" +
            "-fx-border-color: pink; " +
            "-fx-border-width: 2px; " +
            "-fx-border-radius: 15px;" +
            "-fx-effect: dropshadow(three-pass-box, gray, 5, 0, 0, 0);"
        );
    }

    public static void AnimacionBoton(Button btn) {
        ScaleTransition stEnter = new ScaleTransition(Duration.millis(100), btn);
        stEnter.setToX(1.1);
        stEnter.setToY(1.1);

        ScaleTransition stExit = new ScaleTransition(Duration.millis(100), btn);
        stExit.setToX(1.0);
        stExit.setToY(1.0);

        btn.setOnMouseEntered(e -> stEnter.playFromStart());
        btn.setOnMouseExited(e -> stExit.playFromStart());
    }
}