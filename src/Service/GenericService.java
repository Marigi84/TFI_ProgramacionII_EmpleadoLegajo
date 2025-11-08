package Service;

import java.util.List;

/**
 * Interfaz genérica que define operaciones CRUD estándar para servicios de negocio.
 * Permite reutilización, polimorfismo y consistencia en toda la aplicación.
 *
 * @param <T> Tipo de entidad (Empleado, Legajo, etc.)
 */
public interface GenericService<T> {
    // Inserta una nueva entidad con validaciones de negocio.
    void insertar(T entidad) throws Exception;

    // Actualiza una entidad existente con validaciones de negocio.
    void actualizar(T entidad) throws Exception;

    // Elimina lógicamente una entidad (soft delete).
    void eliminar(Long id) throws Exception;

    // Obtiene una entidad por su ID.
    T getById(Long id) throws Exception;

    // Obtiene todas las entidades activas (no eliminadas).
    List<T> getAll() throws Exception;
}
