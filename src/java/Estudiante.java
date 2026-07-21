package java;

public class Estudiante {
    private String id;
    private String nombre;
    private String apellido;
    private String idCarrera;
    private String idCategoriaPago;
    private String idNacionalidad;
    private String direccion;

    public Estudiante() {
    }

    public Estudiante(String id, String nombre, String apellido, String idCarrera,
                      String idCategoriaPago, String idNacionalidad, String direccion) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.idCarrera = idCarrera;
        this.idCategoriaPago = idCategoriaPago;
        this.idNacionalidad = idNacionalidad;
        this.direccion = direccion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getIdCarrera() {
        return idCarrera;
    }

    public void setIdCarrera(String idCarrera) {
        this.idCarrera = idCarrera;
    }

    public String getIdCategoriaPago() {
        return idCategoriaPago;
    }

    public void setIdCategoriaPago(String idCategoriaPago) {
        this.idCategoriaPago = idCategoriaPago;
    }

    public String getIdNacionalidad() {
        return idNacionalidad;
    }

    public void setIdNacionalidad(String idNacionalidad) {
        this.idNacionalidad = idNacionalidad;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    @Override
    public String toString() {
        return "Estudiante{id='" + id + "', nombre='" + nombre + "', apellido='" + apellido + "'}";
    }
}
