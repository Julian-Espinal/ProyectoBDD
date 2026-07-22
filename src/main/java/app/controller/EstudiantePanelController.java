package app.controller;

import bdd.ConexionBDD;
import bdd.EstudianteDAO;
import bdd.HorarioClaseDAO;
import bdd.TablaResultado;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import modelo.Estudiante;
import javafx.beans.property.SimpleStringProperty;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.control.ComboBox;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import bdd.AsignaturaDAO;
import bdd.CarreraDAO;
import bdd.GrupoDAO;
import bdd.GrupoInscritoDAO;
import modelo.Asignatura;
import modelo.Carrera;
import modelo.Grupo;
import modelo.GrupoInscrito;
import javafx.scene.control.TableColumn;
import java.util.List;

import java.sql.SQLException;
import java.util.Optional;

public class EstudiantePanelController {
    @FXML private ComboBox<String> comboPeriodo;
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
    private final CarreraDAO carreraDAO = new CarreraDAO();
    private final GrupoInscritoDAO grupoInscritoDAO = new GrupoInscritoDAO();
    private final GrupoDAO grupoDAO = new GrupoDAO();
    private final AsignaturaDAO asignaturaDAO = new AsignaturaDAO();
    private final HorarioClaseDAO horarioClaseDAO = new HorarioClaseDAO();

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colCarrera.setCellValueFactory(new PropertyValueFactory<>("idCarrera"));
        colCategoriaPago.setCellValueFactory(new PropertyValueFactory<>("idCategoriaPago"));
        colNacionalidad.setCellValueFactory(new PropertyValueFactory<>("idNacionalidad"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        cargarPeriodos();
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

    @FXML
    private void onVerHorario() {

        Estudiante estudiante = tablaEstudiantes.getSelectionModel().getSelectedItem();

        if (estudiante == null) {
            mostrarAviso("Seleccione un estudiante primero");
            return;
        }

        String periodo = comboPeriodo.getValue();
        if (periodo == null) {
            mostrarAviso("Seleccione un período primero");
            return;
        }

        cargarHorario(estudiante.getId(), periodo);
    }

    /** Horario cuadriculado: delega en HorarioClaseDAO (SP HorarioClase) y pinta una tabla dinámica. */
    private void cargarHorario(String idEstudiante, String periodo) {
        try {
            TablaResultado resultado = horarioClaseDAO.obtenerHorarioCuadriculado(idEstudiante, periodo);

            TableView<ObservableList<String>> tabla = new TableView<>();

            List<String> columnas = resultado.getColumnas();
            for (int i = 0; i < columnas.size(); i++) {
                final int posicion = i;
                TableColumn<ObservableList<String>, String> columna = new TableColumn<>(columnas.get(i));
                columna.setCellValueFactory(data ->
                        new SimpleStringProperty(data.getValue().get(posicion)));
                tabla.getColumns().add(columna);
            }

            for (String[] fila : resultado.getFilas()) {
                ObservableList<String> filaObservable = FXCollections.observableArrayList();
                for (String dato : fila) {
                    filaObservable.add(dato == null ? "" : dato);
                }
                tabla.getItems().add(filaObservable);
            }

            if (resultado.getFilas().isEmpty()) {
                mostrarAviso("El estudiante no tiene grupos inscritos con horario en ese período.");
            }

            Stage ventana = new Stage();
            VBox root = new VBox(tabla);
            Scene scene = new Scene(root, 900, 600);
            ventana.setTitle("Horario del estudiante");
            ventana.setScene(scene);
            ventana.show();

        } catch (SQLException e) {
            mostrarError("No se pudo generar el horario cuadriculado", e);
        }
    }

    private void cargarPeriodos() {

        ObservableList<String> periodos = FXCollections.observableArrayList();
        String sql = "SELECT codigo FROM PeriodoAcademico ORDER BY codigo DESC";

        try (Connection cn = ConexionBDD.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                periodos.add(rs.getString("codigo").trim());
            }

            comboPeriodo.setItems(periodos);

        } catch (SQLException e) {
            mostrarError("No se pudo cargar la lista de períodos", e);
        }
    }


    @FXML
    private void onVerInforme() {
        Estudiante estudiante = tablaEstudiantes.getSelectionModel().getSelectedItem();
        if (estudiante == null) {
            mostrarAviso("Seleccione un estudiante primero");
            return;
        }
        String periodo = comboPeriodo.getValue();
        if (periodo == null) {
            mostrarAviso("Seleccione un período primero");
            return;
        }
        mostrarInforme(estudiante, periodo);
    }

    private void mostrarInforme(Estudiante estudiante, String periodo) {
        try {
            String nombreCarrera = "";
            if (estudiante.getIdCarrera() != null) {
                Carrera carrera = carreraDAO.buscarPorId(estudiante.getIdCarrera());
                nombreCarrera = carrera != null ? carrera.getNombreCarrera() : estudiante.getIdCarrera();
            }

            List<GrupoInscrito> inscritos =
                    grupoInscritoDAO.listarPorEstudiantePeriodo(estudiante.getId(), periodo);

            ObservableList<InformeFila> filas = FXCollections.observableArrayList();
            for (GrupoInscrito gi : inscritos) {
                Asignatura asignatura = asignaturaDAO.buscarPorId(gi.getCodigoAsignatura());
                Grupo grupo = grupoDAO.buscarPorId(gi.getCodigoPeriodo(), gi.getCodigoAsignatura(), gi.getNumeroGrupo());
                filas.add(new InformeFila(
                        gi.getCodigoAsignatura(),
                        asignatura != null ? asignatura.getNombre() : "",
                        gi.getNumeroGrupo(),
                        grupo != null && grupo.getHorario() != null ? grupo.getHorario() : "(sin horario)"
                ));
            }

            Label lblPeriodo = new Label("Período Académico: " + periodo);
            Label lblEstudiante = new Label("Estudiante: " + estudiante.getId() + " - "
                    + formatearNombreCompleto(estudiante.getNombre(), estudiante.getApellido()));
            Label lblCarrera = new Label("Carrera: " + nombreCarrera);
            lblPeriodo.getStyleClass().add("panel-titulo");

            TableView<InformeFila> tabla = new TableView<>();

            TableColumn<InformeFila, String> colCodAsig = new TableColumn<>("Código Asignatura");
            colCodAsig.setCellValueFactory(new PropertyValueFactory<>("codigoAsignatura"));
            colCodAsig.setPrefWidth(130);

            TableColumn<InformeFila, String> colNombreAsig = new TableColumn<>("Nombre Asignatura");
            colNombreAsig.setCellValueFactory(new PropertyValueFactory<>("nombreAsignatura"));
            colNombreAsig.setPrefWidth(200);

            TableColumn<InformeFila, String> colNumGrupo = new TableColumn<>("Número de Grupo");
            colNumGrupo.setCellValueFactory(new PropertyValueFactory<>("numeroGrupo"));
            colNumGrupo.setPrefWidth(130);

            TableColumn<InformeFila, String> colHorario = new TableColumn<>("Horario");
            colHorario.setCellValueFactory(new PropertyValueFactory<>("horario"));
            colHorario.setPrefWidth(260);

            tabla.getColumns().addAll(colCodAsig, colNombreAsig, colNumGrupo, colHorario);
            tabla.setItems(filas);
            tabla.setPrefHeight(320);

            VBox root = new VBox(12, lblPeriodo, lblEstudiante, lblCarrera, tabla);
            root.setPadding(new Insets(20));

            Stage ventana = new Stage();
            ventana.setTitle("Informe de Inscripción");
            ventana.setScene(new Scene(root, 780, 480));
            ventana.show();

        } catch (SQLException e) {
            mostrarError("No se pudo generar el informe de inscripción", e);
        }
    }

    /** Formatea "Juan Antonio" + "Luna Perez" -> "Juan A. Luna P." */
    private String formatearNombreCompleto(String nombre, String apellido) {
        String primerNombre = "";
        String inicialSegundoNombre = "";
        if (nombre != null && !nombre.trim().isEmpty()) {
            String[] partes = nombre.trim().split("\\s+");
            primerNombre = partes[0];
            if (partes.length > 1) {
                inicialSegundoNombre = partes[1].substring(0, 1).toUpperCase() + ".";
            }
        }
        String primerApellido = "";
        String inicialSegundoApellido = "";
        if (apellido != null && !apellido.trim().isEmpty()) {
            String[] partes = apellido.trim().split("\\s+");
            primerApellido = partes[0];
            if (partes.length > 1) {
                inicialSegundoApellido = partes[1].substring(0, 1).toUpperCase() + ".";
            }
        }
        StringBuilder sb = new StringBuilder(primerNombre);
        if (!inicialSegundoNombre.isEmpty()) sb.append(" ").append(inicialSegundoNombre);
        sb.append(" ").append(primerApellido);
        if (!inicialSegundoApellido.isEmpty()) sb.append(" ").append(inicialSegundoApellido);
        return sb.toString();
    }

    /** Fila para la tabla del informe de inscripción. */
    public static class InformeFila {
        private final String codigoAsignatura;
        private final String nombreAsignatura;
        private final String numeroGrupo;
        private final String horario;

        public InformeFila(String codigoAsignatura, String nombreAsignatura, String numeroGrupo, String horario) {
            this.codigoAsignatura = codigoAsignatura;
            this.nombreAsignatura = nombreAsignatura;
            this.numeroGrupo = numeroGrupo;
            this.horario = horario;
        }

        public String getCodigoAsignatura() { return codigoAsignatura; }
        public String getNombreAsignatura() { return nombreAsignatura; }
        public String getNumeroGrupo() { return numeroGrupo; }
        public String getHorario() { return horario; }
    }
}