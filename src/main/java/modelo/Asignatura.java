package modelo;

public class Asignatura {
    private String codigo;
    private String nombre;
    private Integer creditos;
    private Integer horasTeoricas;
    private Integer horasPracticas;


    public Asignatura(String codigo, String nombre, Integer creditos, Integer horasTeoricas, Integer horasPracticas) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.creditos = creditos;
        this.horasTeoricas = horasTeoricas;
        this.horasPracticas = horasPracticas;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getCreditos() {
        return creditos;
    }

    public void setCreditos(Integer creditos) {
        this.creditos = creditos;
    }

    public Integer getHorasTeoricas() {
        return horasTeoricas;
    }

    public void setHorasTeoricas(Integer horasTeoricas) {
        this.horasTeoricas = horasTeoricas;
    }

    public Integer getHorasPracticas() {
        return horasPracticas;
    }

    public void setHorasPracticas(Integer horasPracticas) {
        this.horasPracticas = horasPracticas;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
}


