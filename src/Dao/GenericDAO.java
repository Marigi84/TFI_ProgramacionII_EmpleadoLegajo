package Dao;

import java.util.List;


//Interfaz gen�rica que define las operaciones CRUD b�sicas.
public interface GenericDAO<T> {

    //Crea un nuevo registro en la BBDD (modo aut�nomo).
    void crear(T entity) throws Exception;

    //Obtiene un registro por su ID (modo aut�nomo).
    T leer(long id) throws Exception;

    //Obtiene todos los registros no eliminados (modo aut�nomo).
    List<T> leerTodos() throws Exception;

    //Actualiza un registro existente
    void actualizar(T entity) throws Exception;

    //Realiza un borrado l�gico (modo aut�nomo).
    void eliminar(long id) throws Exception;
}
