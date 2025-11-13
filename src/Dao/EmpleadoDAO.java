package Dao;

import Entities.Empleado;
import java.sql.Connection;

//Interfaz espec�fica para operaciones de Empleado.
//Hereda los m�todos CRUD est�ndar de GenericDAO (aut�nomos).
//Agrega m�todos transaccionales con Connection externa
//para permitir commit y rollback desde la capa Service.
public interface EmpleadoDAO extends GenericDAO<Empleado> {

    // --- B�squeda espec�fica ---
    // Busca un Empleado por su DNI
    Empleado getByDni(String dni) throws Exception;

    // --- M�todos transaccionales ---
    void crear(Empleado empleado, Connection conn) throws Exception;

    void actualizar(Empleado empleado, Connection conn) throws Exception;

    void eliminar(long id, Connection conn) throws Exception;

    void recuperar(long id, Connection conn) throws Exception;
}
