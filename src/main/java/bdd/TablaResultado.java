package bdd;

import java.util.List;

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