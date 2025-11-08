package Service;

import Entities.Legajo;

/**
 * Interfaz del servicio de negocio para la entidad Legajo.
 * Extiende GenericService heredando operaciones CRUD estándar.
 *
 * Validaciones de negocio aplicadas:
 * - Número de legajo es obligatorio
 * - Estado es obligatorio (ACTIVO o INACTIVO del enum Estado)
 * - Fecha de alta no puede ser futura (si está presente)
 *
 * RESTRICCIÓN IMPORTANTE: El método insertar() NO debe usarse directamente
 * porque un legajo no puede existir sin un empleado asociado (constraint
 * empleado_id NOT NULL en la BD).
 *
 * ALTERNATIVA CORRECTA: Use EmpleadoService.crearEmpleadoConLegajo() para
 * crear ambas entidades juntas en una transacción ACID.
 */
public interface LegajoService extends GenericService<Legajo> {
    // No se agregan métodos adicionales
    // Hereda: insertar, actualizar, eliminar, getById, getAll
}
