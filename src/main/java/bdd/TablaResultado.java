package bdd;

import java.util.List;

/**
 * Contenedor simple para el resultado de una consulta/stored procedure
 * que se va a mostrar en una tabla dinámica (columnas desconocidas en tiempo
 * de compilación). Evita repetir la lógica de leer ResultSetMetaData en
 * cada controller que necesite pintar una tabla genérica.
 */
public class TablaResultado {
    private final List<String> columnas;
    private final List<String[]> filas;

    public TablaResultado(List<String> columnas, List<String[]> filas) {
        this.columnas = columnas;
        this.filas = filas;
    }

    public List<String> getColumnas() {
        return columnas;
    }

    public List<String[]> getFilas() {
        return filas;
    }
}