package Dao;

import Config.DatabaseConnection;
import Entities.Empleado;
import Entities.Legajo;
import Entities.Estado;
import java.sql.*;
import java.time.LocalDate; // Import para mapeo
import java.util.ArrayList;
import java.util.List;

// Implementaci�n JDBC de EmpleadoDAO.
// Implementa tanto los m�todos aut�nomos (de GenericDAO)
// como los transaccionales (a�adidos en EmpleadoDAO).
public class EmpleadoDAOImpl implements EmpleadoDAO {

    // --- M�TODOS TRANSACCIONALES ---
    @Override
    public void crear(Empleado empleado, Connection conn) throws Exception {
        String sql = "INSERT INTO empleados (eliminado, nombre, apellido, dni, email, fecha_ingreso, area) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setBoolean(1, false);
            stmt.setString(2, empleado.getNombre());
            stmt.setString(3, empleado.getApellido());
            stmt.setString(4, empleado.getDni());
            stmt.setString(5, empleado.getEmail());
            stmt.setDate(6, empleado.getFechaIngreso() != null ? Date.valueOf(empleado.getFechaIngreso()) : null);
            stmt.setString(7, empleado.getArea());

            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    empleado.setId(rs.getLong(1));
                } else {
                    throw new SQLException("La inserci�n del empleado fall�, no se obtuvo ID.");
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al insertar Empleado: " + e.getMessage(), e);
        }
    }

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

            if (stmt.executeUpdate() == 0) {
                throw new SQLException("No se actualiz� el empleado, ID no encontrado: " + empleado.getId());
            }
        } catch (SQLException e) {
            throw new Exception("Error al actualizar Empleado: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(long id, Connection conn) throws Exception {
        String sql = "UPDATE empleados SET eliminado=TRUE WHERE id=? AND eliminado=FALSE";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("ID no encontrado o previamente eliminado: " + id);
            }
        } catch (SQLException e) {
            throw new Exception("Error al eliminar Empleado: " + e.getMessage(), e);
        }
    }

    @Override
    public void recuperar(long id, Connection conn) throws Exception {
        String sql = "UPDATE empleados SET eliminado=FALSE WHERE id=? AND eliminado=TRUE";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("ID no encontrado o ya activo: " + id);
            }
        } catch (SQLException e) {
            throw new Exception("Error al recuperar Empleado: " + e.getMessage(), e);
        }
    }

    // --- M�TODOS AUT�NOMOS (Implementaci�n de GenericDAO) ---
    // Estos m�todos solo abren una conexi�n y "envuelven" la llamada
    // al m�todo transaccional de arriba.
    @Override
    public void crear(Empleado empleado) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            this.crear(empleado, conn);
        }
    }

    @Override
    public void actualizar(Empleado empleado) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            this.actualizar(empleado, conn);
        }
    }

    @Override
    public void eliminar(long id) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            this.eliminar(id, conn);
        }
    }

    public void recuperar(long id) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            this.recuperar(id, conn);
        }
    }

    // --- M�TODOS DE LECTURA  ---
    @Override
    public Empleado leer(long id) throws Exception {
        String sql = "SELECT e.*, l.id AS legajo_id, l.nro_legajo, l.categoria, l.estado, l.fecha_alta, l.observaciones "
                + "FROM empleados e "
                + "LEFT JOIN legajos l ON e.id = l.empleado_id AND l.eliminado = FALSE "
                + "WHERE e.id=? AND e.eliminado=FALSE";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearEmpleado(rs);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener Empleado por ID: " + e.getMessage(), e);
        }
        return null;
    }

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

    // --- M�TODO HELPER (Mapeo) ---
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

        // Mapea el Legajo asociado (si existe)
        Long legajoId = rs.getObject("legajo_id", Long.class);

        if (legajoId != null) {
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

            empleado.setLegajo(legajo); // Asocia el Legajo
        }
        return empleado;
    }
}
