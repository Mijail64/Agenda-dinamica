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

import com.mijail64.Style;

import javafx.animation.*; 
import javafx.geometry.Pos;

public class App extends Application 
{
    private void applyHoverEffect(Button button) {
        ScaleTransition st = new ScaleTransition(Duration.millis(50), button);
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
            st.setToX(0.9);  // encoge
            st.setToY(0.9);
            st.setAutoReverse(true); // vuelve al tamaño original
            st.setCycleCount(1);    // ida y vuelta
            st.playFromStart();
            button.disarm();
        });
    }
    @Override
    public void start(Stage stage) {
        //Elementos
        Label labelM = new Label("¡Hola Agenda!");
        Label label1C = new Label("Nueva agenda");
        Label label3 = new Label("Preparate!");
        Button btn1m = new Button("Crear Agenda");
        Button btn2m = new Button("Modificar Agenda");
        Button btn3 = new Button("Volver");
        Button btn4 = new Button("Botón 4");
        Button btn5 = new Button("Botón 5");

        //Fila de botones
        HBox fila = new HBox(20); // 10 = espacio entre botones
        fila.setAlignment(Pos.CENTER); // opcional, centra los botones
        fila.getChildren().addAll(btn1m, btn2m);
        VBox root = new VBox(20); // 20 = espacio vertical entre elementos
        

        //---------------------Estilos---------------------
        Style.Label(labelM, 100);
        Style.Boton(btn1m, 32);
        Style.Boton(btn2m, 32);
    
        Style.AnimacionBoton(btn1m);
        Style.AnimacionBoton(btn2m);
        
        //---------------------Eventos---------------------
        // Evento para el botón "Crear Agenda"
        btn1m.setOnAction(e -> {
            VBox nuevaRoot = new VBox(20);
            nuevaRoot.setAlignment(Pos.CENTER);
            Label nuevaLabel = new Label("Menú Crear Agenda");
            Style.Label(nuevaLabel, 50);
            Button volverBtn = new Button("Volver");
            Style.Boton(volverBtn, 24);
            Style.AnimacionBoton(volverBtn);

            nuevaRoot.getChildren().addAll(nuevaLabel, volverBtn);

            // Usar el mismo fondo que la escena principal
            Image img = new Image(getClass().getResource("/Ely.png").toExternalForm());
            BackgroundImage bg = new BackgroundImage(
            img,
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
        );
        nuevaRoot.setBackground(new Background(bg));

            Scene nuevaScene = new Scene(nuevaRoot, 1000, 500);

            Stage actualStage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            // Espera de 0.5 segundos antes de cambiar la escena
            PauseTransition pausa = new PauseTransition(Duration.millis(500));
            pausa.setOnFinished(ev -> actualStage.setScene(nuevaScene));
            pausa.play();

            volverBtn.setOnAction(ev -> {
                actualStage.setScene(((Node)btn1m).getScene());
            });
        });



        //Layout simple
        root.setAlignment(Pos.CENTER);

        root.getChildren().add(labelM);
        //root.getChildren().add(label2);
        //root.getChildren().add(label3);
        root.getChildren().add(fila); // añadimos la fila al layout principal
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