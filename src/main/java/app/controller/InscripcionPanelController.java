package app.controller;

import bdd.AsignaturaDAO;
import bdd.EstudianteDAO;
import bdd.GrupoDAO;
import bdd.GrupoInscritoDAO;
import bdd.HorarioGrupoDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import modelo.Asignatura;
import modelo.Estudiante;
import modelo.Grupo;
import modelo.GrupoInscrito;
import modelo.HorarioGrupo;

import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class InscripcionPanelController {

    @FXML private ComboBox<String> cbPeriodo;
    @FXML private ComboBox<Estudiante> cbEstudiante;

    @FXML private TableView<GrupoFila> tablaDisponibles;
    @FXML private TableColumn<GrupoFila, String> colDispAsignatura;
    @FXML private TableColumn<GrupoFila, String> colDispNombre;
    @FXML private TableColumn<GrupoFila, String> colDispGrupo;
    @FXML private TableColumn<GrupoFila, String> colDispCupo;

    @FXML private TableView<GrupoFila> tablaInscritos;
    @FXML private TableColumn<GrupoFila, String> colInsAsignatura;
    @FXML private TableColumn<GrupoFila, String> colInsNombre;
    @FXML private TableColumn<GrupoFila, String> colInsGrupo;

    @FXML private TableView<HorarioGrupo> tablaHorario;
    @FXML private TableColumn<HorarioGrupo, String> colHorDia;
    @FXML private TableColumn<HorarioGrupo, String> colHorInicio;
    @FXML private TableColumn<HorarioGrupo, String> colHorFin;

    private final GrupoDAO grupoDAO = new GrupoDAO();
    private final GrupoInscritoDAO grupoInscritoDAO = new GrupoInscritoDAO();
    private final EstudianteDAO estudianteDAO = new EstudianteDAO();
    private final AsignaturaDAO asignaturaDAO = new AsignaturaDAO();
    private final HorarioGrupoDAO horarioGrupoDAO = new HorarioGrupoDAO();

    private final ObservableList<GrupoFila> datosDisponibles = FXCollections.observableArrayList();
    private final ObservableList<GrupoFila> datosInscritos = FXCollections.observableArrayList();
    private final ObservableList<HorarioGrupo> datosHorario = FXCollections.observableArrayList();

    /** Cache del nombre de asignatura por código, para no ir a la BD por cada fila. */
    private final java.util.Map<String, String> nombresAsignatura = new java.util.HashMap<>();

    @FXML
    private void initialize() {
        colDispAsignatura.setCellValueFactory(new PropertyValueFactory<>("codigoAsignatura"));
        colDispNombre.setCellValueFactory(new PropertyValueFactory<>("nombreAsignatura"));
        colDispGrupo.setCellValueFactory(new PropertyValueFactory<>("numeroGrupo"));
        colDispCupo.setCellValueFactory(new PropertyValueFactory<>("cupoTexto"));

        colInsAsignatura.setCellValueFactory(new PropertyValueFactory<>("codigoAsignatura"));
        colInsNombre.setCellValueFactory(new PropertyValueFactory<>("nombreAsignatura"));
        colInsGrupo.setCellValueFactory(new PropertyValueFactory<>("numeroGrupo"));

        colHorDia.setCellValueFactory(cd -> new SimpleStringProperty(String.valueOf(cd.getValue().getDia())));
        colHorInicio.setCellValueFactory(cd -> new SimpleStringProperty(String.valueOf(cd.getValue().getHoraInicio())));
        colHorFin.setCellValueFactory(cd ->
                new SimpleStringProperty(cd.getValue().getHoraFin() != null ? cd.getValue().getHoraFin().toString() : ""));

        tablaDisponibles.setItems(datosDisponibles);
        tablaInscritos.setItems(datosInscritos);
        tablaHorario.setItems(datosHorario);

        cbEstudiante.setConverter(new StringConverter<Estudiante>() {
            @Override public String toString(Estudiante e) {
                return e == null ? "" : e.getId() + " - " + e.getNombre() + " " + e.getApellido();
            }
            @Override public Estudiante fromString(String s) { return null; }
        });

        cbPeriodo.valueProperty().addListener((obs, viejo, nuevo) -> cargarListas());
        cbEstudiante.valueProperty().addListener((obs, viejo, nuevo) -> cargarListas());

        tablaDisponibles.getSelectionModel().selectedItemProperty()
                .addListener((obs, viejo, nuevo) -> cargarHorario(nuevo == null ? null : nuevo.getGrupo()));
        tablaInscritos.getSelectionModel().selectedItemProperty()
                .addListener((obs, viejo, nuevo) -> cargarHorario(nuevo == null ? null : nuevo.getGrupo()));

        cargarPeriodos();
        cargarEstudiantes();
    }

    private void cargarPeriodos() {
        try {
            List<Grupo> todos = grupoDAO.listarTodos();
            Set<String> periodos = new LinkedHashSet<>();
            for (Grupo g : todos) periodos.add(g.getCodigoPeriodo());
            cbPeriodo.setItems(FXCollections.observableArrayList(periodos));
        } catch (SQLException e) {
            error("No se pudo cargar la lista de periodos", e);
        }
    }

    private void cargarEstudiantes() {
        try {
            cbEstudiante.setItems(FXCollections.observableArrayList(estudianteDAO.listarTodos()));
        } catch (SQLException e) {
            error("No se pudo cargar la lista de estudiantes", e);
        }
    }

    private String nombreAsignatura(String codigo) {
        return nombresAsignatura.computeIfAbsent(codigo, c -> {
            try {
                Asignatura a = asignaturaDAO.buscarPorId(c);
                return a != null ? a.getNombre() : "";
            } catch (SQLException e) {
                return "";
            }
        });
    }

    private void cargarListas() {
        datosDisponibles.clear();
        datosInscritos.clear();
        datosHorario.clear();

        String periodo = cbPeriodo.getValue();
        Estudiante estudiante = cbEstudiante.getValue();
        if (periodo == null || estudiante == null) return;

        try {
            List<Grupo> gruposPeriodo = grupoDAO.listarPorPeriodo(periodo);
            List<GrupoInscrito> inscripciones =
                    grupoInscritoDAO.listarPorEstudiantePeriodo(estudiante.getId(), periodo);

            Set<String> inscritoKeys = new LinkedHashSet<>();
            for (GrupoInscrito gi : inscripciones) {
                inscritoKeys.add(clave(gi.getCodigoAsignatura(), gi.getNumeroGrupo()));
            }

            for (Grupo g : gruposPeriodo) {
                String key = clave(g.getCodigoAsignatura(), g.getNumeroGrupo());
                String nombreAsig = nombreAsignatura(g.getCodigoAsignatura());

                if (inscritoKeys.contains(key)) {
                    datosInscritos.add(new GrupoFila(g, nombreAsig, ""));
                } else {
                    int inscritos = grupoInscritoDAO.contarInscritos(
                            g.getCodigoPeriodo(), g.getCodigoAsignatura(), g.getNumeroGrupo());
                    boolean hayCupo = g.getCupoGrupo() == null || inscritos < g.getCupoGrupo();
                    if (hayCupo) {
                        String cupoTexto = g.getCupoGrupo() == null ? "-" : String.valueOf(g.getCupoGrupo() - inscritos);
                        datosDisponibles.add(new GrupoFila(g, nombreAsig, cupoTexto));
                    }
                }
            }
        } catch (SQLException e) {
            error("No se pudo cargar la información de grupos", e);
        }
    }

    private String clave(String codigoAsignatura, String numeroGrupo) {
        return codigoAsignatura.trim() + "|" + numeroGrupo.trim();
    }

    private void cargarHorario(Grupo g) {
        datosHorario.clear();
        if (g == null) return;
        try {
            datosHorario.setAll(horarioGrupoDAO.listarPorGrupo(
                    g.getCodigoPeriodo(), g.getCodigoAsignatura(), g.getNumeroGrupo()));
        } catch (SQLException e) {
            error("No se pudo cargar el horario del grupo", e);
        }
    }

    @FXML
    private void onInscribir() {
        Estudiante estudiante = cbEstudiante.getValue();
        String periodo = cbPeriodo.getValue();
        GrupoFila seleccionado = tablaDisponibles.getSelectionModel().getSelectedItem();

        if (estudiante == null || periodo == null) {
            aviso("Seleccione periodo y estudiante primero.");
            return;
        }
        if (seleccionado == null) {
            aviso("Seleccione un grupo disponible para inscribir.");
            return;
        }

        Grupo g = seleccionado.getGrupo();
        GrupoInscrito gi = new GrupoInscrito(periodo, estudiante.getId(),
                g.getCodigoAsignatura(), g.getNumeroGrupo());

        try {
            grupoInscritoDAO.insertar(gi);
            cargarListas();
        } catch (SQLException e) {
            error("No se pudo inscribir el grupo (revise si hay cruce de horario o cupo)", e);
        }
    }

    @FXML
    private void onEliminarInscripcion() {
        Estudiante estudiante = cbEstudiante.getValue();
        String periodo = cbPeriodo.getValue();
        GrupoFila seleccionado = tablaInscritos.getSelectionModel().getSelectedItem();

        if (estudiante == null || periodo == null || seleccionado == null) {
            aviso("Seleccione un grupo inscrito para eliminar.");
            return;
        }

        Grupo g = seleccionado.getGrupo();
        try {
            grupoInscritoDAO.eliminar(periodo, estudiante.getId(), g.getCodigoAsignatura(), g.getNumeroGrupo());
            cargarListas();
        } catch (SQLException e) {
            error("No se pudo eliminar la inscripción", e);
        }
    }

    private void aviso(String m) {
        new Alert(Alert.AlertType.INFORMATION, m).showAndWait();
    }

    private void error(String m, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(m);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }

    /** Fila precalculada para las tablas: evita consultar la BD en cada render de celda. */
    public static class GrupoFila {
        private final Grupo grupo;
        private final String nombreAsignatura;
        private final String cupoTexto;

        public GrupoFila(Grupo grupo, String nombreAsignatura, String cupoTexto) {
            this.grupo = grupo;
            this.nombreAsignatura = nombreAsignatura;
            this.cupoTexto = cupoTexto;
        }

        public Grupo getGrupo() { return grupo; }
        public String getCodigoAsignatura() { return grupo.getCodigoAsignatura(); }
        public String getNombreAsignatura() { return nombreAsignatura; }
        public String getNumeroGrupo() { return grupo.getNumeroGrupo(); }
        public String getCupoTexto() { return cupoTexto; }
    }
}