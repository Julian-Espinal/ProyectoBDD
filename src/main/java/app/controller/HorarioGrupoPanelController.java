package app.controller;

import bdd.AsignaturaDAO;
import bdd.DiaSemanaDAO;
import bdd.GrupoDAO;
import bdd.HorarioGrupoDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import modelo.Asignatura;
import modelo.DiaSemana;
import modelo.Grupo;
import modelo.HorarioGrupo;

import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class HorarioGrupoPanelController {

    @FXML private ComboBox<String> cbPeriodo;
    @FXML private ComboBox<Grupo> cbGrupo;
    @FXML private Label lblInfoGrupo;
    @FXML private TableView<HorarioGrupo> tablaHorarios;
    @FXML private TableColumn<HorarioGrupo, String> colDia;
    @FXML private TableColumn<HorarioGrupo, String> colHoraInicio;
    @FXML private TableColumn<HorarioGrupo, String> colHoraFin;
    @FXML private ComboBox<DiaSemana> cbDia;
    @FXML private TextField txtHoraInicio;
    @FXML private TextField txtHoraFin;

    private final GrupoDAO grupoDAO = new GrupoDAO();
    private final HorarioGrupoDAO horarioGrupoDAO = new HorarioGrupoDAO();
    private final DiaSemanaDAO diaSemanaDAO = new DiaSemanaDAO();
    private final AsignaturaDAO asignaturaDAO = new AsignaturaDAO();

    private final ObservableList<HorarioGrupo> datosHorarios = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        colDia.setCellValueFactory(cd ->
                new SimpleStringProperty(nombreDia(cd.getValue().getDia())));
        colHoraInicio.setCellValueFactory(cd ->
                new SimpleStringProperty(String.valueOf(cd.getValue().getHoraInicio())));
        colHoraFin.setCellValueFactory(cd ->
                new SimpleStringProperty(cd.getValue().getHoraFin() != null ? cd.getValue().getHoraFin().toString() : ""));
        tablaHorarios.setItems(datosHorarios);

        cbGrupo.setConverter(new StringConverter<Grupo>() {
            @Override public String toString(Grupo g) {
                return g == null ? "" : g.getCodigoAsignatura() + " - Grupo " + g.getNumeroGrupo();
            }
            @Override public Grupo fromString(String s) { return null; }
        });

        cbPeriodo.valueProperty().addListener((obs, viejo, nuevo) -> cargarGrupos(nuevo));
        cbGrupo.valueProperty().addListener((obs, viejo, nuevo) -> {
            actualizarInfoGrupo(nuevo);
            cargarHorarios(nuevo);
        });

        cargarDias();
        cargarPeriodos();
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

    private void cargarDias() {
        try {
            cbDia.setItems(FXCollections.observableArrayList(diaSemanaDAO.listarTodos()));
            cbDia.setConverter(new StringConverter<DiaSemana>() {
                @Override public String toString(DiaSemana d) { return d == null ? "" : d.getDescripcion(); }
                @Override public DiaSemana fromString(String s) { return null; }
            });
        } catch (SQLException e) {
            error("No se pudo cargar el catálogo de días", e);
        }
    }

    private String nombreDia(int dia) {
        if (cbDia.getItems() != null) {
            for (DiaSemana d : cbDia.getItems()) {
                if (d.getDia() != null && d.getDia() == dia) return d.getDescripcion();
            }
        }
        return String.valueOf(dia);
    }

    /** Dos bloques cruzan si son el mismo día y sus rangos de hora se solapan. */
    private boolean seCruzan(int diaA, LocalTime iniA, LocalTime finA,
                             int diaB, LocalTime iniB, LocalTime finB) {
        if (diaA != diaB) return false;

        LocalTime finARef = finA != null ? finA : iniA;
        LocalTime finBRef = finB != null ? finB : iniB;

        // Solapan si NO ocurre que uno termina antes de que el otro empiece.
        return iniA.isBefore(finBRef) && iniB.isBefore(finARef);
    }

    private void cargarGrupos(String periodo) {
        datosHorarios.clear();
        lblInfoGrupo.setText("Selecciona un grupo para ver su información");
        if (periodo == null) {
            cbGrupo.setItems(FXCollections.observableArrayList());
            return;
        }
        try {
            cbGrupo.setItems(FXCollections.observableArrayList(grupoDAO.listarPorPeriodo(periodo)));
        } catch (SQLException e) {
            error("No se pudo cargar la lista de grupos", e);
        }
    }

    private void actualizarInfoGrupo(Grupo g) {
        if (g == null) {
            lblInfoGrupo.setText("Selecciona un grupo para ver su información");
            return;
        }
        lblInfoGrupo.setText("Asignatura: " + g.getCodigoAsignatura()
                + "  |  Cupo: " + g.getCupoGrupo()
                + "  |  Horario condensado: " + (g.getHorario() == null ? "(sin horario)" : g.getHorario()));
    }

    private void cargarHorarios(Grupo g) {
        datosHorarios.clear();
        if (g == null) return;
        try {
            datosHorarios.setAll(horarioGrupoDAO.listarPorGrupo(
                    g.getCodigoPeriodo(), g.getCodigoAsignatura(), g.getNumeroGrupo()));
        } catch (SQLException e) {
            error("No se pudo cargar el horario del grupo", e);
        }
    }

    /** Vuelve a leer el grupo desde la BD (el trigger ya recalculó Grupo.horario) y refresca la vista. */
    private void refrescarGrupoActual() {
        Grupo actual = cbGrupo.getValue();
        if (actual == null) return;
        try {
            Grupo actualizado = grupoDAO.buscarPorId(
                    actual.getCodigoPeriodo(), actual.getCodigoAsignatura(), actual.getNumeroGrupo());
            int idx = cbGrupo.getItems().indexOf(actual);
            if (idx >= 0 && actualizado != null) {
                cbGrupo.getItems().set(idx, actualizado);
                cbGrupo.getSelectionModel().select(idx);
            }
            actualizarInfoGrupo(actualizado);
            cargarHorarios(actualizado);
        } catch (SQLException e) {
            error("No se pudo refrescar el grupo", e);
        }
    }

    @FXML
    private void onAgregarHorario() {
        Grupo g = cbGrupo.getValue();
        if (g == null) { aviso("Selecciona un grupo primero."); return; }
        DiaSemana dia = cbDia.getValue();
        if (dia == null) { aviso("Selecciona un día."); return; }

        LocalTime inicio;
        try {
            inicio = LocalTime.parse(txtHoraInicio.getText().trim());
        } catch (DateTimeParseException ex) {
            aviso("Hora de inicio inválida. Usa el formato HH:mm, ej. 08:00");
            return;
        }
        LocalTime fin = null;
        if (!txtHoraFin.getText().trim().isEmpty()) {
            try {
                fin = LocalTime.parse(txtHoraFin.getText().trim());
            } catch (DateTimeParseException ex) {
                aviso("Hora de fin inválida. Usa el formato HH:mm, ej. 10:00");
                return;
            }
        }
        if (fin != null && !fin.isAfter(inicio)) {
            aviso("La hora de fin debe ser posterior a la hora de inicio.");
            return;
        }

        // --- Validación de cruce con los horarios que ya tiene este grupo ---
        for (HorarioGrupo existente : datosHorarios) {
            if (seCruzan(dia.getDia(), inicio, fin,
                    existente.getDia(), existente.getHoraInicio(), existente.getHoraFin())) {
                aviso("Ese horario se cruza con uno ya existente en este grupo: "
                        + nombreDia(existente.getDia()) + " "
                        + existente.getHoraInicio() + " - "
                        + (existente.getHoraFin() != null ? existente.getHoraFin() : "(sin fin)"));
                return;
            }
        }

        HorarioGrupo h = new HorarioGrupo(g.getCodigoPeriodo(), g.getCodigoAsignatura(), g.getNumeroGrupo(),
                dia.getDia(), inicio, fin);
        try {
            horarioGrupoDAO.insertar(h);
            refrescarGrupoActual();
            txtHoraInicio.clear();
            txtHoraFin.clear();
        } catch (SQLException e) {
            error("No se pudo agregar el horario (revisa si se cruza con otro)", e);
        }
    }

    @FXML
    private void onEliminarHorario() {
        Grupo g = cbGrupo.getValue();
        HorarioGrupo seleccionado = tablaHorarios.getSelectionModel().getSelectedItem();
        if (g == null || seleccionado == null) {
            aviso("Selecciona un horario en la tabla para eliminar.");
            return;
        }
        try {
            horarioGrupoDAO.eliminar(g.getCodigoPeriodo(), g.getCodigoAsignatura(), g.getNumeroGrupo(),
                    seleccionado.getDia(), seleccionado.getHoraInicio());
            refrescarGrupoActual();
        } catch (SQLException e) {
            error("No se pudo eliminar el horario", e);
        }
    }

    @FXML
    private void onNuevoGrupo() {
        Dialog<Grupo> dialog = new Dialog<>();
        dialog.setTitle("Nuevo grupo");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField txtPeriodo = new TextField(cbPeriodo.getValue() == null ? "" : cbPeriodo.getValue());
        ComboBox<Asignatura> cbAsignatura = new ComboBox<>();
        cbAsignatura.setConverter(new StringConverter<Asignatura>() {
            @Override public String toString(Asignatura a) { return a == null ? "" : a.getCodigo() + " - " + a.getNombre(); }
            @Override public Asignatura fromString(String s) { return null; }
        });
        try {
            cbAsignatura.setItems(FXCollections.observableArrayList(asignaturaDAO.listarTodos()));
        } catch (SQLException e) {
            error("No se pudo cargar la lista de asignaturas", e);
        }
        TextField txtNumeroGrupo = new TextField();
        TextField txtCupo = new TextField();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.add(new Label("Periodo:"), 0, 0);       grid.add(txtPeriodo, 1, 0);
        grid.add(new Label("Asignatura:"), 0, 1);    grid.add(cbAsignatura, 1, 1);
        grid.add(new Label("Número grupo:"), 0, 2);  grid.add(txtNumeroGrupo, 1, 2);
        grid.add(new Label("Cupo:"), 0, 3);          grid.add(txtCupo, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK && cbAsignatura.getValue() != null) {
                Integer cupo = null;
                try { cupo = Integer.parseInt(txtCupo.getText().trim()); } catch (NumberFormatException ignored) { }
                return new Grupo(txtPeriodo.getText().trim(), cbAsignatura.getValue().getCodigo(),
                        txtNumeroGrupo.getText().trim(), cupo, null);
            }
            return null;
        });

        Optional<Grupo> resultado = dialog.showAndWait();
        resultado.ifPresent(g -> {
            try {
                grupoDAO.insertar(g);
                cargarPeriodos();
                cbPeriodo.setValue(g.getCodigoPeriodo());
            } catch (SQLException e) {
                error("No se pudo crear el grupo", e);
            }
        });
    }

    private void aviso(String m) {
        new Alert(Alert.AlertType.INFORMATION, m).showAndWait();
    }

    private void error(String m, Exception e) {

        System.out.println("ENTRO AL METODO ERROR");
        e.printStackTrace();

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(m);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }
}