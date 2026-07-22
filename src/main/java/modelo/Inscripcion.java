package modelo;

import java.util.Date;

public class Inscripcion {
    private String codigoPeriodo;
    private String idEstudiante;
    private Date fechaInscripcion;
    public Inscripcion(String codigoPeriodo, String idEstudiante, Date fechaInscripcion) {
        this.codigoPeriodo = codigoPeriodo;
        this.idEstudiante = idEstudiante;
        this.fechaInscripcion = fechaInscripcion;
    }

    public String getCodigoPeriodo() {
        return codigoPeriodo;
    }

    public void setCodigoPeriodo(String codigoPeriodo) {
        this.codigoPeriodo = codigoPeriodo;
    }

    public Date getFechaInscripcion() {
        return fechaInscripcion;
    }

    public void setFechaInscripcion(Date fechaInscripcion) {
        this.fechaInscripcion = fechaInscripcion;
    }

    public String getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(String idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

}
