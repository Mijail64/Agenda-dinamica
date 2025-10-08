package com.mijail64;

import javafx.util.Duration;
import javafx.scene.image.Image; // Image
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;    // Background, BackgroundImage, BackgroundRepeat, BackgroundPosition, BackgroundSize
import javafx.application.*;   // Application, Platform, etc.
import javafx.stage.*;          // Stage, Window
import javafx.scene.*;          // Scene, Node
import javafx.scene.control.*;  // Label, Button, TextField, etc.
import javafx.scene.layout.*;   // VBox, HBox, GridPane
import javafx.scene.shape.*;    // Circle, Rectangle, etc.
import javafx.scene.paint.*;    // Color, Paint

import com.mijail64.Style;
import com.mijail64.TablaUtils;
import java.util.List;
import java.util.Optional;

import javafx.animation.*; 
import javafx.geometry.Pos;
import com.mijail64.TablaUtils.Agenda;
import java.sql.SQLException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import javafx.stage.WindowEvent;


public class App extends Application 
{
    
    @Override
    public void start(Stage stage) {
        //Elementos
    Label labelM = new Label("¡Hola Agenda!");
    Button btnCreate = new Button("Crear");
    Button btnUpdate = new Button("Actualizar");
    Button btnView = new Button("Ver");

    //Fila de botones
    HBox fila = new HBox(20); // 20 = espacio entre botones
    fila.setAlignment(Pos.CENTER); // opcional, centra los botones
    fila.getChildren().addAll(btnCreate, btnUpdate, btnView);
        VBox root = new VBox(20); // 20 = espacio vertical entre elementos
        root.setAlignment(Pos.CENTER);
        

        //---------------------Estilos---------------------
    Style.Label(labelM, 100);
    // Aplicar estilo a los botones reales
    Style.Boton(btnCreate, 32);
    Style.Boton(btnUpdate, 32);
    Style.Boton(btnView, 32);

    Style.AnimacionBoton(btnCreate);
    Style.AnimacionBoton(btnUpdate);
    Style.AnimacionBoton(btnView);
        
        
        //---------------------Eventos---------------------
        // Evento Crear: mostrar popup personalizado con campo nombre + imagen a la derecha y descripción abajo
        btnCreate.setOnAction(e -> {
            Stage owner = (Stage)((Node)e.getSource()).getScene().getWindow();
            try {
                AgendaStorage storage = new AgendaStorage("agenda.db");

                Stage dialog = new Stage();
                dialog.initOwner(owner);
                dialog.initModality(Modality.WINDOW_MODAL);
                dialog.initStyle(StageStyle.TRANSPARENT);

                VBox box = new VBox(10);
                box.setAlignment(Pos.CENTER);
                Style.PopupBox(box);

                Label title = new Label("Crear nueva agenda");
                // Use SmallLabel styling per request
                Style.SmallLabel(title, 18);

                // Header: simple title only (image removed)
                StackPane header = new StackPane();
                header.setPrefWidth(480);
                header.getChildren().add(title);

                // HBox: only the TextField for name (we moved the image to the header)
                TextField nameField = new TextField();
                nameField.setPromptText("Nombre de la nueva agenda");
                HBox row = new HBox(8, nameField);
                row.setAlignment(Pos.CENTER);

                TextArea descArea = new TextArea();
                descArea.setPromptText("Descripción (opcional)");
                descArea.setPrefRowCount(3);

                Button save = new Button("Crear");
                Style.Boton(save, 14);
                Style.AnimacionBoton(save);
                Button cancel = new Button("Cancelar");
                Style.Boton(cancel, 14);
                Style.AnimacionBoton(cancel);

                HBox actions = new HBox(10, save, cancel);
                actions.setAlignment(Pos.CENTER);

                box.getChildren().addAll(title, row, descArea, actions);

                Scene s = new Scene(box);
                try { s.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm()); } catch (Exception _ex) {}
                s.setFill(Color.TRANSPARENT);
                dialog.setScene(s);

                cancel.setOnAction(ev -> dialog.close());

                save.setOnAction(ev -> {
                    String name = nameField.getText() != null ? nameField.getText().trim() : "";
                    String desc = descArea.getText() != null ? descArea.getText().trim() : "";
                    if (name.isEmpty()) { showStyledDialog(owner, "Error", "El nombre no puede estar vacío"); return; }
                    try {
                        List<AgendaStorage.AgendaRecord> list = storage.listAgendas();
                        boolean exists = list.stream().anyMatch(a -> a.name.equalsIgnoreCase(name));
                        if (exists) { showStyledDialog(owner, "Error", "Ya existe una agenda con ese nombre"); return; }
                        int id;
                        try { id = storage.createAgenda(name, desc); }
                        catch (SQLException ex) { showStyledDialog(owner, "Error", "No se pudo crear la agenda: "+ex.getMessage()); return; }
                        dialog.close();
                        abrirMenuCrear(owner, id, true, false);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        showStyledDialog(owner, "Error", "No se pudo crear la agenda: " + ex.getMessage());
                    }
                });

                // show centered with entrance animation
                // use centralized helper for centering + entrance animation
                showAnimatedDialog(owner, dialog, box, true);

            } catch (SQLException ex) {
                ex.printStackTrace();
                showStyledDialog((Stage)((Node)e.getSource()).getScene().getWindow(), "Error", "No se pudo crear la agenda: " + ex.getMessage());
            }
        });

        // Evento Actualizar: mostrar lista y abrir editor para la seleccionada
        btnUpdate.setOnAction(e -> {
            Stage owner = (Stage)((Node)e.getSource()).getScene().getWindow();
            try {
                AgendaStorage storage = new AgendaStorage("agenda.db");
                List<AgendaStorage.AgendaRecord> list = storage.listAgendas();
                if (list.isEmpty()) { showStyledDialog(owner, "Info", "No hay agendas disponibles"); return; }
                AgendaStorage.AgendaRecord sel = showAgendaSelectionDialog(owner, list, "Selecciona agenda para editar", storage, true);
                if (sel != null) abrirMenuCrear(owner, sel.id, true, true);
            } catch (SQLException ex) {
                ex.printStackTrace();
                showStyledDialog((Stage)((Node)e.getSource()).getScene().getWindow(), "Error", "No se pudo listar agendas: " + ex.getMessage());
            }
        });

        // Evento Ver: mostrar lista y abrir editor en modo ver
        btnView.setOnAction(e -> {
            Stage owner = (Stage)((Node)e.getSource()).getScene().getWindow();
            try {
                AgendaStorage storage = new AgendaStorage("agenda.db");
                List<AgendaStorage.AgendaRecord> list = storage.listAgendas();
                if (list.isEmpty()) { showStyledDialog(owner, "Info", "No hay agendas disponibles"); return; }
                AgendaStorage.AgendaRecord sel = showAgendaSelectionDialog(owner, list, "Selecciona agenda para ver", storage, false);
                if (sel != null) abrirMenuCrear(owner, sel.id, false, true);
            } catch (SQLException ex) {
                ex.printStackTrace();
                showStyledDialog((Stage)((Node)e.getSource()).getScene().getWindow(), "Error", "No se pudo listar agendas: " + ex.getMessage());
            }
        });



        //Layout simple
        

        root.getChildren().add(labelM);
        root.getChildren().add(fila); // añadimos la fila al layout principal
        //------fondo-------
    java.net.URL elyJpg = getClass().getResource("/Ely.jpg");
    java.net.URL elyPng = getClass().getResource("/Ely.png");
    Image img = null;
    if (elyJpg != null) img = new Image(elyJpg.toExternalForm());
    else if (elyPng != null) img = new Image(elyPng.toExternalForm());
    else img = new Image(getClass().getResource("/Ely.png").toExternalForm());
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
        // Cargar hoja de estilos global
        try {
            String css = getClass().getResource("/styles.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception ex) {
            // si no se encuentra, no hacemos nada
        }

        // Configuramos y mostramos la ventana
        stage.setTitle("Mi Agenda - JavaFX");
        stage.setScene(scene);
        stage.show();
    }

    private void abrirMenuCrear(Stage stage, int agendaId, boolean editable, boolean autoLoad) {
        Scene escenaPrincipal = stage.getScene();
        VBox nuevaRoot = new VBox(20);
        nuevaRoot.setAlignment(Pos.CENTER);
        // Determine proper title depending on flow: create / update / view
        String titleText = "Menú Crear Agenda";
        if (!autoLoad && editable) {
            // create flow: use 'creando:(agenda)'
            try {
                AgendaStorage storage = new AgendaStorage("agenda.db");
                java.util.List<AgendaStorage.AgendaRecord> list = storage.listAgendas();
                String name = null;
                for (AgendaStorage.AgendaRecord a : list) { if (a.id == agendaId) { name = a.name; break; } }
                titleText = name != null ? ("creando: " + name) : "creando:(agenda)";
            } catch (Exception ex) {
                titleText = "creando:(agenda)";
            }
        } else if (autoLoad && editable) {
            // update flow: 'actualizar: (agenda)'
            try {
                AgendaStorage storage = new AgendaStorage("agenda.db");
                java.util.List<AgendaStorage.AgendaRecord> list = storage.listAgendas();
                String name = null;
                for (AgendaStorage.AgendaRecord a : list) { if (a.id == agendaId) { name = a.name; break; } }
                titleText = name != null ? ("actualizar: " + name) : "actualizar: (agenda)";
            } catch (Exception ex) {
                titleText = "actualizar: (agenda)";
            }
        } else if (autoLoad && !editable) {
            // view flow: 'ver: (agenda)'
            try {
                AgendaStorage storage = new AgendaStorage("agenda.db");
                java.util.List<AgendaStorage.AgendaRecord> list = storage.listAgendas();
                String name = null;
                for (AgendaStorage.AgendaRecord a : list) { if (a.id == agendaId) { name = a.name; break; } }
                titleText = name != null ? ("Ver: " + name) : "ver: (agenda)";
            } catch (Exception ex) {
                titleText = "ver: (agenda)";
            }
        }

        Label nuevaLabel = new Label(titleText);
        Style.Label(nuevaLabel, 50);
    Button volverBtn = new Button("Volver");
    Style.Boton(volverBtn, 24);
    Style.AnimacionBoton(volverBtn);

    // Botones para guardar / cargar
    Button guardarBtn = new Button("Guardar");
    Style.Boton(guardarBtn, 18);
    Style.AnimacionBoton(guardarBtn);

    Button cargarBtn = new Button("Cargar");
    Style.Boton(cargarBtn, 18);
    Style.AnimacionBoton(cargarBtn);

        // Tabla
        TableView<TablaUtils.Agenda> tabla = TablaUtils.crearTablaFija(761, 400);
        // apply styling immediately so the table already looks correct when the scene is shown
        if (!tabla.getStyleClass().contains("agenda-table")) tabla.getStyleClass().add("agenda-table");
        if (!editable && !tabla.getStyleClass().contains("readonly")) tabla.getStyleClass().add("readonly");
        Style.estilizarTabla(tabla);
        // keep a later run to tweak lookup-based styles once the scene graph is ready
        Platform.runLater(() -> {
            if (!tabla.getStyleClass().contains("agenda-table")) tabla.getStyleClass().add("agenda-table");
            if (!editable && !tabla.getStyleClass().contains("readonly")) tabla.getStyleClass().add("readonly");
            Style.estilizarTabla(tabla);
        });
        // si no es editable, desactivar edición y selección de celda
        if (!editable) {
            tabla.setEditable(false);
            tabla.getSelectionModel().setCellSelectionEnabled(false);
            tabla.getStyleClass().add("readonly");
        }
        // Añadir filas con formato de hora 00:00 - 23:00
        for (int i = 0; i < 24; i++) {
            String horaFormateada = String.format("%02d:00", i);
            tabla.getItems().add(new TablaUtils.Agenda(horaFormateada));
        }

        // Aplicar clip redondeado al fondo del encabezado para que coincida con los bordes de la tabla
        Platform.runLater(() -> {
            try {
                Node headerBg = tabla.lookup(".column-header-background");
                if (headerBg instanceof Region) {
                    Region r = (Region) headerBg;
                    Rectangle clip = new Rectangle();
                    // ajustar los arcs para que coincidan con el radio visual de la tabla
                    clip.setArcWidth(20);
                    clip.setArcHeight(20);
                    // ligar tamaño del clip al tamaño del nodo
                    clip.widthProperty().bind(r.widthProperty());
                    clip.heightProperty().bind(r.heightProperty());
                    r.setClip(clip);

                    // opcional: si quieres también forzar un fondo transparente directamente
                    r.setStyle("-fx-background-color: transparent;");
                }
            } catch (Exception ex) {
                // no hacer nada si falla el lookup
            }
        });

    // Añadir botones de guardar/cargar en una línea antes del volver
    HBox filaAcciones = new HBox(10);
    filaAcciones.setAlignment(Pos.CENTER);
    // Si se carga automáticamente, ocultamos el botón Cargar para evitar confusión
    if (autoLoad) {
        filaAcciones.getChildren().addAll(guardarBtn, volverBtn);
    } else {
        filaAcciones.getChildren().addAll(guardarBtn, cargarBtn, volverBtn);
    }

    nuevaRoot.getChildren().addAll(nuevaLabel, tabla, filaAcciones);

        // Fondo
    java.net.URL elyJpg2 = getClass().getResource("/Ely.jpg");
    java.net.URL elyPng2 = getClass().getResource("/Ely.png");
    Image img2 = null;
    if (elyJpg2 != null) img2 = new Image(elyJpg2.toExternalForm());
    else if (elyPng2 != null) img2 = new Image(elyPng2.toExternalForm());
    else img2 = new Image(getClass().getResource("/Ely.png").toExternalForm());
        BackgroundImage bg = new BackgroundImage(
            img2,
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
        );
        nuevaRoot.setBackground(new Background(bg));

        Scene nuevaScene = new Scene(nuevaRoot, 1000, 700);
        try {
            String css = getClass().getResource("/styles.css").toExternalForm();
            nuevaScene.getStylesheets().add(css);
        } catch (Exception ex) {}

        // Cambio de escena con pausa y animación de entrada (fade + scale) similar a los popups
        PauseTransition pausa = new PauseTransition(Duration.millis(300));
        pausa.setOnFinished(ev -> {
            stage.setScene(nuevaScene);
            try {
                nuevaRoot.setOpacity(0);
                nuevaRoot.setScaleX(0.95);
                nuevaRoot.setScaleY(0.95);
                FadeTransition ft = new FadeTransition(javafx.util.Duration.millis(220), nuevaRoot);
                ft.setFromValue(0);
                ft.setToValue(1);
                ScaleTransition st = new ScaleTransition(javafx.util.Duration.millis(220), nuevaRoot);
                st.setFromX(0.95);
                st.setFromY(0.95);
                st.setToX(1);
                st.setToY(1);
                new javafx.animation.ParallelTransition(ft, st).play();
            } catch (Exception ex) {
                // swallow — animation best-effort
            }
        });
        pausa.play();

        // once the scene's window is available and shown, force inline styling on the table
        nuevaScene.windowProperty().addListener((obs, oldW, newW) -> {
            if (newW != null) {
                newW.addEventHandler(WindowEvent.WINDOW_SHOWN, we -> {
                    Platform.runLater(() -> applyInlineTableStyling(tabla));
                });
            }
        });

        volverBtn.setOnAction(ev -> stage.setScene(escenaPrincipal));

    // Acción Guardar: construye la matriz [7][24][4] a partir de la tabla y guarda en SQLite
        guardarBtn.setOnAction(ev -> {
            boolean confirm = showConfirmationDialog(stage, "Confirmar guardado", "¿Deseas guardar la agenda actual?");
            if (!confirm) return;
            try {
                AgendaStorage storage = new AgendaStorage("agenda.db");
                String[][][] matrix = new String[7][24][4];
                // inicializar
                for (int d = 0; d < 7; d++) for (int h = 0; h < 24; h++) matrix[d][h] = new String[] {"", "", "", ""};

                for (int h = 0; h < tabla.getItems().size() && h < 24; h++) {
                    Agenda fila = tabla.getItems().get(h);
                    for (int d = 0; d < 7; d++) {
                        String[] datos = fila.obtener(d, h);
                        if (datos == null) datos = new String[] {"", "", "", ""};
                        // copiar valores (garantizar longitud 4)
                        for (int k = 0; k < 4; k++) matrix[d][h][k] = (k < datos.length && datos[k] != null) ? datos[k] : "";
                    }
                }
                storage.saveMatrix(agendaId, matrix);
                showStyledDialog(stage, "Guardado", "Agenda guardada en agenda.db");
            } catch (SQLException ex) {
                ex.printStackTrace();
                showStyledDialog(stage, "Error", "Error guardando agenda: " + ex.getMessage());
            }
        });

        // Acción Cargar: lee la matriz desde SQLite y actualiza la tabla en memoria
        cargarBtn.setOnAction(ev -> {
            boolean confirm = showConfirmationDialog(stage, "Confirmar carga", "¿Deseas cargar la agenda desde disco? Esto sobrescribirá los datos actuales.");
            if (!confirm) return;
            try {
                AgendaStorage storage = new AgendaStorage("agenda.db");
                String[][][] matrix = storage.loadMatrix(agendaId);
                for (int h = 0; h < tabla.getItems().size() && h < 24; h++) {
                    Agenda fila = tabla.getItems().get(h);
                    for (int d = 0; d < 7; d++) {
                        String[] cell = matrix[d][h];
                        if (cell == null) cell = new String[] {"", "", "", ""};
                        fila.registrar(d, h, cell[0], cell[1], cell[2], cell[3]);
                    }
                }
                tabla.refresh();
                showStyledDialog(stage, "Cargado", "Agenda cargada desde agenda.db");
            } catch (SQLException ex) {
                ex.printStackTrace();
                showStyledDialog(stage, "Error", "Error cargando agenda: " + ex.getMessage());
            }
        });

        // Si se solicita autoload, realizar la carga ahora y evitar botón
        if (autoLoad) {
            try {
                AgendaStorage storage = new AgendaStorage("agenda.db");
                String[][][] matrix = storage.loadMatrix(agendaId);
                for (int h = 0; h < tabla.getItems().size() && h < 24; h++) {
                    Agenda fila = tabla.getItems().get(h);
                    for (int d = 0; d < 7; d++) {
                        String[] cell = matrix[d][h];
                        if (cell == null) cell = new String[] {"", "", "", ""};
                        fila.registrar(d, h, cell[0], cell[1], cell[2], cell[3]);
                    }
                }
                tabla.refresh();
            } catch (SQLException ex) {
                ex.printStackTrace();
                showStyledDialog(stage, "Error", "Error cargando agenda al abrir: " + ex.getMessage());
            }
        }

        // En modo vista (no editable) queremos mostrar detalles cuando se seleccione una fila
        if (!editable) {
            // In view mode, open the details popup immediately when a day cell is clicked
            tabla.setEditable(false);
            tabla.getSelectionModel().setCellSelectionEnabled(true);
            tabla.setOnMouseClicked(ev -> {
                if (tabla.getSelectionModel().getSelectedCells().isEmpty()) return;
                TablePosition<?,?> pos = tabla.getSelectionModel().getSelectedCells().get(0);
                if (pos == null) return;
                int row = pos.getRow();
                int col = pos.getColumn();
                // column 0 is Hora; days are columns 1..7
                if (col >= 1 && col <= 7 && row >= 0 && row < tabla.getItems().size()) {
                    int dayIndex = col - 1;
                    TablaUtils.Agenda fila = tabla.getItems().get(row);
                    String[] datos = fila.obtener(dayIndex, row);
                    String diaName = switch (dayIndex) {
                        case 0 -> "Lunes"; case 1 -> "Martes"; case 2 -> "Miércoles";
                        case 3 -> "Jueves"; case 4 -> "Viernes"; case 5 -> "Sábado"; default -> "Domingo";
                    };
                    String horaLabel = fila.getHora();
                    showCellDetailsPopup((Stage) tabla.getScene().getWindow(), diaName, horaLabel, datos);
                }
            });
        }
    }

    private AgendaStorage.AgendaRecord showAgendaSelectionDialog(Stage owner, List<AgendaStorage.AgendaRecord> list, String title, AgendaStorage storage, boolean allowModify) {
        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initStyle(StageStyle.TRANSPARENT);

        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        Style.PopupBox(box);

    Label lbl = new Label(title);
    // Use compact small label for selection dialogs to match popup labeling preference
    Style.SmallLabel(lbl, 16);

        ListView<String> lv = new ListView<>();
    lv.getStyleClass().add("agenda-list");
        for (AgendaStorage.AgendaRecord a : list) lv.getItems().add(a.id + ": " + a.name + (a.description != null && !a.description.isEmpty() ? " - " + a.description : ""));
        lv.setPrefSize(400, 200);

    Button ok = new Button("Seleccionar");
        Style.Boton(ok, 14);
        Style.AnimacionBoton(ok);
    Button delete = new Button("Eliminar");
    Style.Boton(delete, 14);

    Button rename = new Button("Renombrar");
    Style.Boton(rename, 14);
    Style.AnimacionBoton(rename);

    Button cancel = new Button("Cancelar");
    Style.Boton(cancel, 14);
    Style.AnimacionBoton(cancel);

        final AgendaStorage.AgendaRecord[] selected = new AgendaStorage.AgendaRecord[1];
        ok.setOnAction(e -> {
            int idx = lv.getSelectionModel().getSelectedIndex();
            if (idx >= 0) selected[0] = list.get(idx);
            dialog.close();
        });
        delete.setOnAction(e -> {
            int idx = lv.getSelectionModel().getSelectedIndex();
            if (idx < 0) return;
            AgendaStorage.AgendaRecord a = list.get(idx);
            boolean conf = showConfirmationDialog(owner, "Confirmar eliminación", "Eliminar agenda '" + a.name + "'? Esto borrará sus celdas.");
            if (!conf) return;
            try {
                storage.deleteAgenda(a.id);
                lv.getItems().remove(idx);
                list.remove(idx);
            } catch (SQLException ex) {
                ex.printStackTrace();
                showStyledDialog(owner, "Error", "No se pudo eliminar: " + ex.getMessage());
            }
        });
        rename.setOnAction(e -> {
            int idx = lv.getSelectionModel().getSelectedIndex();
            if (idx < 0) return;
            AgendaStorage.AgendaRecord a = list.get(idx);
            TextInputDialog td = new TextInputDialog(a.name);
            td.initOwner(owner);
            td.setHeaderText("Nuevo nombre para la agenda");
            Optional<String> r = td.showAndWait();
            if (r.isEmpty() || r.get().trim().isEmpty()) return;
            String newName = r.get().trim();
            try {
                storage.renameAgenda(a.id, newName);
                // actualizar lista y visual
                list.set(idx, new AgendaStorage.AgendaRecord(a.id, newName, a.description, a.createdAt));
                lv.getItems().set(idx, a.id + ": " + newName + (a.description != null && !a.description.isEmpty() ? " - " + a.description : ""));
            } catch (SQLException ex) {
                ex.printStackTrace();
                showStyledDialog(owner, "Error", "No se pudo renombrar: " + ex.getMessage());
            }
        });
        cancel.setOnAction(e -> dialog.close());

        // si no permitimos modificar, ocultamos rename/delete
        if (!allowModify) {
            rename.setVisible(false);
            delete.setVisible(false);
        }

    HBox actions = new HBox(10, ok, rename, delete, cancel);
        actions.setAlignment(Pos.CENTER);
        box.getChildren().addAll(lbl, lv, actions);

        Scene s = new Scene(box);
        try { s.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm()); } catch (Exception ex) {}
        s.setFill(Color.TRANSPARENT);
    dialog.setScene(s);
    // Use the centralized helper so the selection dialog is centered and uses
    // the same entrance animation as other popups in the app.
    showAnimatedDialog(owner, dialog, box, true);
        return selected[0];
    }

    private void showStyledDialog(Stage owner, String title, String message) {
        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setResizable(false);
        // Quitar la decoración de la ventana para que se vea como un popup
        dialog.initStyle(StageStyle.TRANSPARENT);

        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        Style.PopupBox(box);

        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #333;");
        Label lblMsg = new Label(message);
        lblMsg.setWrapText(true);
        lblMsg.setMaxWidth(400);

        Button ok = new Button("OK");
        Style.Boton(ok, 14);
        Style.AnimacionBoton(ok);
        ok.setOnAction(e -> dialog.close());

        box.getChildren().addAll(lblTitle, lblMsg, ok);

    Scene scene = new Scene(box);
    try { scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm()); } catch (Exception ex) {}
        // Hacer el fondo de la escena transparente y dejar el popup visual por medio del estilo aplicado a 'box'
        scene.setFill(Color.TRANSPARENT);
        dialog.setScene(scene);
        // Centrar el dialog respecto al owner cuando se muestre y reproducir animación de entrada
        showAnimatedDialog(owner, dialog, box, true);
    }

    // Actualiza los controles de detalle a partir de la fila y el día seleccionados
    private void actualizarDetallesPorDia(TableView<TablaUtils.Agenda> tabla, int filaIndex, int diaIndex, Label lNombre, TextArea lDescripcionArea, Label lTag, Label lPrioridad) {
        if (filaIndex < 0 || filaIndex >= tabla.getItems().size()) return;
        if (diaIndex < 0 || diaIndex >= 7) diaIndex = 0;
        TablaUtils.Agenda fila = tabla.getItems().get(filaIndex);
        String[] datos = fila.obtener(diaIndex, filaIndex);
        if (datos == null) datos = new String[] {"", "", "", ""};
        lNombre.setText("Nombre: " + (datos[0] != null ? datos[0] : ""));
        lDescripcionArea.setText(datos[1] != null ? datos[1] : "");
        lTag.setText("Tag: " + (datos[2] != null ? datos[2] : ""));
        lPrioridad.setText("Prioridad: " + (datos[3] != null ? datos[3] : ""));
    }

    private void showCellDetailsPopup(Stage owner, String dia, String hora, String[] datos) {
        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initStyle(StageStyle.TRANSPARENT);

        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        Style.PopupBox(box);

        Label title = new Label(dia + " - " + hora);
        title.setStyle("-fx-font-weight:bold; -fx-font-size:16px;");
    Label nombre = new Label("Nombre: " + (datos != null && datos.length>0 ? datos[0] : ""));
    TextArea descripcion = new TextArea(datos != null && datos.length>1 ? datos[1] : "");
        descripcion.setWrapText(true);
        descripcion.setEditable(false);
        descripcion.setPrefRowCount(6);
    Label tag = new Label("Tag: " + (datos != null && datos.length>2 ? datos[2] : ""));
    Label prioridad = new Label("Prioridad: " + (datos != null && datos.length>3 ? datos[3] : ""));

    // Apply compact small-label styling for consistency with other popups
    Style.SmallLabel(nombre, 12);
    Label descLabel = new Label("Descripción:");
    Style.SmallLabel(descLabel, 12);
    Style.SmallLabel(tag, 12);
    Style.SmallLabel(prioridad, 12);

        Button close = new Button("Cerrar");
        Style.Boton(close, 14);
        Style.AnimacionBoton(close);
        close.setOnAction(e -> dialog.close());

    box.getChildren().addAll(title, nombre, descLabel, descripcion, tag, prioridad, close);

        Scene s = new Scene(box);
        try { s.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm()); } catch (Exception ex) {}
        s.setFill(Color.TRANSPARENT);
        dialog.setScene(s);
        showAnimatedDialog(owner, dialog, box, true);
    }

    // Force inline styles on header, rows and scrollbar so the table matches the app theme
    private void applyInlineTableStyling(TableView<?> tabla) {
        try {
            // header background
            tabla.lookupAll(".column-header-background").forEach(node -> {
                node.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, rgba(250,235,245,0.6), rgba(255,224,240,0.4)); -fx-background-insets: 0; -fx-padding: 0;");
                if (node instanceof Region r) {
                    r.setClip(new Rectangle(r.getWidth(), r.getHeight(), 20, 20));
                }
            });

            // header labels
            tabla.lookupAll(".column-header").forEach(node -> node.setStyle("-fx-background-color: transparent; -fx-padding: 6px;"));

            // rows
            tabla.lookupAll(".table-row-cell").forEach(node -> node.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #f7f1f6, #ffeef8); -fx-background-insets: 0;"));

            // scrollbars (thumb)
            tabla.lookupAll(".scroll-bar:vertical").forEach(node -> {
                node.setStyle("-fx-background-color: transparent;");
                node.lookupAll(".thumb").forEach(t -> t.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #ffd6ea, #ffb0d9); -fx-background-radius:10px; -fx-border-color:#ffd6ea; -fx-border-width:1px;"));
            });
        } catch (Exception ex) {
            // swallow — best-effort styling
        }
    }

    /**
     * Muestra un diálogo de confirmación estilizado (Sí/No) y devuelve true si el usuario confirma.
     */
    private boolean showConfirmationDialog(Stage owner, String title, String message) {
        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setResizable(false);
        dialog.initStyle(StageStyle.TRANSPARENT);

        VBox box = new VBox(12);
        box.setAlignment(Pos.CENTER);
        Style.PopupBox(box);

        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #333;");
        Label lblMsg = new Label(message);
        lblMsg.setWrapText(true);
        lblMsg.setMaxWidth(380);

        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER);

        Button yes = new Button("Sí");
        Style.Boton(yes, 14);
        Style.AnimacionBoton(yes);
        Button no = new Button("No");
        Style.Boton(no, 14);
        Style.AnimacionBoton(no);

        final boolean[] result = new boolean[1];
        yes.setOnAction(e -> {
            result[0] = true;
            dialog.close();
        });
        no.setOnAction(e -> {
            result[0] = false;
            dialog.close();
        });

        actions.getChildren().addAll(yes, no);
        box.getChildren().addAll(lblTitle, lblMsg, actions);

    Scene scene = new Scene(box);
    try { scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm()); } catch (Exception ex) {}
    scene.setFill(Color.TRANSPARENT);
    dialog.setScene(scene);
        showAnimatedDialog(owner, dialog, box, true);
        return result[0];
    }



    public static void main(String[] args) {
        launch(args); // arranca la aplicación JavaFX
    }

    /**
     * Centra el dialog respecto a owner y reproduce una animación de entrada (fade + scale).
     * If wait==true uses showAndWait(), otherwise show().
     */
    private void showAnimatedDialog(Stage owner, Stage dialog, Region content, boolean wait) {
        dialog.setOnShown(ev -> {
            try {
                Window o = owner;
                dialog.setX(o.getX() + (o.getWidth() - dialog.getWidth()) / 2);
                dialog.setY(o.getY() + (o.getHeight() - dialog.getHeight()) / 2);
                content.setOpacity(0);
                content.setScaleX(0.92);
                content.setScaleY(0.92);
                FadeTransition ft = new FadeTransition(javafx.util.Duration.millis(180), content);
                ft.setFromValue(0);
                ft.setToValue(1);
                ScaleTransition st = new ScaleTransition(javafx.util.Duration.millis(180), content);
                st.setFromX(0.92);
                st.setFromY(0.92);
                st.setToX(1);
                st.setToY(1);
                new ParallelTransition(ft, st).play();
            } catch (Exception ex) {
                // swallow
            }
        });
        if (wait) dialog.showAndWait(); else dialog.show();
    }
}