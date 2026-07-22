package modelo;

public class GrupoInscrito {
    private String codigoPeriodo;
    private String idEstudiante;
    private String codigoAsignatura;
    private String numeroGrupo;

    public GrupoInscrito() {
    }

    public GrupoInscrito(String codigoPeriodo, String idEstudiante, String codigoAsignatura, String numeroGrupo) {
        this.codigoPeriodo = codigoPeriodo;
        this.idEstudiante = idEstudiante;
        this.codigoAsignatura = codigoAsignatura;
        this.numeroGrupo = numeroGrupo;
    }

    public String getCodigoPeriodo() {
        return codigoPeriodo;
    }

    public void setCodigoPeriodo(String codigoPeriodo) {
        this.codigoPeriodo = codigoPeriodo;
    }

    public String getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(String idEstudiante) {
        this.idEstudiante = idEstudiante;
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

    @Override
    public String toString() {
        return "modelo.GrupoInscrito{codigoPeriodo='" + codigoPeriodo + "', idEstudiante='" + idEstudiante
                + "', codigoAsignatura='" + codigoAsignatura + "', numeroGrupo='" + numeroGrupo + "'}";
    }
}