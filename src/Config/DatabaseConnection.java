/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DatabaseConnection {
   
    /** URL de conexión JDBC: configurable o usa valor por defecto */
    private static final String URL = System.getProperty("db.url", 
            "jdbc:mysql://localhost:3306/empresa?useSSL=false&serverTimezone=UTC");

    /** Usuario de la base de datos: configurable o usa "root" */
    private static final String USER = System.getProperty("db.user", "root");

    /** Contraseña: configurable o vacía si no se define */
    private static final String PASSWORD = System.getProperty("db.password", "");

    /**
     * Bloque estático de inicialización.
     * Se ejecuta UNA SOLA VEZ cuando la clase se carga en memoria.
     * 
     * - Carga el driver JDBC de MySQL
     * - Valida la configuración básica (fail-fast)
     */
    static {
        try {
            // Carga explícita del driver JDBC
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Validación temprana de configuración
            validateConfiguration();
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError("? Error: No se encontró el driver JDBC de MySQL. " + e.getMessage());
        } catch (IllegalStateException e) {
            throw new ExceptionInInitializerError("? Error en la configuración de la base de datos: " + e.getMessage());
        }
    }

    /** Constructor privado: evita instanciación (clase utilitaria). */
    private DatabaseConnection() {
        throw new UnsupportedOperationException("Clase utilitaria: no debe instanciarse.");
    }

    /**
     * Devuelve una nueva conexión a la base de datos.
     * 
     * ? Uso recomendado:
     * <pre>
     * try (Connection conn = DatabaseConnection.getConnection()) {
     *     // usar la conexión
     * }
     * </pre>
     * 
     * @return conexión JDBC activa
     * @throws SQLException si no se puede conectar
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Valida que la configuración no tenga errores.
     * 
     * Reglas:
     * - URL y USER no deben ser null ni vacíos.
     * - PASSWORD puede ser vacío, pero no null.
     */
    private static void validateConfiguration() {
        if (URL == null || URL.trim().isEmpty()) {
            throw new IllegalStateException("La URL de la base de datos no está configurada.");
        }
        if (USER == null || USER.trim().isEmpty()) {
            throw new IllegalStateException("El usuario de la base de datos no está configurado.");
        }
        if (PASSWORD == null) {
            throw new IllegalStateException("La contraseña no puede ser null (puede ser vacía).");
        }
    }

}
    