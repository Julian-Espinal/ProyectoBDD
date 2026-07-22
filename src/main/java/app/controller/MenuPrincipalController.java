package app.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;

/**
 * Controlador del menu principal (menu-principal.fxml).
 * Cada metodo se conectara mas adelante a su propia ventana/FXML;
 * por ahora solo muestra un aviso de "en construccion".
 */
public class MenuPrincipalController {

    @javafx.fxml.FXML
    private void onCrudEstudiante(ActionEvent event) {
        mostrarPendiente("CRUD Estudiante / Asignatura");
    }

    @javafx.fxml.FXML
    private void onHorarioGrupo(ActionEvent event) {
        mostrarPendiente("Asignar horario a un grupo");
    }

    @javafx.fxml.FXML
    private void onInscripcion(ActionEvent event) {
        mostrarPendiente("Inscribir grupos a un estudiante");
    }

    @javafx.fxml.FXML
    private void onHorarioCuadriculado(ActionEvent event) {
        mostrarPendiente("Horario cuadriculado");
    }

    @javafx.fxml.FXML
    private void onInforme(ActionEvent event) {
        mostrarPendiente("Informe de inscripcion");
    }

    private void mostrarPendiente(String modulo) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("En construccion");
        alert.setHeaderText(null);
        alert.setContentText("El modulo \"" + modulo + "\" todavia no esta conectado.");
        alert.showAndWait();
    }
}