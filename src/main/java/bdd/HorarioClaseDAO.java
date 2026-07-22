package bdd;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class HorarioClaseDAO {

    /**
     * Ejecuta el SP HorarioClase y devuelve tanto los nombres de columna
     * como las filas, listos para pintar en una tabla dinámica.
     */
    public TablaResultado obtenerHorarioCuadriculado(String idEstudiante, String codigoPeriodo) throws SQLException {
        List<String> columnas = new ArrayList<>();
        List<String[]> filas = new ArrayList<>();
        String sql = "{call HorarioClase(?, ?)}";

        try (Connection con = ConexionBDD.getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setString(1, idEstudiante.trim());
            cs.setString(2, codigoPeriodo.trim());

            boolean hayResultado = cs.execute();
            boolean primerResultSet = true;

            while (hayResultado) {
                try (ResultSet rs = cs.getResultSet()) {
                    ResultSetMetaData meta = rs.getMetaData();
                    int numColumnas = meta.getColumnCount();

                    if (primerResultSet) {
                        for (int i = 1; i <= numColumnas; i++) {
                            columnas.add(meta.getColumnLabel(i));
                        }
                        primerResultSet = false;
                    }

                    while (rs.next()) {
                        String[] fila = new String[numColumnas];
                        for (int i = 1; i <= numColumnas; i++) {
                            fila[i - 1] = rs.getString(i);
                        }
                        filas.add(fila);
                    }
                }
                hayResultado = cs.getMoreResults();
            }
        }
        return new TablaResultado(columnas, filas);
    }
}