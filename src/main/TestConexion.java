/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

import Config.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class TestConexion {
    

    public static void main(String[] args) {
        /**
         * ? Se usa un bloque try-with-resources para asegurar que la conexión
         *     se cierre automáticamente al salir del bloque.
         * ? No es necesario llamar explícitamente a conn.close().
         */
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn != null) {
                System.out.println("? Conexión establecida con éxito.");

                // ? Consulta simple a la tabla empleados
                String sql = "SELECT id, nombre, apellido, dni, email, area FROM empleados";

                try (PreparedStatement pstmt = conn.prepareStatement(sql);
                     ResultSet rs = pstmt.executeQuery()) {

                    System.out.println("? Listado de empleados registrados en la base de datos:");
                    System.out.println("------------------------------------------------------");

                    while (rs.next()) {
                        long id = rs.getLong("id");
                        String nombre = rs.getString("nombre");
                        String apellido = rs.getString("apellido");
                        String dni = rs.getString("dni");
                        String email = rs.getString("email");
                        String area = rs.getString("area");

                        System.out.println("ID: " + id +
                                " | Nombre: " + nombre +
                                " | Apellido: " + apellido +
                                " | DNI: " + dni +
                                " | Email: " + email +
                                " | Área: " + area);
                    }
                    System.out.println("------------------------------------------------------");
                }

                // ? Prueba adicional: contar legajos registrados
                String sql2 = "SELECT COUNT(*) AS total_legajos FROM legajos";
                try (PreparedStatement pstmt2 = conn.prepareStatement(sql2);
                     ResultSet rs2 = pstmt2.executeQuery()) {

                    if (rs2.next()) {
                        int total = rs2.getInt("total_legajos");
                        System.out.println("? Total de legajos registrados: " + total);
                    }
                }

            } else {
                System.out.println("? No se pudo establecer la conexión.");
            }

        } catch (SQLException e) {
            // ? Manejo de errores en la conexión a la base de datos
            System.err.println("?? Error al conectar a la base de datos: " + e.getMessage());
            e.printStackTrace(); // Imprime el stack trace completo para depuración
        }
    }
}
