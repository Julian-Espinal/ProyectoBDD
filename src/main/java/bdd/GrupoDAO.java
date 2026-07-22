package bdd;

import modelo.Grupo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GrupoDAO {

    public boolean insertar(Grupo g) throws SQLException {
        String sql = "INSERT INTO Grupo (codigoPeriodo, codigoAsignatura, numeroGrupo, cupoGrupo, horario) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = ConexionBDD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, g.getCodigoPeriodo());
            ps.setString(2, g.getCodigoAsignatura());
            ps.setString(3, g.getNumeroGrupo());
            ps.setObject(4, g.getCupoGrupo());
            ps.setString(5, g.getHorario());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean actualizar(Grupo g) throws SQLException {
        String sql = "UPDATE Grupo SET cupoGrupo = ?, horario = ? " +
                "WHERE RTRIM(codigoPeriodo) = ? AND RTRIM(codigoAsignatura) = ? AND RTRIM(numeroGrupo) = ?";
        try (Connection con = ConexionBDD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setObject(1, g.getCupoGrupo());
            ps.setString(2, g.getHorario());
            ps.setString(3, g.getCodigoPeriodo().trim());
            ps.setString(4, g.getCodigoAsignatura().trim());
            ps.setString(5, g.getNumeroGrupo().trim());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(String codigoPeriodo, String codigoAsignatura, String numeroGrupo) throws SQLException {
        String sql = "DELETE FROM Grupo " +
                "WHERE RTRIM(codigoPeriodo) = ? AND RTRIM(codigoAsignatura) = ? AND RTRIM(numeroGrupo) = ?";
        try (Connection con = ConexionBDD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigoPeriodo.trim());
            ps.setString(2, codigoAsignatura.trim());
            ps.setString(3, numeroGrupo.trim());
            return ps.executeUpdate() > 0;
        }
    }

    public Grupo buscarPorId(String codigoPeriodo, String codigoAsignatura, String numeroGrupo) throws SQLException {
        String sql = "SELECT codigoPeriodo, codigoAsignatura, numeroGrupo, cupoGrupo, horario FROM Grupo " +
                "WHERE RTRIM(codigoPeriodo) = ? AND RTRIM(codigoAsignatura) = ? AND RTRIM(numeroGrupo) = ?";
        try (Connection con = ConexionBDD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigoPeriodo.trim());
            ps.setString(2, codigoAsignatura.trim());
            ps.setString(3, numeroGrupo.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    /** Lista los grupos ofertados en un período académico específico. */
    public List<Grupo> listarPorPeriodo(String codigoPeriodo) throws SQLException {
        List<Grupo> lista = new ArrayList<>();
        String sql = "SELECT codigoPeriodo, codigoAsignatura, numeroGrupo, cupoGrupo, horario FROM Grupo " +
                "WHERE RTRIM(codigoPeriodo) = ? ORDER BY codigoAsignatura";
        try (Connection con = ConexionBDD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigoPeriodo.trim());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    public List<Grupo> listarTodos() throws SQLException {
        List<Grupo> lista = new ArrayList<>();
        String sql = "SELECT codigoPeriodo, codigoAsignatura, numeroGrupo, cupoGrupo, horario FROM Grupo " +
                "ORDER BY codigoPeriodo, codigoAsignatura";
        try (Connection con = ConexionBDD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    private Grupo mapear(ResultSet rs) throws SQLException {
        return new Grupo(
                rs.getString("codigoPeriodo").trim(),
                rs.getString("codigoAsignatura").trim(),
                rs.getString("numeroGrupo").trim(),
                (Integer) rs.getObject("cupoGrupo"),
                rs.getString("horario")
        );
    }
}