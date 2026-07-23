package bdd;

import modelo.CategoriaPago;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoriaPagoDAO {

    public CategoriaPago buscarPorId(String id) throws SQLException {
        String sql = "SELECT id, descripcion FROM CategoriaPago WHERE RTRIM(id) = ?";
        try (Connection con = ConexionBDD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new CategoriaPago(
                            rs.getString("id").trim(),
                            rs.getString("descripcion")
                    );
                }
            }
        }
        return null;
    }

    public List<CategoriaPago> listarTodos() throws SQLException {
        List<CategoriaPago> lista = new ArrayList<>();
        String sql = "SELECT id, descripcion FROM CategoriaPago ORDER BY descripcion";
        try (Connection con = ConexionBDD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new CategoriaPago(
                        rs.getString("id").trim(),
                        rs.getString("descripcion")
                ));
            }
        }
        return lista;
    }
}