package Dao;

import java.sql.Connection;
import java.util.List;

// Interfaz genérica que define las operaciones CRUD básicas para cualquier entidad.
// Se utilizan los nombres que se pidieron (crear, leer, etc. pedidos en el tp)
// Los métodos transaccionales (Crear-actualizar-eliminar) aceptan una Connection externa.

public interface GenericDAO<T> {

    // Crea una nueva registro en la base de datos (lanza excepción si falla)
    void crear(T entity, Connection conn) throws Exception;

    // Obtiene una registro por su ID (maneja su propia conexión)
    T leer(long id) throws Exception;

    // Obtiene todas los registros no eliminadas (maneja su propia conexión)
    List<T> leerTodos() throws Exception;

    // Actualiza un registro existente (lanza excepción si falla)
    void actualizar(T entity, Connection conn) throws Exception;

    // Realiza un borrado lógico (lanza excepción si falla)
    void eliminar(long id, Connection conn) throws Exception;
}
