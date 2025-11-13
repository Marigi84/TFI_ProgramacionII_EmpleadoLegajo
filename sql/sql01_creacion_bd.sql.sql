-- ==========================================================
-- Archivo: sql01_creacion_bd.sql
-- Descripción: Crea la base de datos 'empresa', las tablas
-- empleados y legajos (relación 1→1 unidireccional),
-- con claves, restricciones e índices.
-- ==========================================================

DROP DATABASE IF EXISTS empresa;
CREATE DATABASE empresa;
USE empresa;

-- ==========================================================
-- TABLA: EMPLEADOS
-- ==========================================================
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

-- ==========================================================
-- TABLA: LEGAJOS
-- ==========================================================
DROP TABLE IF EXISTS legajos;

CREATE TABLE legajos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    eliminado BOOLEAN NOT NULL DEFAULT FALSE,
    nro_legajo VARCHAR(20) NOT NULL UNIQUE,
    categoria VARCHAR(30),
    estado ENUM('ACTIVO', 'INACTIVO') NOT NULL,
    fecha_alta DATE,
    observaciones VARCHAR(255),
    empleado_id BIGINT NOT NULL UNIQUE,
    CONSTRAINT fk_empleado FOREIGN KEY (empleado_id)
        REFERENCES empleados(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);