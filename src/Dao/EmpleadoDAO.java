package Dao;

import Entities.Empleado;



// Interfaz específica para operaciones de Empleado.
// Hereda los métodos básicos y agrega la búsqueda por DNI.
public interface EmpleadoDAO extends GenericDAO<Empleado> {
    
    // obtiene un Empleado por su DNI
    Empleado getByDni(String dni) throws Exception;
}