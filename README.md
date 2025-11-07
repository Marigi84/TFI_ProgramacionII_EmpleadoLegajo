# 💻 Trabajo Final Integrador – Programación II  
## Aplicación Java con relación **1 → 1 unidireccional** (Empleado ↔ Legajo) – MySQL + JDBC

---

### 👥 Equipo de trabajo
| Integrante | Rol |
|-------------|------|
| **Marina Cordero** | UML, Config, Entities, test, SQL |
| **Silvia Giardini** | DAO (persistencia y CRUD) |
| **Alex Dauria** | Service (transacciones) |
| **Matías Perdigués** | Main (interfaz y validaciones) |

---

### 👩‍💻 Integrante responsable capa 1
**Marina Giselle Cordero**  
Comisión 07 – Tecnicatura Universitaria en Programación a Distancia (UTN)  
**Rol:** Modelado del dominio, capa Config, capa Entities, conexión JDBC y script SQL.

---

### 🧱 Descripción general
Proyecto académico en Java que implementa una relación **1 a 1 unidireccional** entre las entidades `Empleado` y `Legajo`.  
Incluye conexión a **MySQL** mediante **JDBC**, más scripts SQL para la creación de la base y carga de datos de prueba.

---

### 🧩 Estructura del proyecto
```
TFI_ProgramacionII_EmpleadoLegajo/
│
├── src/
│   ├── Config/
│   │    └── DatabaseConnection.java
│   ├── Entities/
│   │    ├── BaseEntity.java
│   │    ├── Empleado.java
│   │    ├── Estado.java
│   │    └── Legajo.java
│   └── test/
│        ├── TestConexion.java
│        └── mainEntidades.java
│
├── .gitignore
└── README.md

---

### ⚙️ Ejecución
1. Crear la base ejecutando en MySQL Workbench:  
   ```sql
   SOURCE sql/01_creacion_y_datos_prueba.sql;
   ```
2. Abrir el proyecto en **NetBeans**.  
3. Agregar el conector **mysql-connector-j-8.4.0.jar** a *Libraries*.  
4. Ejecutar `DatabaseConnection.java`.  
   Si aparece:  
   ```
   ✅ Conexión establecida correctamente.
   ```
   la conexión fue exitosa.

---
### 🧱 Capa Entities
| Clase | Descripción |
|-------|--------------|
| **BaseEntity** | Clase abstracta que define el atributo `eliminado` para gestionar bajas lógicas. |
| **Empleado** | Representa un empleado con sus datos y referencia a su legajo. |
| **Legajo** | Contiene información administrativa y vínculo con un empleado. |
| **Estado (enum)** | Define los valores `ACTIVO` e `INACTIVO`. |

---

### 🧪 Clases Main de prueba
Las clases de prueba se ubican en `src/test/` y permiten verificar el correcto funcionamiento de la conexión y del modelo.
---

### 🔗 Dependencias
- **Java JDK 21**  
- **MySQL Server 8.4.7 LTS**  
- **MySQL Connector/J 8.4.0**  
  👉 Descargar desde: [https://dev.mysql.com/downloads/connector/j/](https://dev.mysql.com/downloads/connector/j/)

---

### 📄 Estado actual
✅ Config y Entities  
✅ Conexión JDBC  
✅ Script SQL 
✅ Main de prueba  
🔜 DAO / Service / Main (en desarrollo)

---

📅 **Última actualización:** 4/11/2025  
📘 **Versión:** 1.0 – Entrega parcial (Cordero)

---
### Integrante responsable capa 2
**Giardini Silvia**  
 
Comisión 07 – Tecnicatura Universitaria en Programación a Distancia (UTN)
 
Rol: Implementación el patrón DAO (Data Access Object), que actúa como el "puente" entre las Entidades (Capa 1) y la base de datos MySQL.
Uso de PreparedStatement en todas las operaciones.
│
├── src/
│   ├── dao/
│   │    ├── GenericDAO.java         // Interfaz genérica (CRUD)
│   │    ├── EmpleadoDAO.java        // Interfaz específica (añade getByDni)
│   │    ├── LegajoDAO.java          // Interfaz específica (añade crearLegajo)
│   │    ├── EmpleadoDAOImpl.java    // Implementación JDBC para Empleado
│   │    └── LegajoDAOImpl.java      // Implementación JDBC para Legajo

### 📄 Estado actual
✅ Config y Entities  
✅ Conexión JDBC  
✅ Script SQL 
✅ Main de prueba  
✅ Dao
🔜 Service / Main (en desarrollo)

---
📅 **Última actualización:** 7/11/2025  
---

