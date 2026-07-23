package app.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;

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
}