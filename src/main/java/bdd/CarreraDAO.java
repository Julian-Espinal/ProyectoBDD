package bdd;

import modelo.Carrera;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CarreraDAO {

    public Carrera buscarPorId(String id) throws SQLException {
        String sql = "SELECT id, nombreCarrera FROM Carrera WHERE RTRIM(id) = ?";
        try (Connection con = ConexionBDD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Carrera(
                            rs.getString("id").trim(),
                            rs.getString("nombreCarrera")
                    );
                }
            }
        }
        return null;
    }
}