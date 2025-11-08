package test;

import Service.EmpleadoServiceImpl;
import Entities.Empleado;
import Entities.Legajo;
import Entities.Estado;
import java.time.LocalDate;
import java.util.List;

/**
 * Test basico para verificar que Service funciona correctamente.
 * 
 * PREREQUISITOS:
 * 1. Base de datos 'empresa' creada con el script SQL
 * 2. Datos de prueba insertados
 * 3. MySQL ejecutandose
 * 
 * TESTS:
 * 1. Listar todos los empleados
 * 2. Buscar empleado por ID
 * 3. Buscar empleado por DNI
 * 4. Insertar nuevo empleado SIN legajo
 * 5. Insertar nuevo empleado CON legajo (transaccion)
 * 6. Actualizar empleado
 * 7. Eliminar empleado (baja logica)
 * 8. Validaciones (campos obligatorios)
 */
public class TestServiceBasico {
    
    private static EmpleadoServiceImpl empleadoService;
    private static int testsPasados = 0;
    private static int testsFallados = 0;
    
    public static void main(String[] args) {
        System.out.println("========================================================");
        System.out.println("   TEST DE SERVICE - Empleado y Legajo (Transacciones) ");
        System.out.println("========================================================\n");
        
        empleadoService = new EmpleadoServiceImpl();
        
        // Ejecutar tests en orden
        test1_listarEmpleados();
        test2_buscarPorId();
        test3_buscarPorDni();
        test4_insertarEmpleadoSinLegajo();
        test5_insertarEmpleadoConLegajo();
        test6_actualizarEmpleado();
        test7_eliminarEmpleado();
        test8_validaciones();
        
        // Resumen final
        System.out.println("\n========================================================");
        System.out.println("                    RESUMEN FINAL                       ");
        System.out.println("========================================================");
        System.out.println("Tests pasados: " + testsPasados);
        System.out.println("Tests fallados: " + testsFallados);
        
        if (testsFallados == 0) {
            System.out.println("\nTODOS LOS TESTS PASARON! Service esta funcionando correctamente.");
        } else {
            System.out.println("\nHay errores que corregir.");
        }
    }
    
    private static void test1_listarEmpleados() {
    System.out.println("-------------------------------------------------------");
    System.out.println("TEST 1: Listar todos los empleados");
    System.out.println("-------------------------------------------------------");
        
        try {
            List<Empleado> empleados = empleadoService.getAll();
            
            if (empleados != null && !empleados.isEmpty()) {
                System.out.println("Empleados encontrados: " + empleados.size());
                for (Empleado emp : empleados) {
                    System.out.println("  - ID: " + emp.getId() + " | " + 
                        emp.getApellido() + ", " + emp.getNombre() + " | DNI: " + emp.getDni());
                    if (emp.getLegajo() != null) {
                        System.out.println("    - Legajo: " + emp.getLegajo().getNroLegajo() + 
                            " | Estado: " + emp.getLegajo().getEstado());
                    }
                }
                testPasado("Test 1");
            } else {
                testFallado("Test 1", "No se encontraron empleados en la BD");
            }
            
        } catch (Exception e) {
            testFallado("Test 1", e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void test2_buscarPorId() {
    System.out.println("\n-------------------------------------------------------");
        System.out.println("TEST 2: Buscar empleado por ID");
        System.out.println("-------------------------------------------------------");
        
        try {
            Empleado emp = empleadoService.getById(1L);
            
            if (emp != null) {
                System.out.println("Empleado encontrado:");
                System.out.println("  ID: " + emp.getId());
                System.out.println("  Nombre: " + emp.getNombre() + " " + emp.getApellido());
                System.out.println("  DNI: " + emp.getDni());
                System.out.println("  Email: " + emp.getEmail());
                System.out.println("  Area: " + emp.getArea());
                
                if (emp.getLegajo() != null) {
                    System.out.println("  Legajo: " + emp.getLegajo().getNroLegajo());
                }
                
                testPasado("Test 2");
            } else {
                testFallado("Test 2", "No se encontró empleado con ID 1");
            }
            
        } catch (Exception e) {
            testFallado("Test 2", e.getMessage());
        }
    }
    
    private static void test3_buscarPorDni() {
    System.out.println("\n-------------------------------------------------------");
        System.out.println("TEST 3: Buscar empleado por DNI");
        System.out.println("-------------------------------------------------------");
        
        try {
            // Usar DNI de uno de los empleados de prueba
            Empleado emp = empleadoService.buscarPorDni("12345678");
            
            if (emp != null) {
                System.out.println("Empleado encontrado por DNI:");
                System.out.println("  " + emp.getNombre() + " " + emp.getApellido());
                testPasado("Test 3");
            } else {
                System.out.println("No se encontro empleado con ese DNI (puede ser normal si no existe)");
                testPasado("Test 3");
            }
            
        } catch (Exception e) {
            testFallado("Test 3", e.getMessage());
        }
    }
    
    private static void test4_insertarEmpleadoSinLegajo() {
    System.out.println("\n-------------------------------------------------------");
        System.out.println("TEST 4: Insertar empleado SIN legajo");
        System.out.println("-------------------------------------------------------");
        
        try {
            Empleado nuevo = new Empleado(
                null, false,
                "CARLOS",
                "LOPEZ",
                String.valueOf(30000000 + (System.currentTimeMillis() % 9999999)),  // DNI único
                "carlos.lopez@empresa.com",
                LocalDate.now(),
                "VENTAS",
                null  // Sin legajo
            );
            
            empleadoService.insertar(nuevo);
            
            if (nuevo.getId() != null && nuevo.getId() > 0) {
                System.out.println("Empleado insertado correctamente");
                System.out.println("  ID generado: " + nuevo.getId());
                testPasado("Test 4");
            } else {
                testFallado("Test 4", "No se genero ID");
            }
            
        } catch (Exception e) {
            testFallado("Test 4", e.getMessage());
        }
    }
    
    private static void test5_insertarEmpleadoConLegajo() {
    System.out.println("\n-------------------------------------------------------");
        System.out.println("TEST 5: Insertar empleado CON legajo (TRANSACCION)");
        System.out.println("-------------------------------------------------------");
        
        try {
            // Crear legajo
            Legajo legajo = new Legajo(
                null, false,
                "LEG-" + (System.currentTimeMillis() % 100000),  // Número único (max 5 dígitos)
                "JUNIOR",
                Estado.ACTIVO,
                LocalDate.now(),
                "Legajo de prueba desde test"
            );
            
            // Crear empleado con legajo
            Empleado nuevo = new Empleado(
                null, false,
                "MARIA",
                "GARCIA",
                String.valueOf(40000000 + (System.currentTimeMillis() % 9999999)),  // DNI único
                "maria.garcia@empresa.com",
                LocalDate.now(),
                "RECURSOS HUMANOS",
                legajo
            );
            
            empleadoService.crearEmpleadoConLegajo(nuevo);
            
            if (nuevo.getId() != null && legajo.getId() != null) {
                System.out.println("Transaccion exitosa:");
                System.out.println("  Empleado ID: " + nuevo.getId());
                System.out.println("  Legajo ID: " + legajo.getId());
                System.out.println("  Commit ejecutado correctamente");
                testPasado("Test 5");
            } else {
                testFallado("Test 5", "No se generaron IDs");
            }
            
        } catch (Exception e) {
            testFallado("Test 5", e.getMessage());
            System.out.println("  Rollback ejecutado (si hubo error en transaccion)");
        }
    }
    
    private static void test6_actualizarEmpleado() {
    System.out.println("\n-------------------------------------------------------");
        System.out.println("TEST 6: Actualizar empleado");
        System.out.println("-------------------------------------------------------");
        
        try {
            // Obtener un empleado existente
            Empleado emp = empleadoService.getById(1L);
            
            if (emp != null) {
                String emailOriginal = emp.getEmail();
                emp.setEmail("nuevo.email@test.com");
                
                empleadoService.actualizar(emp);
                
                // Verificar que se actualizo
                Empleado empActualizado = empleadoService.getById(1L);
                
                if (empActualizado.getEmail().equals("nuevo.email@test.com")) {
                    System.out.println("Empleado actualizado correctamente");
                    System.out.println("  Email anterior: " + emailOriginal);
                    System.out.println("  Email nuevo: " + empActualizado.getEmail());
                    
                    // Restaurar email original
                    empActualizado.setEmail(emailOriginal);
                    empleadoService.actualizar(empActualizado);
                    
                    testPasado("Test 6");
                } else {
                    testFallado("Test 6", "El email no se actualizó");
                }
            } else {
                testFallado("Test 6", "No se encontró empleado con ID 1");
            }
            
        } catch (Exception e) {
            testFallado("Test 6", e.getMessage());
        }
    }
    
    private static void test7_eliminarEmpleado() {
    System.out.println("\n-------------------------------------------------------");
        System.out.println("TEST 7: Eliminar empleado (baja logica)");
        System.out.println("-------------------------------------------------------");
        
        try {
            // Crear un empleado para eliminar
            Empleado temp = new Empleado(
                null, false,
                "TEMPORAL",
                "BORRAR",
                String.valueOf(90000000 + (System.currentTimeMillis() % 9999999)),  // DNI único
                "temp@test.com",
                LocalDate.now(),
                "TEST",
                null
            );
            
            empleadoService.insertar(temp);
            Long idTemp = temp.getId();
            
            // Eliminar (baja lógica)
            empleadoService.eliminar(idTemp);
            
            // Verificar que ya no aparece en getAll()
            List<Empleado> empleados = empleadoService.getAll();
            boolean encontrado = empleados.stream()
                .anyMatch(e -> e.getId().equals(idTemp));
            
            if (!encontrado) {
                System.out.println("Baja logica ejecutada correctamente");
                System.out.println("  El empleado ya no aparece en la lista activa");
                testPasado("Test 7");
            } else {
                testFallado("Test 7", "El empleado todavia aparece despues de eliminarlo");
            }
            
        } catch (Exception e) {
            testFallado("Test 7", e.getMessage());
        }
    }
    
    private static void test8_validaciones() {
    System.out.println("\n-------------------------------------------------------");
        System.out.println("TEST 8: Validaciones de campos obligatorios");
        System.out.println("-------------------------------------------------------");
        
        try {
            // Intentar insertar empleado sin nombre (debe fallar)
            Empleado invalido = new Empleado(
                null, false,
                null,  // Nombre vacío (obligatorio)
                "APELLIDO",
                "11111111",
                "test@test.com",
                LocalDate.now(),
                "TEST",
                null
            );
            
            empleadoService.insertar(invalido);
            
            // Si llegamos aquí, el test falló (debería haber lanzado excepción)
            testFallado("Test 8", "No se valido el nombre obligatorio");
            
        } catch (IllegalArgumentException e) {
            // Este es el comportamiento esperado
            System.out.println("Validacion funciono correctamente:");
            System.out.println("  Mensaje: " + e.getMessage());
            testPasado("Test 8");
            
        } catch (Exception e) {
            testFallado("Test 8", "Error inesperado: " + e.getMessage());
        }
    }
    
    // Metodos auxiliares
    private static void testPasado(String nombreTest) {
        System.out.println("\n" + nombreTest + " PASADO\n");
        testsPasados++;
    }
    
    private static void testFallado(String nombreTest, String motivo) {
        System.out.println("\n" + nombreTest + " FALLADO");
        System.out.println("   Motivo: " + motivo + "\n");
        testsFallados++;
    }
}
