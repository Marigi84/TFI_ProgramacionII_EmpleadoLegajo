-- Creación de la base de datos
DROP DATABASE IF EXISTS empresa;
CREATE DATABASE empresa;
USE empresa;

-- Eliminación preventiva de tablas (para idempotencia)
DROP TABLE IF EXISTS legajos;
DROP TABLE IF EXISTS empleados;


CREATE TABLE empleados (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    eliminado BOOLEAN NOT NULL DEFAULT FALSE,
    nombre VARCHAR(80) NOT NULL,
    apellido VARCHAR(80) NOT NULL,
    dni VARCHAR(15) NOT NULL UNIQUE,
    email VARCHAR(120) CHECK (email LIKE '%@%.%'),
    fecha_ingreso DATE,
    area VARCHAR(50)
);

CREATE TABLE legajos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    eliminado BOOLEAN NOT NULL DEFAULT FALSE,
    nro_legajo VARCHAR(20) NOT NULL UNIQUE,
    categoria VARCHAR(30),
    estado ENUM('ACTIVO', 'INACTIVO') NOT NULL,
    fecha_alta DATE,
    observaciones VARCHAR(255),
    empleado_id BIGINT NOT NULL UNIQUE,
    CONSTRAINT fk_empleado FOREIGN KEY (empleado_id) REFERENCES empleados(id) ON DELETE CASCADE ON UPDATE CASCADE
);


-- Inserción válida 1: 
INSERT INTO empleados (eliminado, nombre, apellido, dni, email, fecha_ingreso, area)
VALUES (FALSE, 'Juan', 'Perez', '12345678', 'juan.perez@empresa.com', '2023-01-15', 'RRHH');

INSERT INTO legajos (eliminado, nro_legajo, categoria, estado, fecha_alta, observaciones, empleado_id)
VALUES (FALSE, 'LEG-001', 'Senior', 'ACTIVO', '2023-01-30', 'Legajo activo', 1);


-- Inserción válida 2: 
INSERT INTO empleados (eliminado, nombre, apellido, dni, email, fecha_ingreso, area)
VALUES (FALSE, 'Maria', 'Gomez', '87654321', 'maria.gomez@empresa.com', '2024-06-20', 'IT');


INSERT INTO legajos (eliminado, nro_legajo, categoria, estado, fecha_alta, observaciones, empleado_id)
VALUES (FALSE, 'LEG-002', 'Junior', 'ACTIVO', '2024-07-01', 'Legajo Incompleto',2);

-- ==========================================================
-- Fin del script de creación y carga inicial de datos
-- (Crea la base 'empresa', tablas, y registros de prueba)
-- ==========================================================
