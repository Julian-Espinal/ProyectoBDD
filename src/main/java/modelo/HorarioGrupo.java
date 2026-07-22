package modelo;

import java.time.LocalTime;

public class HorarioGrupo {
    private String codigoPeriodo;
    private String codigoAsignatura;
    private String numeroGrupo;
    private int dia;
    private LocalTime horaInicio;
    private LocalTime horaFin;

    public HorarioGrupo() {
    }

    public HorarioGrupo(String codigoPeriodo, String codigoAsignatura, String numeroGrupo,
                        int dia, LocalTime horaInicio, LocalTime horaFin) {
        this.codigoPeriodo = codigoPeriodo;
        this.codigoAsignatura = codigoAsignatura;
        this.numeroGrupo = numeroGrupo;
        this.dia = dia;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    public String getCodigoPeriodo() {
        return codigoPeriodo;
    }

    public void setCodigoPeriodo(String codigoPeriodo) {
        this.codigoPeriodo = codigoPeriodo;
    }

    public String getCodigoAsignatura() {
        return codigoAsignatura;
    }

    public void setCodigoAsignatura(String codigoAsignatura) {
        this.codigoAsignatura = codigoAsignatura;
    }

    public String getNumeroGrupo() {
        return numeroGrupo;
    }

    public void setNumeroGrupo(String numeroGrupo) {
        this.numeroGrupo = numeroGrupo;
    }

    public int getDia() {
        return dia;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    @Override
    public String toString() {
        return "modelo.HorarioGrupo{codigoAsignatura='" + codigoAsignatura + "', numeroGrupo='" + numeroGrupo
                + "', dia=" + dia + ", horaInicio=" + horaInicio + ", horaFin=" + horaFin + "}";
    }
}