package Dao;

import Config.DatabaseConnection;
import Entities.Legajo;
import Entities.Estado;
import java.sql.*;
import java.time.LocalDate; // Import para mapeo
import java.util.ArrayList;
import java.util.List;

// Implementaci�n JDBC de LegajoDAO.
// Bloquea el 'crear' gen�rico y expone 'crearLegajo'.
public class LegajoDAOImpl implements LegajoDAO {

    // --- M�TODO TRANSACCIONAL ---
    @Override
    public void crearLegajo(Legajo legajo, Connection conn, Long empleadoId) throws Exception {
        String sql = "INSERT INTO legajos (eliminado, nro_legajo, categoria, estado, fecha_alta, observaciones, empleado_id) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setBoolean(1, false);
            stmt.setString(2, legajo.getNroLegajo());
            stmt.setString(3, legajo.getCategoria());
            stmt.setString(4, legajo.getEstado().name());
            stmt.setDate(5, legajo.getFechaAlta() != null ? Date.valueOf(legajo.getFechaAlta()) : null);
            stmt.setString(6, legajo.getObservaciones());
            stmt.setLong(7, empleadoId); // La FK

            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    legajo.setId(rs.getLong(1));
                } else {
                    throw new SQLException("La inserci�n del legajo fall�, no se obtuvo ID.");
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al crear Legajo: " + e.getMessage(), e);
        }
    }

    @Override
    public void actualizar(Legajo legajo, Connection conn) throws Exception {
        String sql = "UPDATE legajos SET nro_legajo=?, categoria=?, estado=?, fecha_alta=?, observaciones=? WHERE id=? AND eliminado=FALSE";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, legajo.getNroLegajo());
            stmt.setString(2, legajo.getCategoria());
            stmt.setString(3, legajo.getEstado().name());
            stmt.setDate(4, legajo.getFechaAlta() != null ? Date.valueOf(legajo.getFechaAlta()) : null);
            stmt.setString(5, legajo.getObservaciones());
            stmt.setLong(6, legajo.getId());

            if (stmt.executeUpdate() == 0) {
                throw new SQLException("No se actualiz� el legajo, ID no encontrado: " + legajo.getId());
            }
        } catch (SQLException e) {
            throw new Exception("Error al actualizar Legajo: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(long id, Connection conn) throws Exception {
        String sql = "UPDATE legajos SET eliminado=TRUE WHERE id=? AND eliminado=FALSE";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("ID no encontrado o previamente eliminado: " + id);
            }
        } catch (SQLException e) {
            throw new Exception("Error al eliminar Legajo: " + e.getMessage(), e);
        }
    }

    @Override
    public void recuperar(long id, Connection conn) throws Exception {
        String sql = "UPDATE legajos SET eliminado=FALSE WHERE id=? AND eliminado=TRUE";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("ID no encontrado o ya activo: " + id);
            }
        } catch (SQLException e) {
            throw new Exception("Error al recuperar Legajo: " + e.getMessage(), e);
        }
    }
    // --- M�TODOS AUT�NOMOS (Wrappers) ---
    @Override
    public void actualizar(Legajo legajo) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            this.actualizar(legajo, conn);
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
    // --- M�TODOS "BLOQUEADOS" (Por seguridad) ---
    private void lanzarErrorCrear() throws UnsupportedOperationException {
        throw new UnsupportedOperationException(
                "Operaci�n no permitida: Un legajo no puede crearse solo. "
                + "Por favor, use el m�todo: crearLegajo(Legajo, Connection, Long empleadoId)"
        );
    }

    @Override
    public void crear(Legajo legajo) throws Exception {
        lanzarErrorCrear(); // Bloquea el m�todo aut�nomo
    }

    @Override
    public void crear(Legajo legajo, Connection conn) throws Exception {
        lanzarErrorCrear(); // Bloquea el m�todo transaccional
    }

    // --- M�TODOS DE LECTURA (Aut�nomos por dise�o) ---
    @Override
    public Legajo leer(long id) throws Exception {
        String sql = "SELECT * FROM legajos WHERE id=? AND eliminado=FALSE";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearLegajo(rs);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al leer Legajo: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Legajo> leerTodos() throws Exception {
        List<Legajo> lista = new ArrayList<>();
        String sql = "SELECT * FROM legajos WHERE eliminado=FALSE";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearLegajo(rs));
            }
        } catch (SQLException e) {
            throw new Exception("Error al leer todos los Legajos: " + e.getMessage(), e);
        }
        return lista;
    }

    // --- M�TODO DE AYUDA (Mapeo) ---
    private Legajo mapearLegajo(ResultSet rs) throws SQLException {
        Legajo legajo = new Legajo();
        legajo.setId(rs.getLong("id"));
        legajo.setEliminado(rs.getBoolean("eliminado"));
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
        return legajo;
    }
}
