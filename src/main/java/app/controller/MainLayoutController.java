package app.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

/**
 * Controlador del layout principal (main-layout.fxml).
 * Cada opcion del menu carga su propio FXML dentro del area de contenido
 * central (contentArea). Las que todavia no tienen pantalla real muestran
 * un placeholder temporal.
 */
public class MainLayoutController {

    @FXML
    private StackPane contentArea;

    @FXML
    private void onEstudiante() {
        cargarPanel("/app/estudiante-panel.fxml");
    }

    @FXML
    private void onAsignatura() {
        cargarPanel("/app/asignatura-panel.fxml");
    }

    @FXML
    private void onHorarioGrupo() {
        cargarPanel("/app/horario-grupo-panel.fxml");
    }

    @FXML
    private void onInscripcion() {
        cargarPanel("/app/inscripcion-panel.fxml");
    }

    /**
     * El horario cuadriculado se genera desde el panel de Estudiante
     * (selecciona un estudiante + período y pulsa "Ver Horario"), así que
     * esta opción del menú simplemente lleva ahí en vez de duplicar pantalla.
     */
    @FXML
    private void onHorarioCuadriculado() {
        cargarPanel("/app/estudiante-panel.fxml");
    }

    @FXML
    private void onInforme() {
        mostrarPlaceholder("Informe de inscripcion");
    }

    private void cargarPanel(String rutaFxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFxml));
            Parent panel = loader.load();
            contentArea.getChildren().setAll(panel);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error al cargar panel");
            alert.setHeaderText(null);
            alert.setContentText("No se pudo cargar " + rutaFxml + ": " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void mostrarPlaceholder(String nombre) {
        Label titulo = new Label(nombre);
        titulo.getStyleClass().add("panel-titulo");

        VBox panel = new VBox(12, titulo);
        panel.setPadding(new Insets(24));

        contentArea.getChildren().setAll(panel);
    }
}