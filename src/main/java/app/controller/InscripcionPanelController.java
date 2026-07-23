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

import java.time.LocalTime;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class InscripcionPanelController {

    @FXML private ComboBox<String> cbPeriodo;
    @FXML private TextField txtBuscarEstudiante;
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

    /** Lista completa de estudiantes (sin filtrar), cacheada para no ir a la BD en cada tecla. */
    private final ObservableList<Estudiante> todosEstudiantes = FXCollections.observableArrayList();

    /** Cache del nombre de asignatura por código, para no ir a la BD por cada fila. */
    private final Map<String, String> nombresAsignatura = new HashMap<>();

    // ---- Caches por período: se recargan SOLO cuando cambia el período, ----
    // ---- no cuando cambia el estudiante. Esto elimina el patrón N+1.     ----
    private String periodoCacheado = null;
    private List<Grupo> gruposPeriodoActual = new ArrayList<>();
    private Map<String, Integer> conteoInscritosPeriodo = new HashMap<>();
    private Map<String, List<HorarioGrupo>> horariosPorGrupoPeriodo = new HashMap<>();

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

        // A medida que el usuario escribe, filtramos la lista completa por ID o apellido
        // y actualizamos los items del combo, abriéndolo para mostrar las coincidencias.
        txtBuscarEstudiante.textProperty().addListener((obs, viejo, nuevo) -> filtrarEstudiantes(nuevo));

        // Cambiar de PERIODO recarga las caches (grupos, cupos, horarios) desde la BD.
        cbPeriodo.valueProperty().addListener((obs, viejo, nuevo) -> {
            cargarCachesDelPeriodo(nuevo);
            cargarListas();
        });

        // Cambiar de ESTUDIANTE solo reutiliza las caches ya cargadas: sin queries extra
        // salvo la de sus inscripciones (una sola consulta, indispensable).
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
            todosEstudiantes.setAll(estudianteDAO.listarTodos());
            cbEstudiante.setItems(FXCollections.observableArrayList(todosEstudiantes));
        } catch (SQLException e) {
            error("No se pudo cargar la lista de estudiantes", e);
        }
    }

    /**
     * Filtra la lista completa de estudiantes por ID o apellido (contains, sin distinguir
     * mayúsculas/minúsculas) y actualiza los items del combo. Si el texto está vacío,
     * restaura la lista completa. No toca la selección actual salvo que ya no aparezca
     * en el resultado filtrado.
     */
    private void filtrarEstudiantes(String texto) {
        String filtro = texto == null ? "" : texto.trim().toLowerCase();

        Estudiante seleccionadoActual = cbEstudiante.getValue();

        if (filtro.isEmpty()) {
            cbEstudiante.setItems(FXCollections.observableArrayList(todosEstudiantes));
        } else {
            ObservableList<Estudiante> filtrados = FXCollections.observableArrayList();
            for (Estudiante e : todosEstudiantes) {
                boolean coincideId = e.getId() != null && e.getId().toLowerCase().contains(filtro);
                boolean coincideApellido = e.getApellido() != null && e.getApellido().toLowerCase().contains(filtro);
                if (coincideId || coincideApellido) {
                    filtrados.add(e);
                }
            }
            cbEstudiante.setItems(filtrados);

            if (!filtrados.isEmpty()) {
                cbEstudiante.show();
            } else {
                cbEstudiante.hide();
            }
        }

        // Si el estudiante que estaba seleccionado sigue en la lista filtrada, lo mantenemos.
        if (seleccionadoActual != null && cbEstudiante.getItems().contains(seleccionadoActual)) {
            cbEstudiante.setValue(seleccionadoActual);
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

    /**
     * Carga en memoria, con 3 consultas totales (independiente de cuántos grupos haya),
     * todo lo que se necesita del período: los grupos, cuántos inscritos tiene cada uno,
     * y el horario completo de cada uno. Se llama SOLO cuando cambia el período.
     */
    private void cargarCachesDelPeriodo(String periodo) {
        gruposPeriodoActual = new ArrayList<>();
        conteoInscritosPeriodo = new HashMap<>();
        horariosPorGrupoPeriodo = new HashMap<>();
        periodoCacheado = periodo;

        if (periodo == null) return;

        try {
            gruposPeriodoActual = grupoDAO.listarPorPeriodo(periodo);
            conteoInscritosPeriodo = grupoInscritoDAO.contarInscritosPorPeriodo(periodo);

            List<HorarioGrupo> horariosDelPeriodo = horarioGrupoDAO.listarPorPeriodo(periodo);
            for (HorarioGrupo h : horariosDelPeriodo) {
                String key = clave(h.getCodigoAsignatura(), h.getNumeroGrupo());
                horariosPorGrupoPeriodo.computeIfAbsent(key, k -> new ArrayList<>()).add(h);
            }
        } catch (SQLException e) {
            error("No se pudo cargar la información del período", e);
        }
    }

    /** Reconstruye las tablas de disponibles/inscritos usando las caches del período + las inscripciones del estudiante. */
    private void cargarListas() {
        datosDisponibles.clear();
        datosInscritos.clear();
        datosHorario.clear();

        String periodo = cbPeriodo.getValue();
        Estudiante estudiante = cbEstudiante.getValue();
        if (periodo == null || estudiante == null) return;

        // Seguridad: si por algún motivo la cache no corresponde al período actual, recárgala.
        if (!periodo.equals(periodoCacheado)) {
            cargarCachesDelPeriodo(periodo);
        }

        try {
            List<GrupoInscrito> inscripciones =
                    grupoInscritoDAO.listarPorEstudiantePeriodo(estudiante.getId(), periodo);

            Set<String> inscritoKeys = new LinkedHashSet<>();
            for (GrupoInscrito gi : inscripciones) {
                inscritoKeys.add(clave(gi.getCodigoAsignatura(), gi.getNumeroGrupo()));
            }

            for (Grupo g : gruposPeriodoActual) {
                String key = clave(g.getCodigoAsignatura(), g.getNumeroGrupo());
                String nombreAsig = nombreAsignatura(g.getCodigoAsignatura());

                if (inscritoKeys.contains(key)) {
                    datosInscritos.add(new GrupoFila(g, nombreAsig, ""));
                } else {
                    int inscritos = conteoInscritosPeriodo.getOrDefault(key, 0);
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

    /** Muestra el horario de un grupo usando la cache del período (sin consultar la BD). */
    private void cargarHorario(Grupo g) {
        datosHorario.clear();
        if (g == null) return;
        String key = clave(g.getCodigoAsignatura(), g.getNumeroGrupo());
        datosHorario.setAll(horariosPorGrupoPeriodo.getOrDefault(key, List.of()));
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

        String conflicto = detectarCruceHorario(g);
        if (conflicto != null) {
            aviso("No se puede inscribir: el horario cruza con " + conflicto + ".");
            return;
        }

        GrupoInscrito gi = new GrupoInscrito(periodo, estudiante.getId(),
                g.getCodigoAsignatura(), g.getNumeroGrupo());

        try {
            grupoInscritoDAO.insertar(gi);
            // El cupo disponible cambió: refrescamos la cache del período.
            cargarCachesDelPeriodo(periodo);
            cargarListas();
        } catch (SQLException e) {
            error("No se pudo inscribir el grupo", e);
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
            // El cupo disponible cambió: refrescamos la cache del período.
            cargarCachesDelPeriodo(periodo);
            cargarListas();
        } catch (SQLException e) {
            error("No se pudo eliminar la inscripción", e);
        }
    }

    /**
     * Compara el horario del grupo candidato contra los horarios de todos los grupos
     * en los que el estudiante ya está inscrito (tabla "Inscritos" actualmente en pantalla).
     * Usa la cache en memoria, no consulta la BD. Devuelve la descripción del grupo con
     * el que cruza, o null si no hay cruce.
     */
    private String detectarCruceHorario(Grupo candidato) {
        String keyCandidato = clave(candidato.getCodigoAsignatura(), candidato.getNumeroGrupo());
        List<HorarioGrupo> horarioCandidato = horariosPorGrupoPeriodo.getOrDefault(keyCandidato, List.of());

        if (horarioCandidato.isEmpty()) return null; // sin horario asignado, nada que cruzar

        for (GrupoFila fila : datosInscritos) {
            Grupo gInscrito = fila.getGrupo();
            String keyInscrito = clave(gInscrito.getCodigoAsignatura(), gInscrito.getNumeroGrupo());
            List<HorarioGrupo> horarioInscrito = horariosPorGrupoPeriodo.getOrDefault(keyInscrito, List.of());

            for (HorarioGrupo hCand : horarioCandidato) {
                for (HorarioGrupo hIns : horarioInscrito) {
                    if (seCruzan(hCand, hIns)) {
                        return gInscrito.getCodigoAsignatura() + " - Grupo " + gInscrito.getNumeroGrupo();
                    }
                }
            }
        }
        return null;
    }

    /** Dos bloques cruzan si son el mismo día y sus rangos de hora se solapan. */
    private boolean seCruzan(HorarioGrupo a, HorarioGrupo b) {
        if (a.getDia() != b.getDia()) return false;

        LocalTime iniA = a.getHoraInicio();
        LocalTime finA = a.getHoraFin() != null ? a.getHoraFin() : iniA;
        LocalTime iniB = b.getHoraInicio();
        LocalTime finB = b.getHoraFin() != null ? b.getHoraFin() : iniB;

        // Solapan si NO ocurre que uno termina antes de que el otro empiece.
        return iniA.isBefore(finB) && iniB.isBefore(finA);
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