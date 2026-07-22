package modelo;

import java.util.Date;

public class PeriodoAcademico {
    private String codigo;
    private String descripcion;
    private Date fechaPrematricula;
    private Date fechaRetiro;
    private Date fechaPublicacion;


    public PeriodoAcademico(String codigo, String descripcion, Date fechaPrematricula, Date fechaRetiro, Date fechaPublicacion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.fechaPrematricula = fechaPrematricula;
        this.fechaRetiro = fechaRetiro;
        this.fechaPublicacion = fechaPublicacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Date getFechaPrematricula() {
        return fechaPrematricula;
    }

    public void setFechaPrematricula(Date fechaPrematricula) {
        this.fechaPrematricula = fechaPrematricula;
    }

    public Date getFechaRetiro() {
        return fechaRetiro;
    }

    public void setFechaRetiro(Date fechaRetiro) {
        this.fechaRetiro = fechaRetiro;
    }

    public Date getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(Date fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }
}
