package Dao;

import Entities.Empleado;
import java.sql.Connection;

//Interfaz especifica para operaciones de Empleado.
//Hereda los metodos CRUD estandar de GenericDAO (autonomos).
//Agrega metodos transaccionales con Connection externa
//para permitir commit y rollback desde la capa Service.
public interface EmpleadoDAO extends GenericDAO<Empleado> {

    // --- Busqueda especifica ---
    // Busca un Empleado por su DNI
    Empleado getByDni(String dni) throws Exception;

    // --- Metodos transaccionales ---
    void crear(Empleado empleado, Connection conn) throws Exception;

    void actualizar(Empleado empleado, Connection conn) throws Exception;

    void eliminar(long id, Connection conn) throws Exception;

    void recuperar(long id, Connection conn) throws Exception;
}
