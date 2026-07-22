package modelo;

public class Carrera {
    private String id;
    private String nombreCarrera;


    public Carrera(String id, String nombreCarrera) {
        this.id = id;
        this.nombreCarrera = nombreCarrera;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombreCarrera() {
        return nombreCarrera;
    }

    public void setNombreCarrera(String nombreCarrera) {
        this.nombreCarrera = nombreCarrera;
    }
}
