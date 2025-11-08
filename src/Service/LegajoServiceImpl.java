package Service;

import Config.DatabaseConnection;
import Dao.LegajoDAO;
import Dao.LegajoDAOImpl;
import Entities.Legajo;

import java.sql.Connection;
import java.util.List;

/**
 * Implementación del servicio de negocio para la entidad Legajo.
 * Capa intermedia entre la UI y el DAO que aplica validaciones y restricciones de negocio.
 *
 * Responsabilidades:
 * - Validar que los datos del legajo sean correctos ANTES de persistir
 * - Aplicar restricciones de integridad referencial
 * - Coordinar transacciones para operaciones de actualización y eliminación
 * - Bloquear operaciones inválidas (inserción directa sin empleado)
 *
 * IMPORTANTE: Un legajo NO puede existir sin un empleado asociado debido a la
 * constraint de BD: empleado_id NOT NULL en la tabla legajos.
 *
 * Patrón: Service Layer con transacciones manuales
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
     *
     * Razón técnica: La columna empleado_id en la tabla legajos tiene constraint
     * NOT NULL, por lo que la BD rechazaría cualquier INSERT sin FK válida.
     *
     * ALTERNATIVA CORRECTA: Use EmpleadoService.crearEmpleadoConLegajo()
     * que crea ambas entidades en una transacción ACID.
     *
     * @param legajo Legajo a insertar
     * @throws UnsupportedOperationException Siempre (operación bloqueada por diseño)
     */
    @Override
    public void insertar(Legajo legajo) throws Exception {
        throw new UnsupportedOperationException(
            "No se puede insertar un legajo sin empleado asociado. " +
            "Use EmpleadoService.crearEmpleadoConLegajo() para crear legajo con empleado."
        );
    }
    
    /**
     * Actualiza un legajo existente en la base de datos.
     *
     * Validaciones:
     * - El legajo debe tener ID > 0 (debe estar persistido)
     * - Número de legajo y estado son obligatorios
     * - Estado debe ser ACTIVO o INACTIVO
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
        
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            legajoDAO.actualizar(legajo, conn);
            
            conn.commit();
        } catch (Exception e) {
            if (conn != null) conn.rollback();
            throw new Exception("Error al actualizar legajo: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }
    
    /**
     * Elimina lógicamente un legajo (soft delete).
     * Marca el legajo como eliminado sin borrarlo físicamente de la BD.
     *
     * @param id ID del legajo a eliminar
     * @throws Exception Si id <= 0 o no existe el legajo
     */
    @Override
    public void eliminar(Long id) throws Exception {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor a 0");
        }
        
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            legajoDAO.eliminar(id, conn);
            
            conn.commit();
        } catch (Exception e) {
            if (conn != null) conn.rollback();
            throw new Exception("Error al eliminar legajo: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }
    
    /**
     * Obtiene un legajo por su ID.
     *
     * @param id ID del legajo a buscar
     * @return Legajo encontrado, o null si no existe o está eliminado
     * @throws Exception Si id <= 0 o hay error de BD
     */
    @Override
    public Legajo getById(Long id) throws Exception {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor a 0");
        }
        return legajoDAO.leer(id);
    }
    
    /**
     * Obtiene todos los legajos activos (no eliminados).
     *
     * @return Lista de legajos activos (puede estar vacía)
     * @throws Exception Si hay error de BD
     */
    @Override
    public List<Legajo> getAll() throws Exception {
        return legajoDAO.leerTodos();
    }
    
    /**
     * Valida que un legajo tenga todos los datos obligatorios.
     *
     * Reglas de negocio aplicadas:
     * - Número de legajo es obligatorio (no null, no vacío)
     * - Estado es obligatorio (debe ser ACTIVO o INACTIVO del enum Estado)
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
