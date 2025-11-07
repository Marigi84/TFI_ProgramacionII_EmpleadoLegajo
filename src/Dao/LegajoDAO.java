package Dao;

import Entities.Legajo;

import java.sql.Connection;

// Interfaz específica para operaciones de Legajo.
// Hereda los métodos básicos y agrega el "crearLegajo" con empleadoId.
//para guardar un Legajo en la base de datos, necesitas obligatoriamente el empleado_id para la clave foránea (FK).
public interface LegajoDAO extends GenericDAO<Legajo> {
    
    // Método específico para crear Legajo asociado a un Empleado (relación 1:1)
    void crearLegajo(Legajo legajo, Connection conn, Long empleadoId) throws Exception;
}