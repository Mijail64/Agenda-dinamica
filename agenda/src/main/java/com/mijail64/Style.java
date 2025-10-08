package com.mijail64;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.layout.VBox;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.application.Platform;
import javafx.scene.control.ScrollBar;

import com.mijail64.TablaUtils.Agenda;


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
    //Para la tabla
    public static void estilizarTabla(TableView<Agenda> tabla) {
        // Estilo general de la tabla
        tabla.setStyle(
            "-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #77d6ffff, #ffa1ecff); " +
            "-fx-background-radius: 20px; " +
            "-fx-border-color: #ffc2f5ff; " +
            "-fx-border-width: 5px; " +
            "-fx-border-radius: 20px; " +
            "-fx-table-cell-border-color: transparent; " +
            "-fx-effect: dropshadow(three-pass-box, #ffb7dfff, 5, 0, 0, 0);"
        );

        // Encabezado
        for (TableColumn<Agenda, ?> col : tabla.getColumns()) {
            col.setStyle(
                "-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #aedcffff, #ffb0fbff); " +
                "-fx-background-radius: 15px; " +
                "-fx-border-color: #ffc2f5ff; " +
                "-fx-border-radius: 15px; " +
                "-fx-border-width: 2px; " +
                "-fx-text-fill: #ffffff; " +
                "-fx-font-family: 'Arial Black'; " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold; " +
                "-fx-alignment: CENTER; " +
                "-fx-border-radius: 15px;" +
                "-fx-effect: dropshadow(three-pass-box, gray, 3, 0, 0, 0);"
            );
        }

    
        // Celdas
        tabla.setRowFactory(tv -> {
            TableRow<Agenda> row = new TableRow<>();
            row.setStyle(
                "-fx-background-color: #adc4ffff; " +
                "-fx-background-radius: 5px; " +
                "-fx-border-color: #ffc2f5ff; " +
                "-fx-border-width: 0 0 1 0; " +
                "-fx-border-radius: 5px 5px 5px 5px; " +
                "-fx-effect: dropshadow(three-pass-box, #ffb7dfff, 2, 0, 0, 0);"
            );
            return row;
        });
        // ScrollBar + header tweaks: ajustar estilos una vez que la escena se construya
        Platform.runLater(() -> {
            // Scrollbar vertical: colores pastel rosa
            tabla.lookupAll(".scroll-bar:vertical").forEach(node -> {
               if (node instanceof ScrollBar sb) {
                    sb.setStyle(
                        "-fx-background-color: transparent; " +
                        "-fx-padding: 2px; " +
                        "-fx-background-insets: 0; " +
                        "-fx-border-color: transparent; " +
                        // Thumb style (aplica al thumb por medio de substructure en CSS, but inline here for simplicity)
                        "-fx-background-radius: 10px; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(255,183,223,0.3), 3, 0, 0, 0);"
                    );
                    // también intentar estilizar el 'thumb' si está disponible
                    sb.lookupAll(".thumb").forEach(t -> t.setStyle(
                        "-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #ffd6ea, #ffb0d9); " +
                        "-fx-background-radius: 10px; " +
                        "-fx-border-color: #ffd6ea; -fx-border-width: 1px;"
                    ));
                }
            });

            // Encabezado: quitar el fondo blanco y aplicar un degradado/transparencia
            tabla.lookupAll(".column-header-background").forEach(node -> {
                node.setStyle(
                    "-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, rgba(250,235,245,0.6), rgba(255,224,240,0.4)); " +
                    "-fx-background-insets: 0; " +
                    "-fx-padding: 0 0 0 0;"
                );
            });

            // Las celdas de encabezado (column-header) también las hacemos más transparentes
            tabla.lookupAll(".column-header").forEach(node -> {
                node.setStyle(
                    "-fx-background-color: transparent; " +
                    "-fx-padding: 6px;"
                );
            });
        });
    }
    // Estilo para popups
    public static void PopupBox(VBox box) {
        box.setStyle(
            "-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #ffffff, #ffecfaff); " +
            "-fx-background-radius: 28px; " +
            "-fx-border-color: #ffc2f5ff; " +
            "-fx-border-width: 5px; " +
            "-fx-border-radius: 25px; " +
            "-fx-padding: 15px; " +
            "-fx-effect: dropshadow(three-pass-box, #ffb7dfff, 5, 0, 0, 0);"
        );
    }

    // Label compacto para entradas pequeñas (menos padding y font más pequeño)
    public static void SmallLabel(Label label, int tam) {
        label.setStyle(
            "-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #77d6ffff, #ff98e2ff); " +
            "-fx-text-fill: #ffffffff; " +
            "-fx-font-family: 'Arial Black'; " +
            "-fx-font-size: "+ tam +"px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 5px;" +
            "-fx-border-color: #ffc2f5ff; " +
            "-fx-border-width: 3px; " +
            "-fx-border-radius: 4px;" +
            "-fx-effect: dropshadow(three-pass-box, #ffb7dfff, 5, 0, 0, 0);"
        );
    }
}

