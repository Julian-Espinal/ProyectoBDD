package modelo;

public class DiaSemana {
    private Integer dia;
    private String descripcion;
    private String nombreCorto;

    public DiaSemana(Integer dia, String descripcion, String nombreCorto) {
        this.dia = dia;
        this.descripcion = descripcion;
        this.nombreCorto = nombreCorto;
    }

    public Integer getDia() {
        return dia;
    }

    public void setDia(Integer dia) {
        this.dia = dia;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getNombreCorto() {
        return nombreCorto;
    }

    public void setNombreCorto(String nombreCorto) {
        this.nombreCorto = nombreCorto;
    }
}
