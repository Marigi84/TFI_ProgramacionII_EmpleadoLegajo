package Dao;

import Entities.Legajo;
import java.sql.Connection;

// Interfaz espec�fica para operaciones de Legajo.
// Hereda los m�todos CRUD est�ndar de GenericDAO (aut�nomos).
// Agrega m�todos transaccionales con Connection externa.
// 
public interface LegajoDAO extends GenericDAO<Legajo> {

    // --- M�todos transaccionales ---
// M�todo espec�fico para crear un Legajo asociado a un Empleado.
// Este es el m�todo que DEBE usar el Service, ya que recibe
// la 'Connection' de la transacci�n y el 'empleadoId' (la FK).
    void crearLegajo(Legajo legajo, Connection conn, Long empleadoId) throws Exception;

    void crear(Legajo legajo, Connection conn) throws Exception;

    //Actualiza un Legajo
    void actualizar(Legajo legajo, Connection conn) throws Exception;

    //Elimina un Legajo
    void eliminar(long id, Connection conn) throws Exception;

    
    //Recupera un Legajo 
     
    void recuperar(long id, Connection conn) throws Exception;
}
