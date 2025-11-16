package test;

import Config.DatabaseConnection;
import Dao.EmpleadoDAO;
import Dao.EmpleadoDAOImpl;
import Dao.LegajoDAO;
import Dao.LegajoDAOImpl;
import Entities.Empleado;
import Entities.Estado;
import Entities.Legajo;
import java.time.LocalDate;
import java.sql.Connection;

public class TestDAO {

    public static void main(String[] args) {
        try {
            // Crear empleado
            Empleado emp = new Empleado();
            emp.setNombre("Juan");
            emp.setApellido("Pérez");
            emp.setDni("12345678");
            emp.setEmail("juan.perez@empresa.com");
            emp.setFechaIngreso(LocalDate.now());
            emp.setArea("Sistemas");

            // Crear legajo
            Legajo leg = new Legajo();
            leg.setNroLegajo("LEG-001");
            leg.setCategoria("Senior");
            leg.setEstado(Estado.ACTIVO);
            leg.setFechaAlta(LocalDate.now());

            // Obtener conexión y hacer la transacción
            Connection conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            try {
                // Insertar empleado
                EmpleadoDAO empDAO = new EmpleadoDAOImpl();
                empDAO.crear(emp, conn);

                // Insertar legajo asociado
                LegajoDAO legDAO = new LegajoDAOImpl();
                legDAO.crearLegajo(leg, conn, emp.getId());

                conn.commit();
                System.out.println("Empleado y legajo creados correctamente");

            } catch (Exception e) {
                conn.rollback();
                System.out.println("Error: " + e.getMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
