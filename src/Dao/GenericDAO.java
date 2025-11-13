package Dao;

import java.util.List;


//Interfaz generica que define las operaciones CRUD basicas.
public interface GenericDAO<T> {

    //Crea un nuevo registro en la BBDD.
    void crear(T entity) throws Exception;

    //Obtiene un registro por su ID.
    T leer(long id) throws Exception;

    //Obtiene todos los registros no eliminados.
    List<T> leerTodos() throws Exception;

    //Actualiza un registro existente
    void actualizar(T entity) throws Exception;

    //Realiza un borrado logico.
    void eliminar(long id) throws Exception;
}
