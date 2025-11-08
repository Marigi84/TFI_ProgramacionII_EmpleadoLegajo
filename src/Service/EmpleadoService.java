package Service;

import Entities.Empleado;

/**
 * Servicio de negocio para Empleado con validaciones y transacciones.
 * Valida DNI único, campos obligatorios, email y fecha de ingreso.
 */
public interface EmpleadoService extends GenericService<Empleado> {

    // Busca empleado por DNI.
    Empleado buscarPorDni(String dni) throws Exception;
    
    // Crea empleado con legajo en transacción ACID (ambos o ninguno).
    void crearEmpleadoConLegajo(Empleado empleado) throws Exception;
}
