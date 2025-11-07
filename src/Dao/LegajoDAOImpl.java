package Dao;

import Config.DatabaseConnection;
import Entities.Estado;
import Entities.Legajo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Implementación JDBC del DAO de Legajo
public class LegajoDAOImpl implements LegajoDAO {

    // Método específico de la clase LegajoDAO para crear Legajo con el empleadoId
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
            stmt.setLong(7, empleadoId); // La clave foránea
            
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    legajo.setId(rs.getLong(1)); // Asignamos el ID
                } else {
                    throw new SQLException("La inserción del legajo falló, no se obtuvo ID.");
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al crear Legajo: " + e.getMessage(), e);
        }
    }

    // Override del método genérico, no se usa, pero hay que escribirlo obligatoriamente por la implementación de GenericDAO.
    @Override
    public void crear(Legajo legajo, Connection conn) throws Exception {
        throw new UnsupportedOperationException("Operación no permitida: Un legajo no puede crearse solo");
    }

    // Actualizamos solo los campos editables
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
                 throw new SQLException("No se actualizó el legajo, ID no encontrado: " + legajo.getId());
            }
        } catch (SQLException e) {
            throw new Exception("Error al actualizar Legajo: " + e.getMessage(), e);
        }
    }

    // Borrado lógico
    @Override
    public void eliminar(long id, Connection conn) throws Exception {
        String sql = "UPDATE legajos SET eliminado=TRUE WHERE id=? AND eliminado=FALSE";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            if (stmt.executeUpdate() == 0) {
                 throw new SQLException("No se eliminó el legajo, ID no encontrado o previamente eliminado: " + id);
            }
        } catch (SQLException e) {
            throw new Exception("Error al eliminar Legajo: " + e.getMessage(), e);
        }
    }


    // Busca un solo Legajo por ID
    @Override
    public Legajo leer(long id) throws Exception {
        String sql = "SELECT * FROM legajos WHERE id=? AND eliminado=FALSE";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearLegajo(rs); // Usamos el método auxiliar
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al leer Legajo: " + e.getMessage(), e);
        }
        return null;
    }

    // Obtiene todos los legajos no eliminados
    @Override
    public List<Legajo> leerTodos() throws Exception {
        List<Legajo> lista = new ArrayList<>();
        String sql = "SELECT * FROM legajos WHERE eliminado=FALSE";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                lista.add(mapearLegajo(rs));
            }
        } catch (SQLException e) {
            throw new Exception("Error al leer todos los Legajos: " + e.getMessage(), e);
        }
        return lista;
    }

    // Método auxiliar para convertir ResultSet a objeto Legajo
    private Legajo mapearLegajo(ResultSet rs) throws SQLException {
        Legajo legajo = new Legajo();
        legajo.setId(rs.getLong("id"));
        legajo.setEliminado(rs.getBoolean("eliminado"));
        legajo.setNroLegajo(rs.getString("nro_legajo"));
        legajo.setCategoria(rs.getString("categoria"));
        
        String estadoStr = rs.getString("estado");
        if(estadoStr != null) {
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