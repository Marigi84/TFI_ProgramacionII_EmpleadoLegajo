package Service;

import Config.DatabaseConnection;
import Config.TransactionManager;
import Dao.EmpleadoDAO;
import Dao.EmpleadoDAOImpl;
import Dao.LegajoDAO;
import Dao.LegajoDAOImpl;
import Entities.Empleado;
import Entities.Legajo;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

/**
 * Implementación del servicio de negocio para la entidad Empleado.
 * Aplica validaciones y coordina transacciones entre Empleado y Legajo usando TransactionManager.
 */
public class EmpleadoServiceImpl implements EmpleadoService {
    private final EmpleadoDAO empleadoDAO;
    private final LegajoDAO legajoDAO;
    
    /**
     * Constructor que inicializa los DAOs necesarios.
     * Instancia directamente los DAOImpl (podría mejorarse con inyección de dependencias).
     */
    public EmpleadoServiceImpl() {
        this.empleadoDAO = new EmpleadoDAOImpl();
        this.legajoDAO = new LegajoDAOImpl();
    }
    
    /**
     * Inserta un empleado sin legajo asociado.
     * Valida datos obligatorios y unicidad del DNI.
     *
     * @param empleado Empleado a insertar
     * @throws Exception Si la validación falla o hay error de BD
     */
    @Override
    public void insertar(Empleado empleado) throws Exception {
        validarEmpleado(empleado);
        validarDniUnico(empleado.getDni(), null);
        
        try (Connection conn = DatabaseConnection.getConnection();
             TransactionManager tx = new TransactionManager(conn)) {
            
            tx.startTransaction();
            empleadoDAO.crear(empleado, conn);
            tx.commit();
            
        } catch (Exception e) {
            throw new Exception("Error al insertar empleado: " + e.getMessage(), e);
        }
    }
    
    /**
     * Crea un empleado con su legajo asociado en una única transacción.
     * Garantiza que ambos se creen o ninguno (transacción ACID).
     *
     * @param empleado Empleado a crear (debe contener objeto Legajo asociado)
     * @throws Exception Si la validación falla o hay error en la transacción
     */
    @Override
    public void crearEmpleadoConLegajo(Empleado empleado) throws Exception {
        // Validar datos antes de iniciar transacción
        validarEmpleado(empleado);
        validarDniUnico(empleado.getDni(), null);
        
        if (empleado.getLegajo() != null) {
            validarLegajo(empleado.getLegajo());
        }
        
        // Ejecutar transacción
        try (Connection conn = DatabaseConnection.getConnection();
             TransactionManager tx = new TransactionManager(conn)) {
            
            tx.startTransaction();
            
            // Insertar empleado (obtiene ID autogenerado)
            empleadoDAO.crear(empleado, conn);
            
            // Insertar legajo con FK empleado_id
            if (empleado.getLegajo() != null) {
                legajoDAO.crearLegajo(empleado.getLegajo(), conn, empleado.getId());
            }
            
            tx.commit();
            
        } catch (Exception e) {
            throw new Exception("Error al crear empleado con legajo: " + e.getMessage(), e);
        }
    }
    
    /**
     * Actualiza los datos de un empleado existente.
     * Valida que el ID exista y que el DNI sea único.
     *
     * @param empleado Empleado con los datos actualizados
     * @throws Exception Si la validación falla o el empleado no existe
     */
    @Override
    public void actualizar(Empleado empleado) throws Exception {
        validarEmpleado(empleado);
        if (empleado.getId() == null || empleado.getId() <= 0) {
            throw new IllegalArgumentException("El ID del empleado debe ser mayor a 0 para actualizar");
        }
        validarDniUnico(empleado.getDni(), empleado.getId());
        
        try (Connection conn = DatabaseConnection.getConnection();
             TransactionManager tx = new TransactionManager(conn)) {
            
            tx.startTransaction();
            empleadoDAO.actualizar(empleado, conn);
            tx.commit();
            
        } catch (Exception e) {
            throw new Exception("Error al actualizar empleado: " + e.getMessage(), e);
        }
    }
    
    /**
     * Elimina lógicamente un empleado (soft delete).
     *
     * @param id ID del empleado a eliminar
     * @throws Exception Si el ID es inválido o no existe el empleado
     */
    @Override
    public void eliminar(Long id) throws Exception {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor a 0");
        }
        
        try (Connection conn = DatabaseConnection.getConnection();
             TransactionManager tx = new TransactionManager(conn)) {
            
            tx.startTransaction();
            empleadoDAO.eliminar(id, conn);
            tx.commit();
            
        } catch (Exception e) {
            throw new Exception("Error al eliminar empleado: " + e.getMessage(), e);
        }
    }
    
    /**
     * Obtiene un empleado por su ID.
     *
     * @param id ID del empleado a buscar
     * @return Empleado encontrado, o null si no existe
     * @throws Exception Si el ID es inválido
     */
    @Override
    public Empleado getById(Long id) throws Exception {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor a 0");
        }
        return empleadoDAO.leer(id);
    }
    
    /**
     * Obtiene todos los empleados activos.
     *
     * @return Lista de empleados
     * @throws Exception Si hay error de BD
     */
    @Override
    public List<Empleado> getAll() throws Exception {
        return empleadoDAO.leerTodos();
    }
    
    /**
     * Busca un empleado por DNI.
     *
     * @param dni DNI del empleado
     * @return Empleado encontrado, o null si no existe
     * @throws Exception Si el DNI está vacío
     */
    @Override
    public Empleado buscarPorDni(String dni) throws Exception {
        if (dni == null || dni.trim().isEmpty()) {
            throw new IllegalArgumentException("El DNI no puede estar vacío");
        }
        return empleadoDAO.getByDni(dni.trim());
    }
    
    // ========================================================================
    // VALIDACIONES DE NEGOCIO
    // ========================================================================
    
    /**
     * Valida que un empleado tenga los datos obligatorios correctos.
     *
     * @param empleado Empleado a validar
     * @throws IllegalArgumentException Si alguna validación falla
     */
    private void validarEmpleado(Empleado empleado) {
        if (empleado == null) {
            throw new IllegalArgumentException("El empleado no puede ser null");
        }
        if (empleado.getNombre() == null || empleado.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacio");
        }
        if (empleado.getApellido() == null || empleado.getApellido().trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido no puede estar vacio");
        }
        if (empleado.getDni() == null || empleado.getDni().trim().isEmpty()) {
            throw new IllegalArgumentException("El DNI no puede estar vacio");
        }
        
        // Validar formato de email
        if (empleado.getEmail() != null && !empleado.getEmail().trim().isEmpty()) {
            if (!empleado.getEmail().matches("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
                throw new IllegalArgumentException("El email tiene un formato inválido");
            }
        }
        
        // Validar fecha no futura
        if (empleado.getFechaIngreso() != null && empleado.getFechaIngreso().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de ingreso no puede ser futura");
        }
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
        
        if (legajo.getFechaAlta() != null && legajo.getFechaAlta().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de alta del legajo no puede ser futura");
        }
    }
    
    /**
     * Valida que el DNI sea único en la base de datos.
     * Permite el mismo DNI al actualizar el mismo empleado.
     *
     * @param dni DNI a validar
     * @param empleadoId ID del empleado (null para INSERT)
     * @throws Exception Si el DNI ya existe
     */
    private void validarDniUnico(String dni, Long empleadoId) throws Exception {
        Empleado existente = empleadoDAO.getByDni(dni.trim());
        if (existente != null) {
            if (empleadoId == null || !existente.getId().equals(empleadoId)) {
                throw new IllegalArgumentException("Ya existe un empleado con el DNI: " + dni);
            }
        }
    }
}
