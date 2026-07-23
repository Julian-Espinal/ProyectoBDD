package bdd;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBDD {

    private static final String SERVIDOR = "localhost";
    private static final int PUERTO = 1433;
    private static final String BASE_DATOS = "AcadDB(RDGC0002)";
    private static final String USUARIO = "app_academico";
    private static final String PASSWORD = "TuNuevaPassword123";

    private static Connection conexion = null;

    private ConexionBDD() {
    }


    public static Connection getConexion() throws SQLException {
        if (conexion == null || conexion.isClosed()) {
            String url = String.format(
                    "jdbc:sqlserver://%s:%d;databaseName=%s;encrypt=true;trustServerCertificate=true;",
                    SERVIDOR, PUERTO, BASE_DATOS
            );
            try {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            } catch (ClassNotFoundException e) {
                throw new SQLException("No se encontró el driver de SQL Server (mssql-jdbc) en el classpath.", e);
            }
            conexion = DriverManager.getConnection(url, USUARIO, PASSWORD);
        }
        return conexion;
    }

    public static void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }
}
