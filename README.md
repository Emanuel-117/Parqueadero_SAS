# 🚗 Autos Colombia - Sistema de Control y Gestión de Parqueadero

<div align="center">

![Java](https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0+-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Hibernate](https://img.shields.io/badge/Hibernate-JPA-59666C?style=for-the-badge&logo=hibernate&logoColor=white)
![Figma](https://img.shields.io/badge/UI%2FUX-Figma%20Design-F24E1E?style=for-the-badge&logo=figma&logoColor=white)

**Iteración 1: Módulo Operativo de Entradas, Salidas, Semaforización de Celdas y Novedades**

</div>

---

## 📌 1. Información General del Proyecto

### 1.1. Descripción
**Autos Colombia** es una solución tecnológica desarrollada para automatizar el control operativo, vehicular y financiero de estacionamientos comerciales. Permite monitorear en tiempo real la disponibilidad física de las celdas, registrar ingresos y salidas, liquidar tarifas por tiempo exacto de permanencia y mantener una bitácora digital de inspección física y novedades.

### 1.2. Alcance Funcional de la Iteración 1
* **Registro de Entrada y Asignación:** Validación en tiempo real para evitar que un mismo vehículo tenga estancias duplicadas activas, guardado en catálogo y asignación de celda según categoría (`AUTOMOVIL`, `MOTO`).
* **Semaforización del Mapa de Celdas:** Panel interactivo con código de colores acordado en Figma: **Verde (`#22C55E`) para Disponible** y **Rojo (`#EF4444`) para Ocupada** con datos del vehículo actual.
* **Liquidación y Salida Vehicular:** Búsqueda rápida por placa o celda, cálculo automático de horas/minutos transcurridos, liquidación de cobro por fracción/hora y liberación inmediata de la celda.
* **Hoja de Novedades e Inspección:** Registro de incidentes, rayones, abolladuras o alertas con fecha, hora y operador responsable.

---

## 🛠️ 2. Stack Tecnológico

| Capa / Módulo | Tecnología / Herramienta | Versión / Detalle |
| :--- | :--- | :--- |
| **Lenguaje Base** | Java Standard Edition | OpenJDK 21 LTS |
| **Framework Backend** | Spring Boot | 3.3.4 (Jakarta EE 10) |
| **Capa de Persistencia** | Spring Data JPA / Hibernate | 6.5.x |
| **Motor de Base de Datos** | MySQL Server / MySQL Workbench | 8.0+ (Motor InnoDB, Charset `utf8mb4`) |
| **Base Alternativa Soportada** | PostgreSQL | 15+ (Scripts DDL/DML incluidos en `/database`) |
| **Interfaz de Usuario (Frontend)** | HTML5 Semántico, CSS3, JavaScript ES6+ | Diseño Pixel-Perfect basado en maquetas Figma |
| **Seguridad de Acceso** | BCrypt Password Hashing | 10 rounds de sal |
| **Gestión de Dependencias** | Apache Maven | Maven Wrapper integrado (`mvnw.cmd` / `mvnw`) |

---

## ⚙️ 3. Configuración del Entorno y Base de Datos

### 3.1. Requisitos Previos
1. **Java JDK 21+** instalado y configurado en el `PATH`.
2. **MySQL Server 8.0+** y **MySQL Workbench** corriendo en el puerto por defecto `3306`.
3. Un editor o IDE (Visual Studio Code, IntelliJ IDEA o Eclipse).

### 3.2. Parámetros de la Base de Datos (`autos_colombia`)
* **Host:** `localhost`
* **Puerto:** `3306`
* **Nombre de Esquema:** `autos_colombia`
* **Juego de Caracteres:** `utf8mb4` (Collation: `utf8mb4_unicode_ci`)

> [!TIP]
> **Script Listo para Workbench:**  
> Ejecuta el archivo [`database/mysql_workbench_setup.sql`](file:///c:/antivity/parqueadero/database/mysql_workbench_setup.sql) en MySQL Workbench con el rayo ⚡. Crea la base de datos, las 5 tablas relacionales y siembra automáticamente las 12 celdas y el operador de turno.

### 3.3. Configuración en `src/main/resources/application.properties`

```properties
spring.application.name=autos-colombia-parqueadero
server.port=8080

# Conexión a MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/autos_colombia?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&characterEncoding=UTF-8
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Configuración JPA / Hibernate
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

---

## 🏛️ 4. Arquitectura de Software por Capas

El backend sigue estrictamente el patrón de **Arquitectura por Capas**, desacoplando presentación, negocio, persistencia y dominio:

```
c:\antivity\parqueadero\
├── database/
│   ├── mysql_workbench_setup.sql        # Script definitivo DDL + DML para MySQL Workbench
│   ├── 01_schema_ddl.sql                # Script DDL alternativo para PostgreSQL
│   └── 02_seed_dml.sql                  # Script DML alternativo para PostgreSQL
├── src/main/java/com/autoscolombia/parqueadero/
│   ├── ParqueaderoApplication.java      # Clase principal con Testigo de Conexión en consola
│   ├── controller/                      # Capa de Presentación (Controladores REST)
│   │   ├── IngresoController.java       # POST /api/ingresos
│   │   ├── CeldaController.java         # GET  /api/celdas
│   │   ├── SalidaController.java        # POST /api/salidas/confirmar
│   │   └── NovedadController.java       # POST /api/novedades
│   ├── service/                         # Capa de Negocio (Reglas, validaciones, liquidaciones)
│   │   ├── IngresoService.java
│   │   ├── CeldaService.java
│   │   ├── SalidaService.java
│   │   └── NovedadService.java
│   ├── repository/                      # Capa de Persistencia (Spring Data JPA)
│   │   ├── VehiculoRepository.java
│   │   ├── CeldaRepository.java
│   │   ├── RegistroIngresoRepository.java
│   │   ├── NovedadRepository.java
│   │   └── UsuarioRepository.java
│   ├── model/                           # Capa de Dominio (Entidades y Enumeraciones)
│   │   ├── entity/ (Vehiculo, Celda, RegistroIngreso, Novedad, Usuario)
│   │   └── enums/  (TipoVehiculo, EstadoCelda, EstadoRegistro, RolUsuario)
│   ├── dto/                             # Objetos de Transferencia de Datos validados
│   └── exception/                       # Manejador Global de Errores (@RestControllerAdvice)
├── src/main/resources/static/           # Frontend Web servido por Spring Boot
│   ├── index.html                       # Vistas de Entrada y Salida (Figma)
│   ├── styles.css                       # Sistema de diseño, semaforización y tipografía Inter
│   └── app.js                           # Consumo de APIs REST, timers y modal de novedades
├── frontend/                            # Copia desacoplada standalone para preview directo
└── pom.xml                              # Configuración Maven con drivers MySQL y PostgreSQL
```

### 4.1. Responsabilidades de Clases y Repositorios

| Componente | Capa | Responsabilidad Técnica |
| :--- | :---: | :--- |
| `ParqueaderoApplication` | Arranque | Inicializa Spring Boot y ejecuta el testigo visual de conexión y conteo de celdas. |
| `VehiculoRepository` | Persistencia | Búsqueda por placa con normalización (`findByPlacaIgnoreCase`, `searchByPlacaLike`). |
| `CeldaRepository` | Persistencia | Monitoreo de plazas disponibles por tipo (`findCeldasDisponiblesPorTipo`, `findByActivoTrue`). |
| `RegistroIngresoRepository` | Persistencia | Consultas optimizadas con `JOIN FETCH` para evitar el problema N+1 (`findActivoByPlaca`, `findActivoByCeldaId`). |
| `NovedadRepository` | Persistencia | Registro y recuperación cronológica de incidentes (`findByRegistroIngresoIdOrderByFechaRegistroDesc`). |
| `IngresoService` | Negocio | Valida que la placa no esté activa, verifica celda libre, registra catálogo y asigna a `OCUPADA`. |
| `CeldaService` | Negocio | Calcula tiempos en curso y emite el mapa con badges Verde (`#22C55E`) y Rojo (`#EF4444`). |
| `SalidaService` | Negocio | Localiza estancia activa, calcula tiempo transcurrido (h/min), liquida tarifa y libera celda a `DISPONIBLE`. |
| `NovedadService` | Negocio | Administra la bitácora de novedades e inspecciones vehiculares. |
| `GlobalExceptionHandler` | Transversal | Centraliza respuestas de error estándar en formato JSON (`400 Bad Request`, `404 Not Found`, `500`). |

---

## 📡 5. Catálogo de Rutas y Endpoints API REST

URL Base: `http://localhost:8080/api`

| Método | Endpoint | Descripción | Body / Parámetros | Códigos HTTP |
| :---: | :--- | :--- | :--- | :---: |
| **`POST`** | `/api/ingresos` | Registra el ingreso vehicular, valida placa y cambia celda a `OCUPADA`. | **JSON:** `placa`, `tipoVehiculo`, `idCelda`, `observacionesNovedad` | `201 Created`<br>`400 Bad Request` |
| **`GET`** | `/api/celdas` | Retorna el mapa completo con semaforización y vehículo ocupante actual. | *Ninguno* | `200 OK` |
| **`POST`** | `/api/salidas/confirmar` | Liquida tiempo (h/min), calcula cobro, lista novedades y libera celda. | **JSON:** `placa` (o `idCelda` / `numeroCelda`), `observaciones` | `200 OK`<br>`400 / 404` |
| **`POST`** | `/api/novedades` | Registra incidentes, rayones o golpes al entrar o durante la estancia. | **JSON:** `idRegistroIngreso`, `idUsuario`, `tipoNovedad`, `descripcion` | `201 Created`<br>`400 / 404` |
| **`GET`** | `/api/novedades/registro/{id}` | Historial cronológico de novedades de una estancia específica. | **Path Variable:** `id` (ID de estancia) | `200 OK`<br>`404 Not Found` |

### 5.1. Ejemplos de Payloads

#### Registrar Ingreso (`POST /api/ingresos`)
```json
{
  "placa": "ABC123",
  "tipoVehiculo": "AUTOMOVIL",
  "idCelda": 1,
  "observacionesNovedad": "Rayón leve en puerta delantera izquierda",
  "valorTarifaHora": 5000.00
}
```

#### Confirmar Salida y Cobro (`POST /api/salidas/confirmar`)
```json
{
  "placa": "ABC123"
}
```

---

## 🚀 6. Guía de Ejecución Paso a Paso

### Paso 1: Inicializar la Base de Datos en MySQL Workbench
1. Abre **MySQL Workbench** y conéctate a tu instancia local (`localhost:3306`).
2. Ve a **File ➔ Open SQL Script...** y selecciona:
   ```
   c:\antivity\parqueadero\database\mysql_workbench_setup.sql
   ```
3. Ejecuta el script completo presionando el **Rayo ⚡ (Execute)**.
4. Se creará la base de datos `autos_colombia`, las 5 tablas y las 12 celdas listas para asignación.

### Paso 2: Iniciar el Backend (Spring Boot)
Tienes dos opciones sencillas:

* **Desde VS Code / IDE (Recomendado):**  
  Abre [`ParqueaderoApplication.java`](file:///c:/antivity/parqueadero/src/main/java/com/autoscolombia/parqueadero/ParqueaderoApplication.java) y haz clic en el texto **`▶ Run`** que aparece sobre el método `main` (o presiona `F5`).
* **Desde Terminal (PowerShell / CMD):**  
  ```powershell
  cd c:\antivity\parqueadero
  .\mvnw.cmd spring-boot:run
  ```

> [!NOTE]
> **Testigo en Consola:** Al iniciar, verás el banner que valida la conexión en vivo a MySQL y el conteo de celdas:
> ```text
> ================================================================================
>    🚗  PARQUEADERO 'AUTOS COLOMBIA' - INICIADO CON ÉXITO
> ================================================================================
>    ✅ ESTADO DEL SISTEMA : EN LÍNEA Y OPERATIVO
>    🗄️  BASE DE DATOS     : MySQL 8.x.x
>    📁 ESQUEMA ACTIVO     : autos_colombia
>    🅿️  CELDAS CARGADAS   : 12 celdas listas para asignación
>    🌐 SERVIDOR LOCAL     : http://localhost:8080
>    🖥️  INTERFAZ DE USUARIO: http://localhost:8080/index.html
> ================================================================================
> ```

### Paso 3: Abrir la Interfaz de Usuario (Frontend Figma)
* Abre tu navegador en:  
  👉 **`http://localhost:8080/index.html`** (o `http://localhost:8080/`)
* O abre directamente con doble clic el archivo local:  
  👉 [`frontend/index.html`](file:///c:/antivity/parqueadero/frontend/index.html)

---

## 👥 7. Usuarios y Credenciales Precargadas (Seeders)

| Nombre Completo | Username | Rol | Contraseña en Plano | Hash BCrypt Almacenado |
| :--- | :--- | :---: | :---: | :--- |
| **Carlos Andrés Restrepo** | `operador_turno1` | `OPERADOR` | `Operador2026*` | `$2a$10$wT0o3q6Fv1O/F8S8ZqXqee2nFvUeL7l8dGvh7xW/ZqXqee2nFvUeL` |
| **Laura Milena Morales** | `admin_sistema` | `ADMINISTRADOR` | `Admin2026*` | `$2a$10$8K1p/a0dL1O9l2Z8fQeLdu0kF7V7kL8dGvh7xW8ZqXqee2nFvUeL.` |

---

<div align="center">
  <sub>Desarrollado para el sistema de control vehicular <strong>Autos Colombia</strong> • Iteración 1</sub>
</div>
