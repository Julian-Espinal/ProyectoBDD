package bdd;

import modelo.DiaSemana;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DiaSemanaDAO {

    public List<DiaSemana> listarTodos() throws SQLException {
        List<DiaSemana> lista = new ArrayList<>();
        String sql = "SELECT dia, descripcion, nombreCorto FROM DiaSemana ORDER BY dia";
        try (Connection con = ConexionBDD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new DiaSemana(
                        rs.getInt("dia"),
                        rs.getString("descripcion") != null ? rs.getString("descripcion").trim() : null,
                        rs.getString("nombreCorto") != null ? rs.getString("nombreCorto").trim() : null
                ));
            }
        }
        return lista;
    }
}