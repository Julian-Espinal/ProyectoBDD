package bdd;

import modelo.Nacionalidad;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NacionalidadDAO {

    public Nacionalidad buscarPorId(String id) throws SQLException {
        String sql = "SELECT id, nombre FROM Nacionalidad WHERE RTRIM(id) = ?";
        try (Connection con = ConexionBDD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Nacionalidad(
                            rs.getString("id").trim(),
                            rs.getString("nombre")
                    );
                }
            }
        }
        return null;
    }

    public List<Nacionalidad> listarTodos() throws SQLException {
        List<Nacionalidad> lista = new ArrayList<>();
        String sql = "SELECT id, nombre FROM Nacionalidad ORDER BY nombre";
        try (Connection con = ConexionBDD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Nacionalidad(
                        rs.getString("id").trim(),
                        rs.getString("nombre")
                ));
            }
        }
        return lista;
    }
}