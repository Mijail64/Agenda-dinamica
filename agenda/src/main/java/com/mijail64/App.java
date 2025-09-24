package com.mijail64;

import javafx.util.Duration;
import javafx.animation.ScaleTransition;
import javafx.scene.image.Image; // Image
import javafx.scene.layout.*;    // Background, BackgroundImage, BackgroundRepeat, BackgroundPosition, BackgroundSize
import javafx.application.*;   // Application, Platform, etc.
import javafx.stage.*;          // Stage, Window
import javafx.scene.*;          // Scene, Node
import javafx.scene.control.*;  // Label, Button, TextField, etc.
import javafx.scene.layout.*;   // VBox, HBox, GridPane
import javafx.scene.shape.*;    // Circle, Rectangle, etc.
import javafx.scene.paint.*;    // Color, Paint
import javafx.animation.*; 
import javafx.geometry.Pos;

public class App extends Application 
{
    private void applyHoverEffect(Button button) {
        ScaleTransition st = new ScaleTransition(Duration.millis(100), button);
        button.setOnMouseEntered(e -> {
            st.setToX(1.1);
            st.setToY(1.1);
            st.playFromStart();
        });
        button.setOnMouseExited(e -> {
            st.setToX(1.0);
            st.setToY(1.0);
            st.playFromStart();
        });
        button.setOnAction(e -> {
            st.setToX(1.0);  // encoge
            st.setToY(1.0);
            st.setAutoReverse(true); // vuelve al tamaño original
            st.setCycleCount(1);    // ida y vuelta
            st.playFromStart();
            button.disarm();
        });
    }
    @Override
    public void start(Stage stage) {
        //Elementos
        Label label1 = new Label("¡Hola Agenda!");
        Label label2 = new Label("Nueva agenda");
        Label label3 = new Label("Preparate!");
        Button btn1 = new Button("Botón 1");
        Button btn2 = new Button("Botón 2");
        Button btn3 = new Button("Botón 3");
        Button btn4 = new Button("Botón 4");
        Button btn5 = new Button("Botón 5");

        //Fila de botones
        HBox fila = new HBox(10); // 10 = espacio entre botones
        fila.setAlignment(Pos.CENTER); // opcional, centra los botones
        fila.getChildren().addAll(btn1, btn2, btn3, btn4, btn5);
        VBox root = new VBox(20); // 20 = espacio vertical entre elementos
        root.getChildren().add(fila); // añadimos la fila al layout principal

        //plantilla de label
        label1.setStyle(
            // "-fx-background-color: #a265c3AA; " + //color de fondo
            "-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, lightgreen, pink); " + //degradado
            "-fx-text-fill: lightpink; " + //color de texto
            "-fx-font-family: 'Arial Black'; " + //tipo de letra
            "-fx-font-size: 64px; " + //tamaño de letra
            "-fx-font-weight: bold; " + //negrita
            "-fx-background-radius: 25px;" + //bordes redondeados
            "-fx-border-color: pink; " + //color del borde
            "-fx-border-width: 2px; " + //grosor del borde
            "-fx-border-radius: 25px;" + //bordes redondeados del borde
            "-fx-effect: dropshadow(three-pass-box, gray, 5, 0, 0, 0);" //sombra
        );

        //plantilla de boton
        btn1.setStyle(
            "-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, lightblue, pink); " + //degradado
            "-fx-text-fill: white; " + //color de texto
            "-fx-font-family: 'Arial Black'; " + //tipo de letra
            "-fx-font-size: 18px; " + //tamaño de letra
            "-fx-font-weight: bold; " + //negrita
            "-fx-background-radius: 15px;" + //bordes redondeados
            "-fx-border-color: pink; " + //color del borde
            "-fx-border-width: 2px; " + //grosor del borde
            "-fx-border-radius: 15px;" + //bordes redondeados del borde
            "-fx-effect: dropshadow(three-pass-box, gray, 5, 0, 0, 0);" //sombra
        );
    
        btn2.setStyle(btn1.getStyle());
        btn3.setStyle(btn1.getStyle());
        btn4.setStyle(btn1.getStyle());
        btn5.setStyle(btn1.getStyle());
        applyHoverEffect(btn1);
        applyHoverEffect(btn2);
        applyHoverEffect(btn3);
        applyHoverEffect(btn4);
        applyHoverEffect(btn5);
        
        //Layout simple
        root.setAlignment(Pos.TOP_CENTER);

        root.getChildren().add(label1);
        root.getChildren().add(label2);
        root.getChildren().add(label3);

        Image img = new Image(getClass().getResource("/Ely.png").toExternalForm());
        BackgroundImage bg = new BackgroundImage(
            img,
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
        );
        root.setBackground(new Background(bg));

        // Escena con tamaño de ventana
        Scene scene = new Scene(root, 1000, 500);
        scene.setFill(Color.web("#318CE7"));

        // Configuramos y mostramos la ventana
        stage.setTitle("Mi Agenda - JavaFX");
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch(args); // arranca la aplicación JavaFX
    }
}