package Service;

import Config.DatabaseConnection;
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
 * Capa intermedia entre la UI y el DAO que aplica validaciones y coordina transacciones.
 *
 * Responsabilidades:
 * - Validar que los datos del empleado sean correctos ANTES de persistir
 * - Garantizar unicidad del DNI en la base de datos
 * - Coordinar operaciones transaccionales entre Empleado y Legajo
 * - Aplicar propiedades ACID en transacciones complejas
 * - Transformar excepciones técnicas en errores de negocio comprensibles
 *
 * Patrón: Service Layer con transacciones manuales (setAutoCommit, commit, rollback)
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
     *
     * Flujo:
     * 1. Valida datos obligatorios (nombre, apellido, DNI, email, fecha)
     * 2. Verifica que el DNI no exista en la BD
     * 3. Inicia transacción y delega al DAO para insertar
     * 4. Confirma con commit o revierte con rollback si hay error
     *
     * NOTA: Para crear empleado CON legajo, usar crearEmpleadoConLegajo().
     *
     * @param empleado Empleado a insertar (id será autogenerado)
     * @throws Exception Si la validación falla, el DNI ya existe, o hay error de BD
     */
    @Override
    public void insertar(Empleado empleado) throws Exception {
        validarEmpleado(empleado);
        validarDniUnico(empleado.getDni(), null);
        
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            empleadoDAO.crear(empleado, conn);
            
            conn.commit();
        } catch (Exception e) {
            if (conn != null) conn.rollback();
            throw new Exception("Error al insertar empleado: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }
    
    /**
     * Crea un empleado con su legajo asociado en una única transacción ACID.
     *
     * Flujo transaccional:
     * 1. Valida empleado y legajo ANTES de abrir transacción
     * 2. Desactiva autocommit para control manual de la transacción
     * 3. Inserta empleado (obtiene ID autogenerado por la BD)
     * 4. Inserta legajo con FK empleado_id (garantiza relación 1:1)
     * 5. Hace commit si todo fue exitoso
     * 6. Hace rollback si ocurre cualquier error (garantiza atomicidad)
     *
     * Propiedades ACID garantizadas:
     * - Atomicidad: Ambos se crean o ninguno (rollback en error)
     * - Consistencia: FK empleado_id garantiza integridad referencial
     * - Aislamiento: setAutoCommit(false) aísla la transacción
     * - Durabilidad: commit() persiste cambios en disco
     *
     * @param empleado Empleado a crear (debe contener objeto Legajo asociado)
     * @throws Exception Si validación falla o hay error en la transacción
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
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Insertar empleado (obtiene ID autogenerado)
            empleadoDAO.crear(empleado, conn);
            
            // Insertar legajo con FK empleado_id
            if (empleado.getLegajo() != null) {
                legajoDAO.crearLegajo(empleado.getLegajo(), conn, empleado.getId());
            }
            
            conn.commit();
            
        } catch (Exception e) {
            if (conn != null) {
                conn.rollback();
            }
            throw new Exception("Error al crear empleado con legajo: " + e.getMessage(), e);
            
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }
    
    /**
     * Actualiza los datos de un empleado existente.
     *
     * Validaciones:
     * - El empleado debe tener ID > 0 (debe estar persistido)
     * - Datos obligatorios completos (nombre, apellido, DNI, email, fecha)
     * - El DNI debe ser único (permite el mismo DNI si es del mismo empleado)
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
        
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            empleadoDAO.actualizar(empleado, conn);
            
            conn.commit();
        } catch (Exception e) {
            if (conn != null) conn.rollback();
            throw new Exception("Error al actualizar empleado: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }
    
    /**
     * Elimina lógicamente un empleado (soft delete).
     * Marca el empleado como eliminado sin borrarlo físicamente de la BD.
     *
     * @param id ID del empleado a eliminar
     * @throws Exception Si id <= 0 o no existe el empleado
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
            
            empleadoDAO.eliminar(id, conn);
            
            conn.commit();
        } catch (Exception e) {
            if (conn != null) conn.rollback();
            throw new Exception("Error al eliminar empleado: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }
    
    /**
     * Obtiene un empleado por su ID.
     *
     * @param id ID del empleado a buscar
     * @return Empleado encontrado, o null si no existe o está eliminado
     * @throws Exception Si id <= 0 o hay error de BD
     */
    @Override
    public Empleado getById(Long id) throws Exception {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor a 0");
        }
        return empleadoDAO.leer(id);
    }
    
    /**
     * Obtiene todos los empleados activos (no eliminados).
     * Incluye legajo asociado mediante LEFT JOIN.
     *
     * @return Lista de empleados activos (puede estar vacía)
     * @throws Exception Si hay error de BD
     */
    @Override
    public List<Empleado> getAll() throws Exception {
        return empleadoDAO.leerTodos();
    }
    
    /**
     * Busca un empleado por su número de DNI.
     *
     * @param dni DNI del empleado a buscar
     * @return Empleado encontrado, o null si no existe
     * @throws Exception Si el DNI está vacío o hay error de BD
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
     * Valida que un empleado tenga todos los datos obligatorios y correctos.
     *
     * Reglas de negocio aplicadas:
     * - Nombre, apellido y DNI son obligatorios (no null, no vacíos)
     * - Email debe tener formato válido (regex: usuario@dominio.ext)
     * - Fecha de ingreso no puede ser futura
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
     * Valida que un legajo tenga todos los datos obligatorios.
     *
     * Reglas de negocio aplicadas:
     * - Número de legajo es obligatorio (no null, no vacío)
     * - Estado es obligatorio (debe ser ACTIVO o INACTIVO)
     * - Fecha de alta no puede ser futura (si está presente)
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
     *
     * Lógica diferenciada por operación:
     * - INSERT (empleadoId = null): Rechaza si el DNI ya existe
     * - UPDATE (empleadoId != null): Permite si el DNI pertenece al mismo empleado
     *
     * Esta validación garantiza integridad de datos a nivel de negocio.
     *
     * @param dni DNI a validar
     * @param empleadoId ID del empleado (null para INSERT, valor para UPDATE)
     * @throws Exception Si el DNI ya existe y pertenece a otro empleado
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
