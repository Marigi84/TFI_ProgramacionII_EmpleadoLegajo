/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DatabaseConnection {
    

    // ? Datos de conexión - Se configuran directamente en el código
    private static final String URL = "jdbc:mysql://localhost:3306/empresa?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";  // vacío si usás XAMPP sin contraseña

    static {
        try {
            // ? Carga del driver JDBC de MySQL una sola vez
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // ? Se lanza una excepción si el driver no está disponible
            throw new RuntimeException("Error: No se encontró el driver JDBC.", e);
        }
    }

    /**
     * ? Método para obtener una conexión a la base de datos.
     * @return Connection si la conexión es exitosa.
     * @throws SQLException Si hay un problema al conectarse.
     */
    public static Connection getConnection() throws SQLException {
        // Validación adicional: evita credenciales vacías
        if (URL == null || URL.isEmpty() || USER == null || USER.isEmpty()) {
            throw new SQLException("Configuración de la base de datos incompleta o inválida.");
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

