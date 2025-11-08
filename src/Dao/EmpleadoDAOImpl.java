package Dao;

import Config.DatabaseConnection;
import Entities.Empleado;
import Entities.Estado;
import Entities.Legajo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Implementación JDBC de Empleados
// ESTE DAO SOLO ES RESPONSABLE DE LA TABLA empleados
// Sigue el estilo que pide el tp (crear, leer, etc.) 
// recibe la Conexion en metodos crear actualizar borrar, para que la Capa Service pueda manejar las transacciones.

public class EmpleadoDAOImpl implements EmpleadoDAO {

    @Override
    public void crear(Empleado empleado, Connection conn) throws Exception {
        // La sentencia SQL solo se enfoca en la tabla 'empleados'
        String sql = "INSERT INTO empleados (eliminado, nombre, apellido, dni, email, fecha_ingreso, area) VALUES (?, ?, ?, ?, ?, ?, ?)";

        // Usamos try-with-resources solo para el PreparedStatement
        // no cerramos la Connection, la maneja la Capa Service
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setBoolean(1, false);
            stmt.setString(2, empleado.getNombre());
            stmt.setString(3, empleado.getApellido());
            stmt.setString(4, empleado.getDni());
            stmt.setString(5, empleado.getEmail());
            stmt.setDate(6, empleado.getFechaIngreso() != null ? Date.valueOf(empleado.getFechaIngreso()) : null);
            stmt.setString(7, empleado.getArea());

            stmt.executeUpdate();

            // Obtenemos el ID generado (igual que en el ejemplo PersonaDAO, que no recuerdo donde lo habia visto)
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    empleado.setId(rs.getLong(1));
                } else {
                    // Si no se genera ID, lanzamos excepcion 
                    throw new SQLException("La insercion del empleado fallo.");
                }
            }

        } catch (SQLException e) {
            // la Capa Service debe atrapar esta excepcion y hacer el rollback
            throw new Exception("Error al insertar Empleado: " + e.getMessage(), e);
        }
    }

    // MÉTODOS DE LECTURA
    // Estos métodos (get) si manejan su propia conexión,
    // tal como en el ejemplo de PersonaDAO.
    // JOIN para traer el Empleado con su Legajo
    @Override
    public Empleado leer(long id) throws Exception {
        String sql = "SELECT e.*, l.id AS legajo_id, l.nro_legajo, l.categoria, l.estado, l.fecha_alta, l.observaciones "
                + "FROM empleados e "
                + "LEFT JOIN legajos l ON e.id = l.empleado_id AND l.eliminado = FALSE "
                + "WHERE e.id=? AND e.eliminado=FALSE";

        // Usamos try-with-resources para Connection, PreparedStatement y ResultSet
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    //Aqui iria un bloque de codigo que se repite en los siguientes 3 metodos, por eso se unifico en un unico bloque llamado 
                    //mapearEmpleado(), de esta forma solo lo llamamos y no repetimos
                    
                    return mapearEmpleado(rs);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener Empleado por ID: " + e.getMessage(), e);
        }
        return null; // No se encontró
    }

    // JOIN para traer todos los Empleados con sus Legajos
    @Override
    public List<Empleado> leerTodos() throws Exception {
        List<Empleado> lista = new ArrayList<>();
        String sql = "SELECT e.*, l.id AS legajo_id, l.nro_legajo, l.categoria, l.estado, l.fecha_alta, l.observaciones "
                + "FROM empleados e "
                + "LEFT JOIN legajos l ON e.id = l.empleado_id AND l.eliminado = FALSE "
                + "WHERE e.eliminado=FALSE";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearEmpleado(rs));
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener todos los Empleados: " + e.getMessage(), e);
        }
        return lista;
    }

    // Actualizamos los datos del Empleado
    @Override
    public void actualizar(Empleado empleado, Connection conn) throws Exception {
        String sql = "UPDATE empleados SET nombre=?, apellido=?, email=?, fecha_ingreso=?, area=? WHERE id=? AND eliminado=FALSE";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, empleado.getNombre());
            stmt.setString(2, empleado.getApellido());
            stmt.setString(3, empleado.getEmail());
            stmt.setDate(4, empleado.getFechaIngreso() != null ? Date.valueOf(empleado.getFechaIngreso()) : null);
            stmt.setString(5, empleado.getArea());
            stmt.setLong(6, empleado.getId());

            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas == 0) {
                // Si no se actualizo nada (ej. el ID no existia), lanzamos excepcion
                throw new SQLException("No se actualizo el empleado, ID no encontrado: " + empleado.getId());
            }

           
        } catch (SQLException e) {
            throw new Exception("Error al actualizar Empleado: " + e.getMessage(), e);
        }
    }

    // Borrado lógico del Empleado
    @Override
    public void eliminar(long id, Connection conn) throws Exception {
        String sql = "UPDATE empleados SET eliminado=TRUE WHERE id=? AND eliminado=FALSE";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);

            if (stmt.executeUpdate() == 0) {
                throw new SQLException("ID no encontrado o previamente eliminado: " + id);
            }

            // NOTA: El borrado del Legajo lo hará el Service (Integrante 3)
        } catch (SQLException e) {
            throw new Exception("Error al eliminar Empleado: " + e.getMessage(), e);
        }
    }

    // Busqueda por DNI (metodo especifico de EmpleadoDAO)
    @Override
    public Empleado getByDni(String dni) throws Exception {
        String sql = "SELECT e.*, l.id AS legajo_id, l.nro_legajo, l.categoria, l.estado, l.fecha_alta, l.observaciones "
                + "FROM empleados e "
                + "LEFT JOIN legajos l ON e.id = l.empleado_id AND l.eliminado = FALSE "
                + "WHERE e.dni=? AND e.eliminado=FALSE";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, dni);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearEmpleado(rs);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al buscar Empleado por DNI: " + e.getMessage(), e);
        }
        return null;
    }

    // Metodo para convertir ResultSet a objeto Empleado
    // Evita duplicar codigo en leer(), leerTodos() y getByDni()
    private Empleado mapearEmpleado(ResultSet rs) throws SQLException {
        Empleado empleado = new Empleado();

        // Mapeamos los campos del Empleado
        empleado.setId(rs.getLong("id"));
        empleado.setEliminado(rs.getBoolean("eliminado"));
        empleado.setNombre(rs.getString("nombre"));
        empleado.setApellido(rs.getString("apellido"));
        empleado.setDni(rs.getString("dni"));
        empleado.setEmail(rs.getString("email"));

        Date fechaIngreso = rs.getDate("fecha_ingreso");
        if (fechaIngreso != null) {
            empleado.setFechaIngreso(fechaIngreso.toLocalDate());
        }
        empleado.setArea(rs.getString("area"));

        // Si tiene Legajo asociado, lo mapeamos también
        // Usamos getObject() para manejar nulls
        Long legajoId = rs.getObject("legajo_id", Long.class);

        if (legajoId != null) { // Si no era null, creamos el legajo
            Legajo legajo = new Legajo();
            legajo.setId(legajoId);
            legajo.setNroLegajo(rs.getString("nro_legajo"));
            legajo.setCategoria(rs.getString("categoria"));

            String estadoStr = rs.getString("estado");
            if (estadoStr != null) {
                legajo.setEstado(Estado.valueOf(estadoStr));
            }

            Date fechaAlta = rs.getDate("fecha_alta");
            if (fechaAlta != null) {
                legajo.setFechaAlta(fechaAlta.toLocalDate());
            }
            legajo.setObservaciones(rs.getString("observaciones"));

            empleado.setLegajo(legajo); // Asociamos el Legajo al Empleado
        }

        return empleado;
    }
}
