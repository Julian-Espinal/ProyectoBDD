package bdd;

import modelo.GrupoInscrito;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** DAO para la tabla GruposInscritos. Llave compuesta: (codigoPeriodo, idEstudiante, codigoAsignatura, numeroGrupo) */
public class GrupoInscritoDAO {

    public boolean insertar(GrupoInscrito gi) throws SQLException {
        String sql = "INSERT INTO GruposInscritos (codigoPeriodo, idEstudiante, codigoAsignatura, numeroGrupo) " +
                "VALUES (?, ?, ?, ?)";
        try (Connection con = ConexionBDD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, gi.getCodigoPeriodo());
            ps.setString(2, gi.getIdEstudiante());
            ps.setString(3, gi.getCodigoAsignatura());
            ps.setString(4, gi.getNumeroGrupo());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(String codigoPeriodo, String idEstudiante, String codigoAsignatura, String numeroGrupo)
            throws SQLException {
        String sql = "DELETE FROM GruposInscritos WHERE RTRIM(codigoPeriodo) = ? AND RTRIM(idEstudiante) = ? " +
                "AND RTRIM(codigoAsignatura) = ? AND RTRIM(numeroGrupo) = ?";
        try (Connection con = ConexionBDD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigoPeriodo.trim());
            ps.setString(2, idEstudiante.trim());
            ps.setString(3, codigoAsignatura.trim());
            ps.setString(4, numeroGrupo.trim());
            return ps.executeUpdate() > 0;
        }
    }

    /** Lista las asignaturas/grupos en los que está inscrito un estudiante en un período. */
    public List<GrupoInscrito> listarPorEstudiantePeriodo(String idEstudiante, String codigoPeriodo)
            throws SQLException {
        List<GrupoInscrito> lista = new ArrayList<>();
        String sql = "SELECT codigoPeriodo, idEstudiante, codigoAsignatura, numeroGrupo FROM GruposInscritos " +
                "WHERE RTRIM(idEstudiante) = ? AND RTRIM(codigoPeriodo) = ? ORDER BY codigoAsignatura";
        try (Connection con = ConexionBDD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, idEstudiante.trim());
            ps.setString(2, codigoPeriodo.trim());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    /** Cuenta cuántos estudiantes están inscritos actualmente en un grupo (para calcular cupo disponible). */
    public int contarInscritos(String codigoPeriodo, String codigoAsignatura, String numeroGrupo)
            throws SQLException {
        String sql = "SELECT COUNT(*) FROM GruposInscritos WHERE RTRIM(codigoPeriodo) = ? " +
                "AND RTRIM(codigoAsignatura) = ? AND RTRIM(numeroGrupo) = ?";
        try (Connection con = ConexionBDD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigoPeriodo.trim());
            ps.setString(2, codigoAsignatura.trim());
            ps.setString(3, numeroGrupo.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public List<GrupoInscrito> listarTodos() throws SQLException {
        List<GrupoInscrito> lista = new ArrayList<>();
        String sql = "SELECT codigoPeriodo, idEstudiante, codigoAsignatura, numeroGrupo FROM GruposInscritos";
        try (Connection con = ConexionBDD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    private GrupoInscrito mapear(ResultSet rs) throws SQLException {
        return new GrupoInscrito(
                rs.getString("codigoPeriodo").trim(),
                rs.getString("idEstudiante").trim(),
                rs.getString("codigoAsignatura").trim(),
                rs.getString("numeroGrupo").trim()
        );
    }
}