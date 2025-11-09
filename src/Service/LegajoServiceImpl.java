package Service;

import Config.DatabaseConnection;
import Config.TransactionManager;
import Dao.LegajoDAO;
import Dao.LegajoDAOImpl;
import Entities.Legajo;

import java.sql.Connection;
import java.util.List;

/**
 * Implementación del servicio de negocio para la entidad Legajo.
 * Aplica validaciones y restricciones de integridad referencial.
 * 
 * IMPORTANTE: Un legajo NO puede existir sin un empleado asociado.
 */
public class LegajoServiceImpl implements LegajoService {
    private final LegajoDAO legajoDAO;
    
    /**
     * Constructor que inicializa el DAO necesario.
     */
    public LegajoServiceImpl() {
        this.legajoDAO = new LegajoDAOImpl();
    }
    
    /**
     * Operación NO soportada: un legajo no puede insertarse sin empleado asociado.
     * Use EmpleadoService.crearEmpleadoConLegajo() en su lugar.
     *
     * @param legajo Legajo a insertar
     * @throws UnsupportedOperationException Siempre
     */
    @Override
    public void insertar(Legajo legajo) throws Exception {
        throw new UnsupportedOperationException(
            "No se puede insertar un legajo sin empleado asociado. " +
            "Use EmpleadoService.crearEmpleadoConLegajo() para crear legajo con empleado."
        );
    }
    
    /**
     * Actualiza un legajo existente.
     *
     * @param legajo Legajo con los datos actualizados
     * @throws Exception Si la validación falla o el legajo no existe
     */
    @Override
    public void actualizar(Legajo legajo) throws Exception {
        if (legajo.getId() == null || legajo.getId() <= 0) {
            throw new IllegalArgumentException("El ID del legajo debe ser mayor a 0 para actualizar");
        }
        validarLegajo(legajo);
        
        try (Connection conn = DatabaseConnection.getConnection();
             TransactionManager tx = new TransactionManager(conn)) {
            
            tx.startTransaction();
            legajoDAO.actualizar(legajo, conn);
            tx.commit();
            
        } catch (Exception e) {
            throw new Exception("Error al actualizar legajo: " + e.getMessage(), e);
        }
    }
    
    /**
     * Elimina lógicamente un legajo (soft delete).
     *
     * @param id ID del legajo a eliminar
     * @throws Exception Si el ID es inválido o no existe el legajo
     */
    @Override
    public void eliminar(Long id) throws Exception {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor a 0");
        }
        
        try (Connection conn = DatabaseConnection.getConnection();
             TransactionManager tx = new TransactionManager(conn)) {
            
            tx.startTransaction();
            legajoDAO.eliminar(id, conn);
            tx.commit();
            
        } catch (Exception e) {
            throw new Exception("Error al eliminar legajo: " + e.getMessage(), e);
        }
    }
    
    /**
     * Obtiene un legajo por su ID.
     *
     * @param id ID del legajo a buscar
     * @return Legajo encontrado, o null si no existe
     * @throws Exception Si el ID es inválido
     */
    @Override
    public Legajo getById(Long id) throws Exception {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor a 0");
        }
        return legajoDAO.leer(id);
    }
    
    /**
     * Obtiene todos los legajos activos.
     *
     * @return Lista de legajos
     * @throws Exception Si hay error de BD
     */
    @Override
    public List<Legajo> getAll() throws Exception {
        return legajoDAO.leerTodos();
    }
    
    /**
     * Valida que un legajo tenga los datos obligatorios.
     *
     * @param legajo Legajo a validar
     * @throws IllegalArgumentException Si alguna validación falla
     */
    private void validarLegajo(Legajo legajo) {
        if (legajo == null) {
            throw new IllegalArgumentException("El legajo no puede ser null");
        }
        if (legajo.getNroLegajo() == null || legajo.getNroLegajo().trim().isEmpty()) {
            throw new IllegalArgumentException("El número de legajo no puede estar vacío");
        }
        if (legajo.getEstado() == null) {
            throw new IllegalArgumentException("El estado del legajo no puede ser null (debe ser ACTIVO o INACTIVO)");
        }
    }
}
