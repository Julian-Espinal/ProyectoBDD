package bdd;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Maneja la conexión (Singleton) hacia la base de datos SQL Server "AcadDB(RDGC0002)".
 *
 * IMPORTANTE:
 *  - Ajusta SERVIDOR, PUERTO, USUARIO y PASSWORD según tu instalación de SQL Server.
 *  - El nombre de la base de datos tiene paréntesis "AcadDB(RDGC0002)", lo cual es
 *    válido pero poco común. Si tienes problemas de conexión, considera renombrar
 *    la base de datos sin paréntesis en SQL Server Management Studio.
 *  - Necesitas agregar la dependencia del driver mssql-jdbc a tu proyecto
 *    (ver pom.xml / build.gradle o el .jar en el classpath).
 */
public class ConexionBDD {

    private static final String SERVIDOR = "localhost";
    private static final int PUERTO = 1433;
    private static final String BASE_DATOS = "AcadDB(RDGC0002)";
    private static final String USUARIO = "sa";
    private static final String PASSWORD = "academico";

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
