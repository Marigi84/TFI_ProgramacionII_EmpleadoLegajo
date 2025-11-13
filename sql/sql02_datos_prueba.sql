-- ==========================================================
-- Archivo: sql02_datos_prueba.sql
-- Descripción: Inserta registros de ejemplo para Empleados
-- y Legajos. Permite probar la aplicación Java (DAO + JDBC)
-- desde cero.
-- ==========================================================

USE empresa;

-- ==========================================================
-- EMPLEADO 1 + LEGAJO 1
-- ==========================================================
INSERT INTO empleados (eliminado, nombre, apellido, dni, email, fecha_ingreso, area)
VALUES (FALSE, 'Juan', 'Perez', '12345678', 'juan.perez@empresa.com', '2023-01-15', 'RRHH');

INSERT INTO legajos (eliminado, nro_legajo, categoria, estado, fecha_alta, observaciones, empleado_id)
VALUES (FALSE, 'LEG-001', 'Senior', 'ACTIVO', '2023-01-30', 'Legajo activo', 1);

-- ==========================================================
-- EMPLEADO 2 + LEGAJO 2
-- ==========================================================
INSERT INTO empleados (eliminado, nombre, apellido, dni, email, fecha_ingreso, area)
VALUES (FALSE, 'Maria', 'Gomez', '87654321', 'maria.gomez@empresa.com', '2024-06-20', 'IT');

INSERT INTO legajos (eliminado, nro_legajo, categoria, estado, fecha_alta, observaciones, empleado_id)
VALUES (FALSE, 'LEG-002', 'Junior', 'ACTIVO', '2024-07-01', 'Legajo incompleto', 2);

-- ==========================================================
-- Fin del archivo de datos de prueba
-- ==========================================================