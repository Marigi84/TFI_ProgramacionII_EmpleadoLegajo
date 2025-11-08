/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DatabaseConnection {
    

    // ? Datos de conexi�n - Se configuran directamente en el c�digo
    private static final String URL = "jdbc:mysql://localhost:3306/empresa?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "root";  // Prueba con "root" primero

    static {
        try {
            // ? Carga del driver JDBC de MySQL una sola vez
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // ? Se lanza una excepci�n si el driver no est� disponible
            throw new RuntimeException("Error: No se encontr� el driver JDBC.", e);
        }
    }

    /**
     * ? M�todo para obtener una conexi�n a la base de datos.
     * @return Connection si la conexi�n es exitosa.
     * @throws SQLException Si hay un problema al conectarse.
     */
    public static Connection getConnection() throws SQLException {
        // Validaci�n adicional: evita credenciales vac�as
        if (URL == null || URL.isEmpty() || USER == null || USER.isEmpty()) {
            throw new SQLException("Configuraci�n de la base de datos incompleta o inv�lida.");
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

