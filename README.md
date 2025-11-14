# 💻 Trabajo Final Integrador – Programación II  
## Aplicación Java con relación **1 → 1 unidireccional** (Empleado → Legajo)  
**UTN – Tecnicatura Universitaria en Programación a Distancia – Comisión 07**

---

## 🧩 Descripción del dominio

El dominio elegido es **Empleado → Legajo**, una relación **1 a 1 unidireccional**, donde:

- Cada **Empleado** tiene **un único Legajo** asociado.  
- El **Legajo** contiene información administrativa (categoría, estado, fecha de alta, observaciones).  
- La relación se representa mediante la clave foránea `empleado_id` en la tabla `legajos`.  
- El **Empleado** conoce a su Legajo, pero no al revés (unidireccional).

Objetivo del Proyecto
Implementar una aplicación Java basada en una arquitectura por capas que permita gestionar Empleados y Legajos con una relación **1:1 unidireccional** (Empleado → Legajo).  
El proyecto debe cumplir las consignas del TFI:

- Separación en **Capa Entidades**, **DAO**, **Service** y **UI/AppMenu**.  
- Operaciones **CRUD completas**.  
- Manejo de **transacciones JDBC** (commit/rollback).  
- Uso obligatorio de **PreparedStatement**.  
- Persistencia en **MySQL**.  


Se incluyen:
- **Bajas lógicas** (`eliminado = TRUE`).  
- **Transacciones ACID (commit / rollback)** para mantener la integridad entre ambas tablas.  
- **Validaciones de negocio** antes de persistir o actualizar datos.  
- **Arquitectura por capas:** `config`, `entities`, `dao`, `service`, `main`.

---

## ⚙️ Requisitos técnicos

- **Lenguaje:** Java JDK 21  
- **Base de datos:** MySQL Server 8.4.7 LTS  
- **Conector JDBC:** MySQL Connector/J 8.4.0  
- **IDE recomendado:** NetBeans 21 o IntelliJ IDEA  
- **Sistema operativo compatible:** Windows, Linux o macOS  

---

## 🗄️ Creación de la base de datos

Dentro de la carpeta `/sql/` se incluyen **dos scripts SQL**:

| Archivo | Descripción |
|----------|--------------|
| `sql01_creacion_bd.sql` | Crea la base de datos `empresa`, las tablas `empleados` y `legajos`, y define claves primarias, foráneas e índices. |
| `sql02_datos_prueba.sql` | Inserta registros iniciales de prueba (empleados con legajos asociados). |

### 📋 Pasos para crear la base desde MySQL Workbench o consola:

1. Abrir el cliente MySQL (Workbench o terminal).  
2. Ejecutar los scripts en orden:
   ```sql
   SOURCE sql/sql01_creacion_bd.sql;
   SOURCE sql/sql02_datos_prueba.sql;
   ```
3. Verificar las tablas creadas:
   ```sql
   USE empresa;
   SHOW TABLES;
   SELECT * FROM empleados;
   SELECT * FROM legajos;
   ```

#### 🧱 Estructura básica de tablas

**empleados**
| Campo | Tipo | Restricciones |
|--------|------|----------------|
| id | BIGINT | PK, AUTO_INCREMENT |
| eliminado | BOOLEAN | DEFAULT FALSE |
| nombre | VARCHAR(80) | NOT NULL |
| apellido | VARCHAR(80) | NOT NULL |
| dni | VARCHAR(15) | UNIQUE |
| email | VARCHAR(120) | CHECK (email LIKE '%@%.%') |
| fecha_ingreso | DATE | |
| area | VARCHAR(50) | |

**legajos**
| Campo | Tipo | Restricciones |
|--------|------|----------------|
| id | BIGINT | PK, AUTO_INCREMENT |
| eliminado | BOOLEAN | DEFAULT FALSE |
| nro_legajo | VARCHAR(20) | UNIQUE, NOT NULL |
| categoria | VARCHAR(30) | |
| estado | ENUM('ACTIVO','INACTIVO') | NOT NULL |
| fecha_alta | DATE | |
| observaciones | VARCHAR(255) | |
| empleado_id | BIGINT | FK REFERENCES empleados(id) UNIQUE ON DELETE CASCADE |

---

## 🧱 Estructura del proyecto

```
TFI_ProgramacionII_EmpleadoLegajo/
│
├── src/
│   ├── Config/
│   │    ├── DatabaseConnection.java
│   │    └── TransactionManager.java
│   │
│   ├── Entities/
│   │    ├── BaseEntity.java
│   │    ├── Empleado.java
│   │    ├── Legajo.java
│   │    └── Estado.java
│   │
│   ├── Dao/
│   │    ├── GenericDAO.java
│   │    ├── EmpleadoDAO.java
│   │    ├── EmpleadoDAOImpl.java
│   │    ├── LegajoDAO.java
│   │    └── LegajoDAOImpl.java
│   │
│   ├── Service/
│   │    ├── EmpleadoService.java
│   │    ├── EmpleadoServiceImpl.java
│   │    ├── LegajoService.java
│   │    └── LegajoServiceImpl.java
│   │
│   ├── Main/
│   │    └── AppMenu.java
│   │
│   └── Test/
│        ├── TestConexion.java
│        └── TestDAO.java
│
└── sql/
    ├── sql01_creacion_bd.sql
    └── sql02_datos_prueba.sql
```

---

## 🧰 Configuración de conexión

El archivo `DatabaseConnection.java` lee los parámetros desde un archivo de propiedades externo `database.properties`:

```properties
url=jdbc:mysql://localhost:3306/empresa
user=root
password=admin
```

> ⚠️ Si tus credenciales son diferentes, actualizalas antes de ejecutar el proyecto.

---

## 🚀 Cómo compilar y ejecutar

### 🪶 Paso 1: Configurar el conector JDBC
Agregar el archivo **mysql-connector-j-8.4.0.jar** a las *Libraries* del proyecto (clic derecho → Properties → Libraries → Add JAR/Folder).

### 🪶 Paso 2: Compilar
En NetBeans:  
**Run → Clean and Build Project**

### 🪶 Paso 3: Ejecutar
Ejecutar la clase principal `AppMenu.java` desde:
```
src/main/AppMenu.java
```

También se puede probar la conexión con:
```
src/test/TestConexion.java
```

### 🧩 Credenciales de prueba (datos del script)
| ID | Nombre | Apellido | DNI | Email | Área | Nro Legajo | Estado |
|----|---------|-----------|-----|--------|------|-------------|--------|
| 1 | Juan | Pérez | 12345678 | juan.perez@empresa.com | RRHH | LEG-001 | ACTIVO |
| 2 | María | Gómez | 87654321 | maria.gomez@empresa.com | IT | LEG-002 | ACTIVO |

---

## 🧭 Flujo de uso de la aplicación

1. Al iniciar `AppMenu`, el usuario ve un menú de consola con las siguientes opciones:
   - Crear empleado con legajo asociado  
   - Listar empleados  
   - Buscar empleado por DNI  
   - Actualizar datos de empleado  
   - Eliminar (baja lógica) empleado  
   - Salir

2. Las operaciones de escritura (`crear`, `actualizar`, `eliminar`) usan transacciones con **commit/rollback** para garantizar integridad.

3. Las validaciones evitan:
   - DNIs duplicados  
   - Fechas futuras  
   - Campos obligatorios vacíos  
   - Emails inválidos  

4. Los datos quedan persistidos en la base `empresa`.

---

## 🧠 Validaciones de negocio

| Validación | Regla aplicada |
|-------------|----------------|
| DNI único | No se permite duplicar empleados con el mismo DNI. |
| Email válido | Debe contener el formato `usuario@dominio.ext`. |
| Fechas válidas | `fechaIngreso` y `fechaAlta` no pueden ser futuras. |
| Relación 1→1 | No puede existir un Legajo sin un Empleado asociado. |
| Baja lógica | `eliminado = TRUE` mantiene el registro en BD pero lo excluye de consultas. |

---

# 🧩 Diagramas UML del Proyecto

El proyecto incluye un **documento PDF único** que reúne todos los diagramas UML del sistema.  
Este archivo facilita la lectura y permite visualizar la arquitectura completa de manera unificada.

El PDF contiene:

---

## 1️⃣ UML del Modelo de Dominio (Entities)

- Relación 1 → 1 unidireccional entre `Empleado` y `Legajo`
- Herencia de `BaseEntity`
- Uso del `enum Estado`
- Atributos principales del modelo

---

## 2️⃣ UML de la Capa Service

Incluye:

- `GenericService<T>`
- Interfaces: `EmpleadoService`, `LegajoService`
- Implementaciones: `EmpleadoServiceImpl`, `LegajoServiceImpl`
- Dependencias hacia los DAO
- Métodos especiales como `buscarPorDni` y `crearEmpleadoConLegajo`

---

## 3️⃣ UML de la Capa DAO

Incluye:

- `GenericDAO<T>`
- Interfaces DAO específicas
- Implementaciones JDBC (`EmpleadoDAOImpl`, `LegajoDAOImpl`)
- Operaciones CRUD

---

## 4️⃣ UML de Arquitectura en Capas

Representa:

- Dependencias UI → Service → DAO → Entities
- Service → Config (manejo de conexión y transacciones)
- Vista general del sistema

---

📘 **Enlace al PDF con todos los UML:**  
`[🔗 Descargar PDF UML completo](https://github.com/Marigi84/TFI_ProgramacionII_EmpleadoLegajo/blob/main/Diagramas_UML_Empleado_Legajo_ArquitecturaCompleta.pdf)`

---

## 🖼 Representación visual simplificada del dominio

```
┌──────────────────┐        1     1        ┌──────────────────┐
│     Empleado     │---------------------->│      Legajo      │
├──────────────────┤                       ├──────────────────┤
│ - nombre : Str   │                       │ - nroLegajo : Str│
│ - apellido : Str │                       │ - categoria : Str│
│ - dni : Str      │                       │ - estado : Enum  │
│ - email : Str    │                       │ - fechaAlta : Date│
│ - area : Str     │                       │ - observaciones  │
└──────────────────┘                       └──────────────────┘
         ▲
         │
┌──────────────────┐
│   BaseEntity     │
│------------------│
│ +id : Long       │
│ +eliminado : Bool│
└──────────────────┘
```

---

## ✔ Comentario Final

Este documento UML completo permite comprender:

- La estructura del dominio  
- La lógica de negocio en la capa Service  
- La persistencia mediante DAO  
- La arquitectura por capas del sistema  

Una visión clara, profesional y lista para presentación en el TFI.


---

## 🎥 Enlace al video demostrativo

> 🎬 **Video de presentación del TFI:**  
> [👉 Agregar aquí el enlace a YouTube o Google Drive]

---

## 👥 Equipo de desarrollo

| Integrante | Rol / Aporte |
|-------------|---------------|
| **Marina Giselle Cordero** | UML, Config, Entities, SQL, Test, Documentación |
| **Silvia Giardini** | DAO (persistencia y CRUD con JDBC, manejo de excepciones) |
| **Alex Dauria** | Service (transacciones, validaciones y reglas de negocio) |
| **Matías Perdigués** | Main (AppMenu, pruebas de flujo y consola interactiva) |

---

## 🧾 Estado del proyecto

| Capa | Estado | Responsable |
|------|---------|-------------|
| Config / Entities | ✅ Completa | Marina Cordero |
| DAO | ✅ Completa | Silvia Giardini |
| Service | ✅ Completa | Alex Dauria |
| Main | 🔜 En desarrollo | Matías Perdigués |

📘 **Versión actual:** 1.3  
📅 **Última actualización:** 13/11/2025  

---

## 🏁 Conclusión

Este trabajo implementa un sistema completo de persistencia con Java y MySQL, aplicando:
- Patrón **DAO + Service**  
- **Transacciones ACID**  
- **Validaciones de negocio**  
- **Manejo de excepciones y conexión controlada**  
- Arquitectura por capas y código reutilizable  

El proyecto demuestra dominio de los conceptos de **Programación II** y su integración con bases de datos relacionales.

---
