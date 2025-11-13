package Dao;

import Entities.Legajo;
import java.sql.Connection;

// Interfaz especifica para operaciones de Legajo.
// Hereda los metodos CRUD estandar de GenericDAO.
// Agrega metodos transaccionales con Connection externa.

public interface LegajoDAO extends GenericDAO<Legajo> {

    // --- Metodos transaccionales ---
// Metodo especifico para crear un Legajo asociado a un Empleado.
// Este es el metodo que DEBE usar el Service, ya que recibe
// la 'Connection' de la transaccion y el 'empleadoId' (la FK).
    void crearLegajo(Legajo legajo, Connection conn, Long empleadoId) throws Exception;

    void crear(Legajo legajo, Connection conn) throws Exception;

    //Actualiza un Legajo
    void actualizar(Legajo legajo, Connection conn) throws Exception;

    //Elimina un Legajo
    void eliminar(long id, Connection conn) throws Exception;

    
    //Recupera un Legajo 
     
    void recuperar(long id, Connection conn) throws Exception;
}
