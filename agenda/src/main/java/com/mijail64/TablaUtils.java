package com.mijail64;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.scene.Scene;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.ParallelTransition;
import javafx.util.Duration;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import com.mijail64.TablaUtils.Agenda;

import com.mijail64.Style;

import javafx.beans.property.SimpleStringProperty;

public class TablaUtils {

    // Método que muestra un popup editable para una celda específica
    public static void mostrarPopupCelda(TableCell<Agenda, String> cell, String propiedad) {
        // Use a Stage instead of Popup so we can animate and center like other dialogs
        Stage dialog = new Stage();
        Window owner = cell.getScene().getWindow();
        dialog.initOwner((Stage) owner);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initStyle(StageStyle.TRANSPARENT);

        VBox box = new VBox(10);
        Style.PopupBox(box);  // Aplica el estilo del popup
        Agenda fila = cell.getTableView().getItems().get(cell.getIndex());

        // Determinar índice del día según columna
        int dia = switch(propiedad) {
            case "lunes" -> 0;
            case "martes" -> 1;
            case "miercoles" -> 2;
            case "jueves" -> 3;
            case "viernes" -> 4;
            case "sabado" -> 5;
            case "domingo" -> 6;
            default -> -1;
        };

        // Tomar valores de la matriz si corresponde
        String[] datos = (dia >= 0) ? fila.obtener(dia, cell.getIndex()) : null;
        String valorNombre = datos != null && datos[0] != null ? datos[0] : "";
        String valorDescripcion = datos != null && datos[1] != null ? datos[1] : "";
        String valorTag = datos != null && datos[2] != null ? datos[2] : "";
        String valorPrioridad = datos != null && datos[3] != null ? datos[3] : "";

    TextField nombre = new TextField(valorNombre);
    TextArea descripcion = new TextArea(valorDescripcion);
    descripcion.setWrapText(true);
    descripcion.setPrefRowCount(4);
    descripcion.setPrefColumnCount(20);
    TextField tag = new TextField(valorTag);
        ComboBox<String> prioridad = new ComboBox<>();
        prioridad.getItems().addAll("Baja", "Media", "Alta");
        prioridad.setValue(valorPrioridad);

        Button guardar = new Button("Guardar");
        Style.Boton(guardar, 17);
        guardar.setOnAction(e -> {
            // Guardar en propiedades
            if(dia >= 0) {
                fila.registrar(dia, cell.getIndex(), nombre.getText(), descripcion.getText(), tag.getText(), prioridad.getValue());
            }
            switch(propiedad) {
                case "hora" -> fila.setHora(nombre.getText());
                case "lunes" -> fila.setLunes(nombre.getText());
                case "martes" -> fila.setMartes(nombre.getText());
                case "miercoles" -> fila.setMiercoles(nombre.getText());
                case "jueves" -> fila.setJueves(nombre.getText());
                case "viernes" -> fila.setViernes(nombre.getText());
                case "sabado" -> fila.setSabado(nombre.getText());
                case "domingo" -> fila.setDomingo(nombre.getText());
            }
            fila.setDescripcion(descripcion.getText());
            fila.setTag(tag.getText());
            fila.setPrioridad(prioridad.getValue());

            cell.getTableView().refresh();
            dialog.close();
        });

        Button cerrar = new Button("Cerrar");
        Style.Boton(cerrar,17);
    cerrar.setOnAction(e -> dialog.close());

    // Use default Label styling for cell-edit popups (keep them neutral)
    Label lblNom = new Label("Nombre:");
    Label lblDesc = new Label("Descripción:");
    Label lblTag = new Label("Tag:");
    Label lblPri = new Label("Prioridad:");
        box.getChildren().addAll(
            lblNom, nombre,
            lblDesc, descripcion,
            lblTag, tag,
            lblPri, prioridad,
            guardar, cerrar
        );

        Scene s = new Scene(box);
        try { s.getStylesheets().add(TablaUtils.class.getResource("/styles.css").toExternalForm()); } catch (Exception ex) {}
        s.setFill(Color.TRANSPARENT);
        dialog.setScene(s);
        dialog.setOnShown(ev -> {
            try {
                Window o = owner;
                dialog.setX(o.getX() + (o.getWidth() - dialog.getWidth()) / 2);
                dialog.setY(o.getY() + (o.getHeight() - dialog.getHeight()) / 2);
                box.setOpacity(0);
                box.setScaleX(0.95);
                box.setScaleY(0.95);
                FadeTransition ft = new FadeTransition(Duration.millis(160), box);
                ft.setFromValue(0);
                ft.setToValue(1);
                ScaleTransition st = new ScaleTransition(Duration.millis(160), box);
                st.setFromX(0.95);
                st.setFromY(0.95);
                st.setToX(1);
                st.setToY(1);
                new ParallelTransition(ft, st).play();
            } catch (Exception ex) {}
        });
        dialog.showAndWait();
    }

    public static class Agenda {
        public String contenido[][][]= new String[7][24][4];

        public Agenda(String hora) {
            // Inicializar propiedades visuales y la matriz interna en el constructor
            this.hora = new SimpleStringProperty(hora);
            this.nombre = new SimpleStringProperty("");
            this.descripcion = new SimpleStringProperty("");
            this.tag = new SimpleStringProperty("");
            this.prioridad = new SimpleStringProperty("");
            this.lunes = new SimpleStringProperty("");
            this.martes = new SimpleStringProperty("");
            this.miercoles = new SimpleStringProperty("");
            this.jueves = new SimpleStringProperty("");
            this.viernes = new SimpleStringProperty("");
            this.sabado = new SimpleStringProperty("");
            this.domingo = new SimpleStringProperty("");

            // Asegurarse de que la estructura interna no tenga nulls en sus subarreglos
            for (int d = 0; d < contenido.length; d++) {
                for (int h = 0; h < contenido[d].length; h++) {
                    if (contenido[d][h] == null) {
                        contenido[d][h] = new String[] {"", "", "", ""};
                    } else {
                        // garantizar que tenga longitud 4
                        if (contenido[d][h].length < 4) {
                            String[] tmp = new String[4];
                            for (int k = 0; k < 4; k++) tmp[k] = (k < contenido[d][h].length && contenido[d][h][k] != null) ? contenido[d][h][k] : "";
                            contenido[d][h] = tmp;
                        }
                    }
                }
            }
        }

        public void registrar(int dia, int hora, String nombre, String descripcion, String tag, String prioridad) {
            if (!validarIndices(dia, hora)) return;
            if (contenido[dia][hora] == null) contenido[dia][hora] = new String[] {"", "", "", ""};
            contenido[dia][hora][0] = nombre != null ? nombre : "";
            contenido[dia][hora][1] = descripcion != null ? descripcion : "";
            contenido[dia][hora][2] = tag != null ? tag : "";
            contenido[dia][hora][3] = prioridad != null ? prioridad : "";
            // Actualizar propiedades visibles para que la TableView muestre el texto cargado
            String display = nombre != null ? nombre : "";
            switch (dia) {
                case 0 -> this.lunes.set(display);
                case 1 -> this.martes.set(display);
                case 2 -> this.miercoles.set(display);
                case 3 -> this.jueves.set(display);
                case 4 -> this.viernes.set(display);
                case 5 -> this.sabado.set(display);
                case 6 -> this.domingo.set(display);
            }
            // Actualizar otras propiedades visibles
            this.setDescripcion(descripcion != null ? descripcion : "");
            this.setTag(tag != null ? tag : "");
            this.setPrioridad(prioridad != null ? prioridad : "");
        }

        public String[] obtener(int dia, int hora) {
            if (!validarIndices(dia, hora)) return new String[] {"", "", "", ""};
            if (contenido[dia][hora] == null) contenido[dia][hora] = new String[] {"", "", "", ""};
            // garantizar longitud 4
            if (contenido[dia][hora].length < 4) {
                String[] tmp = new String[4];
                for (int k = 0; k < 4; k++) tmp[k] = (k < contenido[dia][hora].length && contenido[dia][hora][k] != null) ? contenido[dia][hora][k] : "";
                contenido[dia][hora] = tmp;
            }
            return contenido[dia][hora];
        }

        private boolean validarIndices(int dia, int hora) {
            if (dia < 0 || dia >= contenido.length) return false;
            if (hora < 0 || hora >= contenido[0].length) return false;
            return true;
        }

        private final SimpleStringProperty nombre, hora, lunes, martes, miercoles, jueves, viernes, sabado, domingo;
        private final SimpleStringProperty descripcion, tag, prioridad;

        // Getters y Setters simplificados
        public String getHora() { return hora.get(); }
        public void setHora(String value) { hora.set(value); }
        public String getLunes() { return lunes.get(); }
        public void setLunes(String value) { lunes.set(value); }
        public String getMartes() { return martes.get(); }
        public void setMartes(String value) { martes.set(value); }
        public String getMiercoles() { return miercoles.get(); }
        public void setMiercoles(String value) { miercoles.set(value); }
        public String getJueves() { return jueves.get(); }
        public void setJueves(String value) { jueves.set(value); }
        public String getViernes() { return viernes.get(); }
        public void setViernes(String value) { viernes.set(value); }
        public String getSabado() { return sabado.get(); }
        public void setSabado(String value) { sabado.set(value); }
        public String getDomingo() { return domingo.get(); }
        public void setDomingo(String value) { domingo.set(value); }
        public String getDescripcion() { return descripcion.get(); }
        public void setDescripcion(String value) { descripcion.set(value); }
        public String getTag() { return tag.get(); }
        public void setTag(String value) { tag.set(value); }
        public String getPrioridad() { return prioridad.get(); }
        public void setPrioridad(String value) { prioridad.set(value); }
        public String getNombre() { return nombre.get(); }
        public void setNombre(String value) { nombre.set(value); }
    }

    public static TableColumn<Agenda, String> crearColumnaEditable(String titulo, String propiedad) {
        TableColumn<Agenda, String> col = new TableColumn<>(titulo);
        col.setCellValueFactory(new PropertyValueFactory<>(propiedad));
        col.setCellFactory(column -> {
            TableCell<Agenda, String> cell = new TextFieldTableCell<>();
            cell.setOnMouseClicked(e -> {
                // only show editable popup when the table itself is editable
                if (!cell.isEmpty() && cell.getTableView() != null && cell.getTableView().isEditable()) {
                    mostrarPopupCelda(cell, propiedad);
                }
            });
            return cell;
        });
        return col;
    }

    public static TableView<Agenda> crearTablaFija(double ancho, double alto) {
    TableView<Agenda> tabla = new TableView<>();
    // apply agenda table style class so styles.css can target it
    tabla.getStyleClass().add("agenda-table");

        // Tamaños fijos para la tabla
        tabla.setPrefWidth(ancho);
        tabla.setPrefHeight(alto);
        tabla.setMinWidth(ancho);
        tabla.setMinHeight(alto);
        tabla.setMaxWidth(ancho);
        tabla.setMaxHeight(alto);
        
        tabla.setEditable(true);
        tabla.getSelectionModel().setCellSelectionEnabled(true);

        TableColumn<Agenda, String> colNombre = crearColumnaEditable("Hora", "hora");
        TableColumn<Agenda, String> colLunes = crearColumnaEditable("Lunes", "lunes");
        TableColumn<Agenda, String> colMartes = crearColumnaEditable("Martes", "martes");
        TableColumn<Agenda, String> colMiercoles = crearColumnaEditable("Miércoles", "miercoles");
        TableColumn<Agenda, String> colJueves = crearColumnaEditable("Jueves", "jueves");
        TableColumn<Agenda, String> colViernes = crearColumnaEditable("Viernes", "viernes");
        TableColumn<Agenda, String> colSabado = crearColumnaEditable("Sábado", "sabado");
        TableColumn<Agenda, String> colDomingo = crearColumnaEditable("Domingo", "domingo");

        tabla.getColumns().addAll(colNombre, colLunes, colMartes, colMiercoles,
                                  colJueves, colViernes, colSabado, colDomingo);

        // Tamaño específico para cada columna
        colNombre.setPrefWidth(80);
        colLunes.setPrefWidth(100);
        colMartes.setPrefWidth(100);
        colMiercoles.setPrefWidth(100);
        colJueves.setPrefWidth(100);
        colViernes.setPrefWidth(100);
        colSabado.setPrefWidth(100);
        colDomingo.setPrefWidth(100);

        // Fijar altura de filas
        tabla.setFixedCellSize(25); // altura de fila en píxeles
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        return tabla;
    }
}
