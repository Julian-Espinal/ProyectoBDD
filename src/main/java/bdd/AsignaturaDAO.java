package bdd;

import modelo.Asignatura;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AsignaturaDAO {

    public boolean insertar(Asignatura a) throws SQLException {
        String sql = "INSERT INTO Asignatura (codigo, nombre, creditos, horasTeoricas, horasPracticas) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = ConexionBDD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, a.getCodigo());
            ps.setString(2, a.getNombre());
            ps.setObject(3, a.getCreditos());
            ps.setObject(4, a.getHorasTeoricas());
            ps.setObject(5, a.getHorasPracticas());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean actualizar(Asignatura a) throws SQLException {
        String sql = "UPDATE Asignatura SET nombre = ?, creditos = ?, horasTeoricas = ?, horasPracticas = ? " +
                "WHERE RTRIM(codigo) = ?";
        try (Connection con = ConexionBDD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, a.getNombre());
            ps.setObject(2, a.getCreditos());
            ps.setObject(3, a.getHorasTeoricas());
            ps.setObject(4, a.getHorasPracticas());
            ps.setString(5, a.getCodigo().trim());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(String codigo) throws SQLException {
        String sql = "DELETE FROM Asignatura WHERE RTRIM(codigo) = ?";
        try (Connection con = ConexionBDD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigo.trim());
            return ps.executeUpdate() > 0;
        }
    }

    public Asignatura buscarPorId(String codigo) throws SQLException {
        String sql = "SELECT codigo, nombre, creditos, horasTeoricas, horasPracticas FROM Asignatura " +
                "WHERE RTRIM(codigo) = ?";
        try (Connection con = ConexionBDD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigo.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    public List<Asignatura> listarTodos() throws SQLException {
        List<Asignatura> lista = new ArrayList<>();
        String sql = "SELECT codigo, nombre, creditos, horasTeoricas, horasPracticas FROM Asignatura ORDER BY codigo";
        try (Connection con = ConexionBDD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    private Asignatura mapear(ResultSet rs) throws SQLException {
        return new Asignatura(
                rs.getString("codigo").trim(),
                rs.getString("nombre"),
                (Integer) rs.getObject("creditos"),
                (Integer) rs.getObject("horasTeoricas"),
                (Integer) rs.getObject("horasPracticas")
        );
    }
}