package bdd;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class HorarioClaseDAO {

    public List<String[]> obtenerHorarioCuadriculado(String idEstudiante, String codigoPeriodo) throws SQLException {
        List<String[]> filas = new ArrayList<>();
        String sql = "{call HorarioClase(?, ?)}";
        try (Connection con = ConexionBDD.getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setString(1, idEstudiante.trim());
            cs.setString(2, codigoPeriodo.trim());

            boolean hayResultado = cs.execute();
            while (hayResultado) {
                try (ResultSet rs = cs.getResultSet()) {
                    ResultSetMetaData meta = rs.getMetaData();
                    int columnas = meta.getColumnCount();
                    while (rs.next()) {
                        String[] fila = new String[columnas];
                        for (int i = 1; i <= columnas; i++) {
                            fila[i - 1] = rs.getString(i);
                        }
                        filas.add(fila);
                    }
                }
                hayResultado = cs.getMoreResults();
            }
        }
        return filas;
    }
}