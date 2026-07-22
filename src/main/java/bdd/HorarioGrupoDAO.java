package bdd;

import modelo.HorarioGrupo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class HorarioGrupoDAO {

    public boolean insertar(HorarioGrupo h) throws SQLException {
        String sql = "INSERT INTO HorarioGrupo (codigoPeriodo, codigoAsignatura, numeroGrupo, dia, horaInicio, horaFin) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionBDD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, h.getCodigoPeriodo());
            ps.setString(2, h.getCodigoAsignatura());
            ps.setString(3, h.getNumeroGrupo());
            ps.setInt(4, h.getDia());
            ps.setTime(5, Time.valueOf(h.getHoraInicio()));
            ps.setTime(6, h.getHoraFin() != null ? Time.valueOf(h.getHoraFin()) : null);
            return ps.executeUpdate() > 0;
        }
    }

    /** Elimina una fila específica de horario (un día puntual de un grupo). */
    public boolean eliminar(String codigoPeriodo, String codigoAsignatura, String numeroGrupo,
                            int dia, LocalTime horaInicio) throws SQLException {
        String sql = "DELETE FROM HorarioGrupo WHERE RTRIM(codigoPeriodo) = ? AND RTRIM(codigoAsignatura) = ? " +
                "AND RTRIM(numeroGrupo) = ? AND dia = ? AND horaInicio = ?";
        try (Connection con = ConexionBDD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigoPeriodo.trim());
            ps.setString(2, codigoAsignatura.trim());
            ps.setString(3, numeroGrupo.trim());
            ps.setInt(4, dia);
            ps.setTime(5, Time.valueOf(horaInicio));
            return ps.executeUpdate() > 0;
        }
    }

    /** Elimina TODOS los horarios de un grupo (útil antes de reasignar desde cero). */
    public boolean eliminarPorGrupo(String codigoPeriodo, String codigoAsignatura, String numeroGrupo)
            throws SQLException {
        String sql = "DELETE FROM HorarioGrupo WHERE RTRIM(codigoPeriodo) = ? AND RTRIM(codigoAsignatura) = ? " +
                "AND RTRIM(numeroGrupo) = ?";
        try (Connection con = ConexionBDD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigoPeriodo.trim());
            ps.setString(2, codigoAsignatura.trim());
            ps.setString(3, numeroGrupo.trim());
            return ps.executeUpdate() > 0;
        }
    }

    /** Lista los horarios de un grupo específico, ordenados por día. */
    public List<HorarioGrupo> listarPorGrupo(String codigoPeriodo, String codigoAsignatura, String numeroGrupo)
            throws SQLException {
        List<HorarioGrupo> lista = new ArrayList<>();
        String sql = "SELECT codigoPeriodo, codigoAsignatura, numeroGrupo, dia, horaInicio, horaFin " +
                "FROM HorarioGrupo WHERE RTRIM(codigoPeriodo) = ? AND RTRIM(codigoAsignatura) = ? " +
                "AND RTRIM(numeroGrupo) = ? ORDER BY dia, horaInicio";
        try (Connection con = ConexionBDD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigoPeriodo.trim());
            ps.setString(2, codigoAsignatura.trim());
            ps.setString(3, numeroGrupo.trim());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    public List<HorarioGrupo> listarTodos() throws SQLException {
        List<HorarioGrupo> lista = new ArrayList<>();
        String sql = "SELECT codigoPeriodo, codigoAsignatura, numeroGrupo, dia, horaInicio, horaFin " +
                "FROM HorarioGrupo ORDER BY codigoPeriodo, codigoAsignatura, numeroGrupo, dia";
        try (Connection con = ConexionBDD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    private HorarioGrupo mapear(ResultSet rs) throws SQLException {
        Time fin = rs.getTime("horaFin");
        return new HorarioGrupo(
                rs.getString("codigoPeriodo").trim(),
                rs.getString("codigoAsignatura").trim(),
                rs.getString("numeroGrupo").trim(),
                rs.getInt("dia"),
                rs.getTime("horaInicio").toLocalTime(),
                fin != null ? fin.toLocalTime() : null
        );
    }
}