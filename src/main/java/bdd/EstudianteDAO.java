package bdd;

import modelo.Estudiante;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EstudianteDAO {

    public boolean insertar(Estudiante e) throws SQLException {
        String sql = "INSERT INTO Estudiante (id, nombre, apellido, idCarrera, idCategoriaPago, idNacionalidad, direccion) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionBDD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, e.getId());
            ps.setString(2, e.getNombre());
            ps.setString(3, e.getApellido());
            ps.setString(4, e.getIdCarrera());
            ps.setString(5, e.getIdCategoriaPago());
            ps.setString(6, e.getIdNacionalidad());
            ps.setString(7, e.getDireccion());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean actualizar(Estudiante e) throws SQLException {
        String sql = "UPDATE Estudiante SET nombre = ?, apellido = ?, idCarrera = ?, idCategoriaPago = ?, " +
                "idNacionalidad = ?, direccion = ? WHERE RTRIM(id) = ?";
        try (Connection con = ConexionBDD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, e.getNombre());
            ps.setString(2, e.getApellido());
            ps.setString(3, e.getIdCarrera());
            ps.setString(4, e.getIdCategoriaPago());
            ps.setString(5, e.getIdNacionalidad());
            ps.setString(6, e.getDireccion());
            ps.setString(7, e.getId().trim());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(String id) throws SQLException {
        String sql = "DELETE FROM Estudiante WHERE RTRIM(id) = ?";
        try (Connection con = ConexionBDD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id.trim());
            return ps.executeUpdate() > 0;
        }
    }

    public Estudiante buscarPorId(String id) throws SQLException {
        String sql = "SELECT id, nombre, apellido, idCarrera, idCategoriaPago, idNacionalidad, direccion " +
                "FROM Estudiante WHERE RTRIM(id) = ?";
        try (Connection con = ConexionBDD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }


    public List<Estudiante> listarPorCarrera(String idCarrera) throws SQLException {
        List<Estudiante> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, apellido, idCarrera, idCategoriaPago, idNacionalidad, direccion " +
                "FROM Estudiante WHERE RTRIM(idCarrera) = ? ORDER BY apellido, nombre";
        try (Connection con = ConexionBDD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, idCarrera.trim());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    public List<Estudiante> listarTodos() throws SQLException {
        List<Estudiante> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, apellido, idCarrera, idCategoriaPago, idNacionalidad, direccion " +
                "FROM Estudiante ORDER BY apellido, nombre";
        try (Connection con = ConexionBDD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    private Estudiante mapear(ResultSet rs) throws SQLException {
        return new Estudiante(
                rs.getString("id").trim(),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("idCarrera") != null ? rs.getString("idCarrera").trim() : null,
                rs.getString("idCategoriaPago") != null ? rs.getString("idCategoriaPago").trim() : null,
                rs.getString("idNacionalidad") != null ? rs.getString("idNacionalidad").trim() : null,
                rs.getString("direccion")
        );
    }

    /** Calcula el siguiente ID disponible como (MAX(id) + 1), en base a los IDs numéricos existentes. */
    public String siguienteId() throws SQLException {
        String sql = "SELECT MAX(CAST(RTRIM(id) AS BIGINT)) AS maxId FROM Estudiante";
        try (Connection con = ConexionBDD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                long maxId = rs.getLong("maxId");
                if (!rs.wasNull()) {
                    return String.valueOf(maxId + 1);
                }
            }
        }

        return "10160001";
    }

}
