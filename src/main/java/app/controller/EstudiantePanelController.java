package app.controller;

import bdd.EstudianteDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import modelo.Estudiante;

import java.sql.SQLException;
import java.util.Optional;

public class EstudiantePanelController {

    @FXML private TableView<Estudiante> tablaEstudiantes;
    @FXML private TableColumn<Estudiante, String> colId;
    @FXML private TableColumn<Estudiante, String> colNombre;
    @FXML private TableColumn<Estudiante, String> colApellido;
    @FXML private TableColumn<Estudiante, String> colCarrera;
    @FXML private TableColumn<Estudiante, String> colCategoriaPago;
    @FXML private TableColumn<Estudiante, String> colNacionalidad;
    @FXML private TableColumn<Estudiante, String> colDireccion;
    @FXML private TextField txtFiltroId;

    private final EstudianteDAO estudianteDAO = new EstudianteDAO();
    private final ObservableList<Estudiante> datosMaestros = FXCollections.observableArrayList();
    private FilteredList<Estudiante> datosFiltrados;

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colCarrera.setCellValueFactory(new PropertyValueFactory<>("idCarrera"));
        colCategoriaPago.setCellValueFactory(new PropertyValueFactory<>("idCategoriaPago"));
        colNacionalidad.setCellValueFactory(new PropertyValueFactory<>("idNacionalidad"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));

        datosFiltrados = new FilteredList<>(datosMaestros, e -> true);
        tablaEstudiantes.setItems(datosFiltrados);

        txtFiltroId.textProperty().addListener((obs, viejo, nuevo) -> {
            String filtro = nuevo == null ? "" : nuevo.trim().toLowerCase();
            datosFiltrados.setPredicate(estudiante ->
                    filtro.isEmpty() || estudiante.getId().toLowerCase().contains(filtro));
        });

        cargarDatos();
    }

    private void cargarDatos() {
        try {
            datosMaestros.setAll(estudianteDAO.listarTodos());
        } catch (SQLException e) {
            mostrarError("No se pudo cargar la lista de estudiantes", e);
        }
    }

    @FXML
    private void onAnadir() {
        Optional<Estudiante> resultado = mostrarFormulario(null);
        resultado.ifPresent(nuevo -> {
            try {
                estudianteDAO.insertar(nuevo);
                cargarDatos();
            } catch (SQLException e) {
                mostrarError("No se pudo agregar el estudiante", e);
            }
        });
    }

    @FXML
    private void onModificar() {
        Estudiante seleccionado = tablaEstudiantes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAviso("Selecciona un estudiante en la tabla para modificar.");
            return;
        }
        Optional<Estudiante> resultado = mostrarFormulario(seleccionado);
        resultado.ifPresent(actualizado -> {
            try {
                estudianteDAO.actualizar(actualizado);
                cargarDatos();
            } catch (SQLException e) {
                mostrarError("No se pudo modificar el estudiante", e);
            }
        });
    }

    @FXML
    private void onEliminar() {
        Estudiante seleccionado = tablaEstudiantes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAviso("Selecciona un estudiante en la tabla para eliminar.");
            return;
        }
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminacion");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Eliminar al estudiante " + seleccionado.getId() + " - "
                + seleccionado.getNombre() + " " + seleccionado.getApellido() + "?");

        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                estudianteDAO.eliminar(seleccionado.getId());
                cargarDatos();
            } catch (SQLException e) {
                mostrarError("No se pudo eliminar el estudiante", e);
            }
        }
    }

    /**
     * Formulario simple de alta/edicion. Si existente es null es un alta;
     * si viene un Estudiante, precarga sus datos y bloquea el ID (es PK).
     * Los campos idCarrera / idCategoriaPago / idNacionalidad son texto libre
     * por ahora (todavia no hay combos con los catalogos correspondientes).
     */
    private Optional<Estudiante> mostrarFormulario(Estudiante existente) {
        boolean esEdicion = existente != null;

        Dialog<Estudiante> dialog = new Dialog<>();
        dialog.setTitle(esEdicion ? "Modificar estudiante" : "Anadir estudiante");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField txtId = new TextField(esEdicion ? existente.getId() : "");
        txtId.setDisable(esEdicion);
        TextField txtNombre = new TextField(esEdicion ? existente.getNombre() : "");
        TextField txtApellido = new TextField(esEdicion ? existente.getApellido() : "");
        TextField txtCarrera = new TextField(esEdicion ? existente.getIdCarrera() : "");
        TextField txtCategoriaPago = new TextField(esEdicion ? existente.getIdCategoriaPago() : "");
        TextField txtNacionalidad = new TextField(esEdicion ? existente.getIdNacionalidad() : "");
        TextField txtDireccion = new TextField(esEdicion ? existente.getDireccion() : "");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 10, 10));

        String[] etiquetas = {"ID:", "Nombre:", "Apellido:", "Carrera (id):",
                "Categoria pago (id):", "Nacionalidad (id):", "Direccion:"};
        TextField[] campos = {txtId, txtNombre, txtApellido, txtCarrera,
                txtCategoriaPago, txtNacionalidad, txtDireccion};

        for (int i = 0; i < etiquetas.length; i++) {
            grid.add(new Label(etiquetas[i]), 0, i);
            grid.add(campos[i], 1, i);
        }

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(boton -> {
            if (boton == ButtonType.OK) {
                return new Estudiante(
                        txtId.getText().trim(),
                        txtNombre.getText().trim(),
                        txtApellido.getText().trim(),
                        txtCarrera.getText().trim(),
                        txtCategoriaPago.getText().trim(),
                        txtNacionalidad.getText().trim(),
                        txtDireccion.getText().trim()
                );
            }
            return null;
        });

        return dialog.showAndWait();
    }

    private void mostrarAviso(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarError(String mensaje, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(mensaje);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }
}