package modelo;


public class Grupo {
    private String codigoPeriodo;
    private String codigoAsignatura;
    private String numeroGrupo;
    private Integer cupoGrupo;
    private String horario;

    public Grupo() {
    }

    public Grupo(String codigoPeriodo, String codigoAsignatura, String numeroGrupo,
                 Integer cupoGrupo, String horario) {
        this.codigoPeriodo = codigoPeriodo;
        this.codigoAsignatura = codigoAsignatura;
        this.numeroGrupo = numeroGrupo;
        this.cupoGrupo = cupoGrupo;
        this.horario = horario;
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

    public Integer getCupoGrupo() {
        return cupoGrupo;
    }

    public void setCupoGrupo(Integer cupoGrupo) {
        this.cupoGrupo = cupoGrupo;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    @Override
    public String toString() {
        return "modelo.Grupo{codigoPeriodo='" + codigoPeriodo + "', codigoAsignatura='" + codigoAsignatura
                + "', numeroGrupo='" + numeroGrupo + "'}";
    }
}