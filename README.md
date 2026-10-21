# Proyecto UCA-CFC-Connect

Sistema web para la gestión de **cursos, diplomados, inscripciones, cotizaciones y servicios complementarios** de un centro de formación.

El proyecto está desarrollado con **Java y Spring Boot**, utilizando una arquitectura por capas basada en **Controller, Service, Repository y Domain (JPA)**, con persistencia real en base de datos, validaciones, manejo centralizado de excepciones y documentación Swagger/OpenAPI.

---

## 📌 Descripción del proyecto

**CFC Connect** es una aplicación web orientada a centralizar y optimizar la gestión de los servicios académicos y administrativos de un centro de formación.

El sistema permite administrar:

* Clientes.
* Cursos.
* Diplomados.
* Inscripciones.
* Cotizaciones.
* Espacios.
* Servicios de catering.
* Pagos.
* Usuarios.
* Roles y permisos.

La **Fase 1** se enfocó en el análisis, diseño y construcción de la arquitectura base (en memoria). La **Fase 2**, ya completada, incorporó **persistencia real con JPA/Hibernate**, **CRUD completo** en los 10 módulos, **DTOs con validaciones**, **manejo centralizado de excepciones** y **documentación Swagger/OpenAPI**. La **Fase 3** agrega **seguridad**: Spring Security, **cifrado de contraseñas con BCrypt** y **autenticación con tokens JWT** (login, emisión, validación y expiración), con control de acceso por rol y protección de la administración de usuarios.

---

## 🎯 Objetivo general

Desarrollar una aplicación web que permita gestionar de forma centralizada los procesos académicos y administrativos de un centro de formación, facilitando el registro de clientes, cursos, diplomados, inscripciones, cotizaciones, espacios, catering y pagos.

---

## 🎯 Objetivos específicos

* [x] Diseñar una arquitectura organizada y escalable.
* [x] Implementar una API REST utilizando Spring Boot.
* [x] Gestionar clientes y cursos mediante endpoints REST.
* [x] Separar las responsabilidades mediante capas (Controller, Service, Repository, DTO, Domain).
* [x] Definir el modelo de dominio del sistema.
* [x] Implementar persistencia con JPA/Hibernate.
* [x] Implementar CRUD completo en todos los módulos.
* [x] Implementar validaciones con Bean Validation (`@Valid`, `@NotBlank`, `@Email`, `@Pattern`, `@Positive`, etc.).
* [x] Implementar manejo centralizado de excepciones (`@ControllerAdvice`).
* [x] Documentar la API con Swagger/OpenAPI.
* [x] Implementar pruebas unitarias básicas.
* [x] Facilitar el trabajo colaborativo mediante Git y GitHub.
* [x] Implementar seguridad y autenticación con Spring Security + JWT (Fase 3).

---

## 🛠️ Tecnologías utilizadas

| Tecnología          | Uso                                    | Estado          |
| ------------------- | --------------------------------------- | ---------------- |
| Java 21              | Lenguaje principal                     | ✅ Implementado   |
| Spring Boot 3.3       | Framework backend                      | ✅ Implementado   |
| Spring Web            | Desarrollo de API REST                 | ✅ Implementado   |
| Spring Data JPA       | Persistencia de datos                  | ✅ Implementado   |
| Hibernate              | Proveedor JPA / ORM                    | ✅ Implementado   |
| Bean Validation (Jakarta) | Validaciones de entrada (`@Valid`) | ✅ Implementado   |
| H2 Database            | Base de datos en memoria (desarrollo)  | ✅ Implementado   |
| MySQL                  | Base de datos relacional (producción)  | ✅ Configurado (perfil `mysql`) |
| Springdoc OpenAPI     | Documentación Swagger                  | ✅ Implementado   |
| Maven                  | Gestión de dependencias                | ✅ Implementado   |
| JUnit 5                | Pruebas unitarias                      | ✅ Implementado   |
| Git / GitHub            | Control de versiones y colaboración   | ✅ Implementado   |
| Spring Security        | Autenticación, BCrypt y control por rol | ✅ Implementado   |
| JJWT (io.jsonwebtoken) | Emisión y validación de tokens JWT      | ✅ Implementado   |

---

## 🏗️ Arquitectura

El proyecto utiliza una arquitectura por capas, organizada **por módulo de negocio** (vertical slicing) en lugar de agrupar todo por tipo de clase:

```text
Cliente HTTP
   │
   ▼
Controller   → expone los endpoints REST, valida el DTO de entrada (@Valid)
   │
   ▼
Service      → logica de negocio (reglas, calculos, validaciones cruzadas)
   │
   ▼
Repository   → interfaces JpaRepository, acceso a datos
   │
   ▼
Domain       → entidades JPA (@Entity)
   │
   ▼
Base de datos (H2 en memoria / MySQL)

Excepciones de cualquier capa ──► GlobalExceptionHandler (@ControllerAdvice) ──► respuesta JSON estandar (ApiError)
```

### Controller
Recibe las solicitudes HTTP, valida el `@RequestBody` con `@Valid` y expone los endpoints REST del modulo.

### Service / Service.implementation
Contiene la logica de negocio (por ejemplo: validar cupo disponible, evitar solapamiento de espacios, aprobar/rechazar cotizaciones) y actua como intermediario entre el controlador y el repositorio.

### Repository
Interfaces que extienden `JpaRepository<Entidad, Long>`. Spring Data JPA genera la implementacion e incluye metodos de consulta derivados (`findByEmailIgnoreCase`, `findByDisponibleTrue`, etc.).

### Domain
Entidades `@Entity` que representan las tablas de la base de datos, con sus relaciones (`@ManyToOne`) y reglas de negocio simples (por ejemplo `tieneCupoDisponible()`).

> **Nota sobre el esquema de base de datos:** el proyecto **no** ejecuta ningun script `.sql` en tiempo de arranque. El esquema real (tablas, columnas, llaves foraneas) lo genera Hibernate automaticamente a partir de las entidades `@Entity`, mediante `spring.jpa.hibernate.ddl-auto=update`. El archivo [`docs/schema.sql`](docs/schema.sql) existe unicamente como **documentacion** para el informe de la fase, y se genero leyendo columna por columna las entidades reales — por lo tanto solo contiene las 10 tablas que en verdad existen en el codigo (no incluye tablas como `categorias` o `agenda`, que no se implementaron en esta fase).

### DTO (Request / Response)
Separan el modelo de persistencia del contrato de la API. Los `*Request` llevan las anotaciones de validacion; los `*Response` controlan que datos se exponen (por ejemplo, `Usuario` nunca expone el `password`).

### Exception
Excepciones de negocio propias (`ResourceNotFoundException`, `CupoAgotadoException`, `EspacioOcupadoException`, `ReglaNegocioException`) capturadas de forma centralizada por `GlobalExceptionHandler`.

---

## 📂 Estructura del proyecto

```text
src/
├── main/
│   ├── java/
│   │   └── sv/edu/udb/cfcconnect/
│   │       │
│   │       ├── CfcConnectApplication.java
│   │       │
│   │       ├── config/
│   │       │   ├── OpenApiConfig.java        # configuracion de Swagger/OpenAPI (con esquema Bearer)
│   │       │   ├── SecurityConfig.java       # Spring Security: BCrypt, cadena de filtros y reglas por rol
│   │       │   ├── JwtProperties.java        # propiedades app.jwt.* (clave, expiracion, emisor)
│   │       │   └── DataSeeder.java           # carga datos de prueba al iniciar (CommandLineRunner)
│   │       │
│   │       ├── exception/
│   │       │   ├── ApiError.java             # formato estandar de error
│   │       │   ├── GlobalExceptionHandler.java  # @ControllerAdvice
│   │       │   ├── RestAuthenticationEntryPoint.java  # 401 en JSON
│   │       │   ├── RestAccessDeniedHandler.java      # 403 en JSON
│   │       │   ├── CredencialesInvalidasException.java
│   │       │   ├── UsuarioInactivoException.java
│   │       │   ├── ResourceNotFoundException.java
│   │       │   ├── CupoAgotadoException.java
│   │       │   ├── EspacioOcupadoException.java
│   │       │   └── ReglaNegocioException.java
│   │       │
│   │       ├── seguridad/
│   │       │   ├── controller/AuthController.java   # POST /auth/login, GET /auth/me
│   │       │   ├── domain/UsuarioPrincipal.java     # UserDetails + roles
│   │       │   ├── dto/AuthRequest.java
│   │       │   ├── dto/AuthResponse.java
│   │       │   ├── dto/SesionResponse.java
│   │       │   ├── jwt/JwtService.java              # emision y validacion de tokens
│   │       │   ├── jwt/JwtAuthenticationFilter.java  # filtro que lee el header Bearer
│   │       │   ├── service/AuthService.java
│   │       │   └── service/implementation/
│   │       │       ├── AuthServiceImpl.java
│   │       │       └── UsuarioDetailsServiceImpl.java
│   │       │
│   │       ├── cliente/
│   │       │   ├── domain/Cliente.java
│   │       │   ├── dto/ClienteRequest.java
│   │       │   ├── dto/ClienteResponse.java
│   │       │   ├── repository/ClienteRepository.java
│   │       │   ├── service/ClienteService.java
│   │       │   ├── service/implementation/ClienteServiceImpl.java
│   │       │   └── controller/ClienteController.java
│   │       │
│   │       ├── curso/          (misma estructura: domain / dto / repository / service / controller)
│   │       ├── diplomado/      (misma estructura)
│   │       ├── espacio/        (misma estructura)
│   │       ├── catering/       (misma estructura)
│   │       ├── inscripcion/    (misma estructura)
│   │       ├── cotizacion/     (misma estructura)
│   │       ├── pago/           (misma estructura)
│   │       ├── rol/            (misma estructura)
│   │       └── usuario/        (misma estructura)
│   │
│   └── resources/
│       ├── application.properties          # perfil por defecto (H2)
│       ├── application-mysql.properties     # perfil MySQL
│       └── static/                         # panel web (frontend)
│           ├── index.html                  # estructura + pantalla de login
│           ├── app.js                      # lógica del panel + manejo del token JWT
│           └── app.css                     # estilos del panel
│
└── test/
    └── java/sv/edu/udb/cfcconnect/...

docs/
└── schema.sql   # DDL de referencia/documentacion, generado a partir de las
                 # entidades reales. NO se ejecuta en tiempo de arranque
                 # (el esquema real lo crea Hibernate via ddl-auto=update).
```

Cada modulo de negocio replica el mismo patron (`domain`, `dto`, `repository`, `service`, `service/implementation`, `controller`), lo que facilita que cada integrante del equipo trabaje su modulo de forma independiente.

---

## 📋 Módulos del sistema

| Módulo        | Descripción                                          | CRUD | Endpoints especiales |
| ------------- | ----------------------------------------------------- | :--: | --------------------- |
| Clientes      | Administración de clientes                             | ✅   | — |
| Cursos        | Gestión de cursos disponibles                          | ✅   | `PATCH /cursos/{id}/inscribir` |
| Diplomados    | Gestión de diplomados                                   | ✅   | `PATCH /diplomados/{id}/inscribir` |
| Inscripciones | Registro de clientes en cursos                          | ✅   | `PATCH /inscripciones/{id}/estado` |
| Cotizaciones  | Elaboración y gestión de cotizaciones                    | ✅   | `PATCH /cotizaciones/{id}/aprobar`, `/rechazar` |
| Espacios      | Administración de espacios disponibles                  | ✅   | `PATCH /espacios/{id}/reservar`, `/liberar` |
| Catering      | Gestión de servicios de alimentación                    | ✅   | — |
| Pagos         | Registro y control de pagos                              | ✅   | `PATCH /pagos/{id}/confirmar` |

> Los 10 módulos cuentan con CRUD **completo** (`GET`, `GET /{id}`, `POST`, `PUT`, `DELETE`). Cotizaciones, Inscripciones y Pagos combinan el `PUT` estándar con endpoints `PATCH` puntuales para las transiciones de estado propias de su flujo de negocio (aprobar/rechazar, cambiar estado, confirmar).
| Usuarios      | Administración de usuarios                               | ✅   | — |
| Roles         | Gestión de permisos y accesos                            | ✅   | — |

---

# 🚀 Fase 1 — Análisis, diseño y arquitectura

### Actividades realizadas

* [x] Definición del proyecto.
* [x] Identificación de los módulos principales.
* [x] Definición inicial del modelo de dominio.
* [x] Diseño de la arquitectura.
* [x] Creación del proyecto Spring Boot.
* [x] Organización de paquetes.
* [x] Creación de modelos iniciales (en memoria).
* [x] Implementación de Repository (en memoria).
* [x] Implementación de Service.
* [x] Implementación de Controller.
* [x] Creación de endpoints GET.
* [x] Pruebas unitarias básicas.

---

# 🚀 Fase 2 — Persistencia, CRUD, validaciones, excepciones y Swagger ✅

### Actividades realizadas

* [x] **Persistencia con JPA/Hibernate**: todas las entidades (`Cliente`, `Curso`, `Diplomado`, `Espacio`, `ServicioCatering`, `Inscripcion`, `Cotizacion`, `Pago`, `Usuario`, `Rol`) se mapearon como `@Entity`, con relaciones `@ManyToOne` donde corresponde (por ejemplo `Inscripcion → Cliente`, `Inscripcion → Curso`).
* [x] **Base de datos**: perfil por defecto con **H2 en memoria** (cero configuración, ideal para desarrollo y pruebas) y perfil `mysql` listo para producción (`application-mysql.properties`).
* [x] **DataSeeder**: carga automática de datos de prueba al iniciar la aplicación (roles, clientes, cursos, diplomados, espacios y catering), incluyendo un curso sin cupo para poder probar la regla de negocio de inmediato.
* [x] **CRUD completo** (`GET`, `GET /{id}`, `POST`, `PUT`, `DELETE`) en los **10 módulos sin excepción** — incluyendo `PUT` en Cotización, Inscripción y Pago, que inicialmente solo tenían endpoints `PATCH` de transición de estado (aprobar/rechazar, cambiar estado, confirmar) y no un `PUT` real de edición.
* [x] **Esquema de base de datos sin inconsistencias**: el proyecto no ejecuta ningún `.sql` en tiempo de arranque (el esquema lo genera Hibernate vía `ddl-auto=update`). Se agregó `docs/schema.sql` solo como documentación de referencia, generado columna por columna desde las entidades `@Entity` reales, para que no describa tablas o campos que no existen en el código (ver nota en la sección *Arquitectura*).
* [x] **DTOs de entrada y salida** (`*Request` / `*Response`) para no exponer las entidades JPA directamente (por ejemplo, `UsuarioResponse` nunca expone el `password`).
* [x] **Validaciones** con Jakarta Bean Validation: `@Valid`, `@NotBlank`, `@NotNull`, `@Email`, `@Size`, `@Pattern`, `@Positive`, `@PositiveOrZero` en todos los DTO `*Request`.
* [x] **Manejo centralizado de excepciones** con `@ControllerAdvice` + `@ExceptionHandler` (`GlobalExceptionHandler`), que traduce cada excepción a un código HTTP y a un cuerpo `ApiError` consistente:
  * `ResourceNotFoundException` → `404 Not Found`
  * `CupoAgotadoException`, `EspacioOcupadoException`, `ReglaNegocioException` → `409 Conflict`
  * `IllegalArgumentException` → `400 Bad Request`
  * `MethodArgumentNotValidException` (fallo de `@Valid`) → `400 Bad Request` con el detalle de cada campo
  * Cualquier otra excepción → `500 Internal Server Error`
* [x] **Documentación Swagger/OpenAPI**: `springdoc-openapi` + `@Tag` en cada controlador + `@Operation` en cada endpoint, disponible en `/swagger-ui.html`.
* [x] Reglas de negocio implementadas y explícitas:
  * `Curso` / `Diplomado`: no se puede inscribir a un participante si no hay cupo disponible.
  * `Espacio`: no se puede reservar un espacio que ya está ocupado.
  * `Cotizacion`: no se puede aprobar una cotización rechazada, ni rechazar una aprobada.
  * `Cliente` / `Usuario`: no se permite registrar correos duplicados.

### Pendiente para próximas fases

* [x] Paginación, filtros y ordenamiento en los listados (`GET`) — endpoints `/{modulo}/buscar` (traído de `upstream/main`).
* [ ] Pruebas unitarias con Mockito para **todos** los servicios (ya hay de curso, espacio e inscripción).
* [ ] Relación N:M real entre `Cotizacion` y los ítems que incluye (tabla `detalle_cotizacion`).

---

# 🚀 Fase 3 — Seguridad: Spring Security, BCrypt y JWT ✅

### Actividades realizadas

* [x] **Dependencias**: se agregó `spring-boot-starter-security` (Spring Security 6.3) y `io.jsonwebtoken` (JJWT 0.12.6: `jjwt-api`, `jjwt-impl`, `jjwt-jackson`), además de `spring-security-test` para las pruebas.
* [x] **Cifrado de contraseñas con BCrypt**: se declaró el bean `PasswordEncoder` (`BCryptPasswordEncoder`) en `SecurityConfig`. `UsuarioServiceImpl` cifra la contraseña **antes de guardarla** (tanto al crear como al actualizar un usuario), por lo que en la tabla `usuarios` solo se almacena el hash `$2a$...`; el texto plano nunca se persiste ni se devuelve en las respuestas.
* [x] **Login con JWT** (`POST /auth/login`): se creó el módulo `seguridad` con `AuthController`, `AuthService`/`AuthServiceImpl`, `JwtService` y `JwtAuthenticationFilter`. Las credenciales se validan contra el hash BCrypt mediante el `AuthenticationManager` de Spring Security y, si son correctas, se emite un token JWT firmado con **HS256**.
* [x] **Emisión del token**: `JwtService` construye el token con los claims `sub` (correo), `rol` (ADMIN, RECEPCIONISTA, CLIENTE, CONTABILIDAD), `iss` (emisor), `iat` y `exp`.
* [x] **Validación del token**: `JwtAuthenticationFilter` (un `OncePerRequestFilter`) lee la cabecera `Authorization: Bearer <token>` en cada petición, verifica la firma y la expiración, recarga el usuario desde la base de datos y establece la autenticación en el contexto de seguridad. Un token inválido, alterado o expirado deja la petición sin autenticar y termina en un `401`.
* [x] **Expiración configurable**: la validez del token se define en `app.jwt.expiration-seconds` (por defecto **3600 s = 1 hora**) y se devuelve en la respuesta del login (`expiresIn` y `expiracion`).
* [x] **Modo *stateless***: sin sesiones HTTP (`SessionCreationPolicy.STATELESS`) y sin CSRF, ya que la autenticación viaja en la cabecera `Authorization`.
* [x] **Protección de la administración de usuarios**: `/usuarios/**` y `/roles/**` exigen el rol **ADMIN**; el resto de los endpoints exigen un usuario autenticado. Las respuestas `401` y `403` se devuelven en el mismo formato `ApiError` JSON del resto de la API (`RestAuthenticationEntryPoint` y `RestAccessDeniedHandler`).
* [x] **Usuarios inactivos**: un usuario con `estado = false` no puede iniciar sesión (`403`).
* [x] **Usuarios de prueba**: el `DataSeeder` crea un administrador y un recepcionista con la contraseña cifrada con BCrypt para poder probar el flujo de inmediato. Se comprueban **por correo** (no solo si la tabla está vacía), para que siempre exista un administrador con el que entrar, incluso en una base de datos MySQL que ya tenga usuarios de la Fase 2.
* [x] **Migración de contraseñas de la Fase 2**: al arrancar, las contraseñas que quedaron guardadas **en texto plano** en bases de datos con datos previos se re-cifran con BCrypt. Sin este paso, esos usuarios no podrían iniciar sesión (el login compara contra el hash). Solo se tocan las contraseñas que aún no están cifradas (las que no empiezan por `$2`).
* [x] **Swagger con autenticación**: se registró el esquema de seguridad `bearerAuth` en el documento OpenAPI, de modo que en Swagger UI se puede autorizar con el token mediante el botón **Authorize**.
* [x] **Pruebas**: se agregaron `JwtServiceTest` (emisión, validación, token alterado, expiración y BCrypt), `AuthServiceTest` (login correcto e incorrecto), `SecurityIntegrationTest` (`401` sin token, `403` sin rol ADMIN, `200` con token válido) y `DataSeederTest` (migración de contraseñas legacy).

### Configuración

```properties
# Clave de firma (Base64, minimo 32 bytes). Generar con: openssl rand -base64 32
app.jwt.secret=${APP_JWT_SECRET:YmRkZmQ5ODQ4MTg3NDRkNzk0MWM1NmM4MmMxZWE0Zjg=}
# Duracion del token en segundos (3600 = 1 hora)
app.jwt.expiration-seconds=3600
app.jwt.issuer=uca-cfc-connect
```

> En producción la clave **debe** sobreescribirse con la variable de entorno `APP_JWT_SECRET`.

### Credenciales de prueba

| Rol | Correo | Contraseña |
|---|---|---|
| ADMIN | `admin@cfc.edu.sv` | `Admin123*` |
| RECEPCIONISTA | `recepcion@cfc.edu.sv` | `Recepcion123*` |

### Flujo de uso

```text
1. POST /auth/login        -> recibe el token JWT
2. GET /clientes           -> Authorization: Bearer <token>
```

---

# 🔌 Endpoints disponibles

Todos los endpoints devuelven y reciben JSON. Los que reciben cuerpo (`POST` / `PUT`) validan el DTO con `@Valid` antes de ejecutar la lógica de negocio.

> ⚠️ **Desde la Fase 3 todos los endpoints requieren un token JWT**, excepto `POST /auth/login`, la documentación de Swagger (`/swagger-ui/**`, `/v3/api-docs/**`) y la consola H2 (`/h2-console/**`).

## Autenticación — `/auth`

| Método | Endpoint | Acceso | Descripción |
|---|---|---|---|
| POST | `/auth/login` | Público | Iniciar sesión y obtener el token JWT |
| GET | `/auth/me` | Autenticado | Datos del usuario autenticado y sus roles |

```http
POST http://localhost:8080/auth/login
Content-Type: application/json

{
  "email": "admin@cfc.edu.sv",
  "password": "Admin123*"
}
```

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBjZmMu...",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "expiracion": "2026-10-20T19:21:48",
  "email": "admin@cfc.edu.sv",
  "nombre": "Administrador CFC",
  "rol": "ADMIN"
}
```

Con el token se accede al resto de la API:

```http
GET http://localhost:8080/usuarios
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBjZmMu...
```

## Clientes — `/clientes`

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/clientes` | Listar todos los clientes |
| GET | `/clientes/{id}` | Obtener un cliente por id |
| POST | `/clientes` | Registrar un nuevo cliente |
| PUT | `/clientes/{id}` | Actualizar un cliente |
| DELETE | `/clientes/{id}` | Eliminar un cliente |

Ejemplo:

```http
GET http://localhost:8080/clientes
```

```json
[
    { "id": 1, "nombre": "Juan Perez", "correo": "juan@gmail.com", "telefono": "7777-1111" },
    { "id": 2, "nombre": "Ana Lopez", "correo": "ana@gmail.com", "telefono": "7777-2222" }
]
```

```http
POST http://localhost:8080/clientes
Content-Type: application/json

{
  "nombre": "Carlos Hernandez",
  "correo": "carlos@gmail.com",
  "telefono": "7000-1111"
}
```

## Cursos — `/cursos`

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/cursos` | Listar todos los cursos |
| GET | `/cursos/{id}` | Obtener un curso por id |
| POST | `/cursos` | Crear un curso |
| PUT | `/cursos/{id}` | Actualizar un curso |
| PATCH | `/cursos/{id}/inscribir` | Inscribir un participante (valida cupo disponible) |
| DELETE | `/cursos/{id}` | Eliminar un curso |

```http
GET http://localhost:8080/cursos
```

```json
[
    { "id": 1, "nombre": "Spring Boot", "precio": 120.0, "cupo": 30, "inscritos": 0, "cupoDisponible": true },
    { "id": 2, "nombre": "React", "precio": 150.0, "cupo": 25, "inscritos": 25, "cupoDisponible": false }
]
```

```http
PATCH http://localhost:8080/cursos/2/inscribir
```

```json
{
  "timestamp": "2026-08-30T22:10:00",
  "status": 409,
  "error": "Conflict",
  "message": "No es posible inscribir al participante: el curso 'React' ya alcanzo su cupo maximo (25).",
  "path": "/cursos/2/inscribir"
}
```

## Diplomados — `/diplomados`

Mismo patrón que Cursos: `GET`, `GET /{id}`, `POST`, `PUT`, `PATCH /{id}/inscribir`, `DELETE`.

## Inscripciones — `/inscripciones`

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/inscripciones` | Listar todas las inscripciones |
| GET | `/inscripciones/{id}` | Obtener una inscripción por id |
| POST | `/inscripciones` | Crear una inscripción (`clienteId`, `cursoId`) |
| PUT | `/inscripciones/{id}` | Actualizar cliente/curso de una inscripción (ajusta cupos si el curso cambia) |
| PATCH | `/inscripciones/{id}/estado?estado=CONFIRMADA` | Cambiar estado (`PENDIENTE`, `CONFIRMADA`, `CANCELADA`, `FINALIZADA`) |
| DELETE | `/inscripciones/{id}` | Eliminar una inscripción |

## Cotizaciones — `/cotizaciones`

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/cotizaciones` | Listar todas las cotizaciones |
| GET | `/cotizaciones/{id}` | Obtener una cotización por id |
| POST | `/cotizaciones` | Solicitar una cotización (`clienteId`, `tipo`, `descripcion`, `total`) |
| PUT | `/cotizaciones/{id}` | Actualizar una cotización (solo si está PENDIENTE o EN_PROCESO) |
| PATCH | `/cotizaciones/{id}/aprobar` | Aprobar una cotización |
| PATCH | `/cotizaciones/{id}/rechazar` | Rechazar una cotización |
| DELETE | `/cotizaciones/{id}` | Eliminar una cotización |

`tipo` admite: `CURSO`, `DIPLOMADO`, `ESPACIO`, `CATERING`, `COMBINADO`.

## Espacios — `/espacios`

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/espacios?soloDisponibles=true` | Listar espacios (opcionalmente solo disponibles) |
| GET | `/espacios/{id}` | Obtener un espacio por id |
| POST | `/espacios` | Registrar un espacio |
| PUT | `/espacios/{id}` | Actualizar un espacio |
| PATCH | `/espacios/{id}/reservar` | Reservar (falla con 409 si ya está ocupado) |
| PATCH | `/espacios/{id}/liberar` | Liberar el espacio |
| DELETE | `/espacios/{id}` | Eliminar un espacio |

## Catering — `/catering`

CRUD estándar: `GET`, `GET /{id}`, `POST`, `PUT`, `DELETE` sobre los servicios (Coffee Break, Almuerzo, Refrigerio, etc.).

## Pagos — `/pagos`

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/pagos` | Listar todos los pagos |
| GET | `/pagos/{id}` | Obtener un pago por id |
| POST | `/pagos` | Registrar un pago (`clienteId`, `tipoReferencia`, `referenciaId`, `monto`, `metodo`) |
| PUT | `/pagos/{id}` | Actualizar un pago (rechazado si ya está `PAGADO`) |
| PATCH | `/pagos/{id}/confirmar` | Confirmar el pago (estado → `PAGADO`) |
| DELETE | `/pagos/{id}` | Eliminar un pago |

`tipoReferencia`: `INSCRIPCION`, `COTIZACION`, `ESPACIO`, `CATERING`. `metodo`: `EFECTIVO`, `TARJETA`, `TRANSFERENCIA`, `DEPOSITO`.

## Búsquedas con filtros y paginación — `/{modulo}/buscar`

Endpoints de búsqueda paginada (traídos de `upstream/main`, rama `feature/busquedas-y-pruebas`). Devuelven un `Page` de Spring Data.

| Método | Endpoint | Filtros | Orden por defecto |
|---|---|---|---|
| GET | `/clientes/buscar` | `nombre`, `correo` | `nombre` |
| GET | `/cursos/buscar` | `nombre`, `soloConCupo` | `nombre` |
| GET | `/diplomados/buscar` | `nombre`, `soloConCupo` | `nombre` |

Parámetros de paginación y ordenamiento (comunes a los tres):

* `page` — número de página (desde 0).
* `size` — registros por página (por defecto **10**).
* `sort` — campo y sentido, por ejemplo `sort=precio,desc`.

```http
GET http://localhost:8080/cursos/buscar?nombre=Spring&soloConCupo=true&page=0&size=5&sort=precio,desc
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

```json
{
  "content": [ { "id": 1, "nombre": "Spring Boot", "precio": 120.0, "cupo": 30 } ],
  "pageable": { "pageNumber": 0, "pageSize": 5, "sort": { "precio": "desc" } },
  "totalElements": 1,
  "totalPages": 1,
  "number": 0,
  "size": 5
}
```

## Usuarios — `/usuarios` y Roles — `/roles`

CRUD estándar (`GET`, `GET /{id}`, `POST`, `PUT`, `DELETE`). Un usuario requiere un `rolId` válido; el `password` nunca se devuelve en las respuestas.

> 🔒 **Acceso restringido**: estos endpoints requieren el rol **ADMIN**. Un usuario autenticado con otro rol recibe `403 Forbidden`; sin token, `401 Unauthorized`.

| Método | Endpoint | Rol |
|---|---|---|
| GET | `/usuarios` | ADMIN |
| GET | `/usuarios/{id}` | ADMIN |
| POST | `/usuarios` | ADMIN |
| PUT | `/usuarios/{id}` | ADMIN |
| DELETE | `/usuarios/{id}` | ADMIN |
| GET/POST/PUT/DELETE | `/roles/**` | ADMIN |

Al crear o actualizar un usuario, la contraseña se recibe en texto plano pero **se almacena cifrada con BCrypt**: nunca se guarda ni se devuelve en texto plano.

---

# 📑 Documentación interactiva (Swagger)

Con la aplicación corriendo, la documentación completa de todos los endpoints está disponible en:

```text
http://localhost:8080/swagger-ui.html
```

Y el contrato OpenAPI en formato JSON en:

```text
http://localhost:8080/v3/api-docs
```

---

# 🗄️ Consola de base de datos (H2)

Con el perfil por defecto (H2 en memoria), se puede inspeccionar la base de datos en:

```text
http://localhost:8080/h2-console
```

* JDBC URL: `jdbc:h2:mem:cfcconnect`
* Usuario: `sa`
* Contraseña: *(vacía)*

> ⚠️ **Ojo con el formulario:** H2 viene con `jdbc:h2:~/test` por defecto y **Spring Boot no lo cambia**. Si se pulsa *Connect* sin editarlo, H2 intenta abrir `C:\Users\<tu-usuario>\test` y falla con el error `Database "C:/Users/marri/test" not found ... [90149-224]`. Hay que **borrar ese valor y escribir `jdbc:h2:mem:cfcconnect`** para ver los datos reales de la aplicación (la consola se ejecuta dentro de la JVM de la app, por eso alcanza la base en memoria).

Para comprobar que las contraseñas se guardan cifradas:

```sql
SELECT email, password FROM usuarios;
```

Los valores empiezan por `$2a$10$...` (hash BCrypt), nunca la contraseña en texto plano.

---

# ⚙️ Requisitos

Para ejecutar el proyecto se recomienda tener instalado:

* Java JDK 21 o superior.
* Maven.
* Git.
* IntelliJ IDEA, Eclipse o Visual Studio Code.
* Postman o una herramienta similar para probar la API (o usar directamente Swagger UI).
* (Opcional) MySQL 8 si se desea usar el perfil `mysql` en lugar de H2.

---

# 📥 Instalación

Clonar el repositorio:

```bash
git clone https://github.com/USUARIO/cfc-connect.git
```

Ingresar al proyecto:

```bash
cd cfc-connect
```

Compilar el proyecto:

```bash
mvn clean install
```

---

# ▶️ Ejecución

### Con base de datos H2 (por defecto, sin configuración adicional)

```bash
mvn spring-boot:run
```

### Con MySQL

1. Crear la base de datos (o dejar que se cree automáticamente):

```sql
CREATE DATABASE uca_cfc_connect;
```

2. Ajustar usuario/contraseña en `application-mysql.properties` si es necesario.
3. Ejecutar con el perfil `mysql`:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

También puede ejecutarse desde el IDE utilizando la clase principal `CfcConnectApplication`.

La aplicación estará disponible en:

```text
http://localhost:8080
```

Al iniciar, el `DataSeeder` carga automáticamente datos de prueba (roles, clientes, cursos, diplomados, espacios, catering y usuarios) para poder probar la API de inmediato.

## 🖥️ Panel web (frontend)

| Qué | URL |
|---|---|
| **Panel web** | `http://localhost:8080` → pide iniciar sesión |
| **Swagger UI** (botón *Authorize*) | `http://localhost:8080/swagger-ui.html` |
| **Info de la API** | `http://localhost:8080/api/info` |
| **Login** | `http://localhost:8080/auth/login` |
| **Consola H2** | `http://localhost:8080/h2-console` → usar `jdbc:h2:mem:cfcconnect` |
| **Pruebas (12 casos)** | `docs/test-seguridad.http` |

El panel de gestión vive en `src/main/resources/static/` (`index.html`, `app.js`, `app.css`) y lo sirve Spring Boot en la raíz. Desde la Fase 3 **exige autenticación**:

1. Al abrir `http://localhost:8080` se muestra la pantalla de **Iniciar sesión**.
2. Se hace `POST /auth/login` y el **token JWT** se guarda en `localStorage`.
3. A partir de ahí, cada petición del panel incluye la cabecera `Authorization: Bearer <token>`.
4. El botón ⏻ de la barra lateral cierra sesión (borra el token y vuelve al login).
5. Si el token expira o es inválido, la API responde `401` y el panel vuelve a pedir el login.

El control de acceso por rol se nota en la interfaz: con el usuario `RECEPCIONISTA`, los módulos **Usuarios** y **Roles** muestran *"No tienes permisos para esta operacion (se requiere rol ADMIN)"*, porque el backend responde `403`.

> Los recursos estáticos se sirven con `Cache-Control: no-cache` (ver `application.properties`) para que los cambios del frontend se vean al recargar, y además llevan versionado (`/app.js?v=0.3.0`).

> 🔐 Como la API está protegida, el primer paso para probarla es **iniciar sesión**:
>
> ```bash
> curl -X POST http://localhost:8080/auth/login \
>   -H "Content-Type: application/json" \
>   -d '{"email":"admin@cfc.edu.sv","password":"Admin123*"}'
> ```
>
> y usar el `token` devuelto en la cabecera `Authorization: Bearer <token>`.

---

# 🧪 Pruebas

Para ejecutar las pruebas:

```bash
mvn test
```

Actualmente se incluyen pruebas de la capa de servicios y de la seguridad:

| Prueba | Qué verifica |
|---|---|
| `ClienteServiceTest` | Consulta de clientes sembrados en la base de datos |
| `CfcConnectApplicationTests` | Carga del contexto de Spring Boot |
| `JwtServiceTest` | Emisión y validación de tokens, token alterado, expiración y cifrado BCrypt |
| `AuthServiceTest` | Login correcto, contraseña incorrecta y correo inexistente |
| `SecurityIntegrationTest` | `401` sin token, `403` sin rol ADMIN, `200` con token válido y acceso a la documentación |
| `DataSeederTest` | Migración de contraseñas en texto plano (Fase 2) a BCrypt y no duplicación de usuarios |

La ampliación de pruebas unitarias con JUnit 5 y Mockito (casos de éxito y de fallo de negocio para cada módulo) está planificada como siguiente paso.

---

# 🌱 Ramas de Git

Para facilitar el trabajo colaborativo se recomienda utilizar ramas separadas por funcionalidad.

Ejemplo:

```text
main
│
├── develop
│
├── feature/clientes
├── feature/cursos
├── feature/diplomados
├── feature/inscripciones
├── feature/cotizaciones
├── feature/espacios
├── feature/catering
├── feature/pagos
└── feature/seguridad
```

### Ejemplo de creación de una rama

```bash
git checkout -b feature/clientes
```

Agregar los cambios:

```bash
git add .
```

Crear un commit:

```bash
git commit -m "feat: implementar modulo de clientes"
```

Subir la rama:

```bash
git push origin feature/clientes
```

---

# 📝 Convención de commits

Se recomienda utilizar commits descriptivos siguiendo una estructura similar a:

```text
feat: nueva funcionalidad
fix: corrección de error
refactor: modificación de código
test: creación o modificación de pruebas
docs: actualización de documentación
style: cambios de formato
chore: tareas de mantenimiento
```

Ejemplos:

```bash
git commit -m "feat: agregar persistencia JPA al modulo de clientes"
```

```bash
git commit -m "feat: agregar manejo centralizado de excepciones"
```

```bash
git commit -m "docs: actualizar README con avances de la Fase 2"
```

---

# 🔮 Próximas fases

## Fase 3 — Seguridad ✅

* [x] Spring Security.
* [x] Autenticación con JWT.
* [x] Autorización por rol (`ADMIN`, `RECEPCIONISTA`, `CLIENTE`, `CONTABILIDAD`).
* [x] Cifrado de contraseñas con `PasswordEncoder` (BCrypt).
* [x] Protección de endpoints según el rol del usuario autenticado.

## Fase 4 — Pruebas y refinamiento

* Pruebas unitarias completas con JUnit 5 + Mockito (casos de éxito y de fallo de negocio en cada servicio).
* Paginación, filtros y ordenamiento en los listados.
* Relación N:M real entre `Cotizacion` y sus ítems (tabla `detalle_cotizacion`).
* Renovación de tokens (refresh token) y cierre de sesión.

## Fase 5 — Interfaz y despliegue

* Interfaz web (frontend).
* Integración frontend/backend.
* Configuración para producción.
* Despliegue.
* Optimización.

---

# 👥 Equipo de desarrollo

Proyecto académico desarrollado para la gestión de servicios de un centro de formación.

```text
* Enrique Alexander Solano Lopez SL223188
* Adrián Alejandro Jiménez Mena JM242020
* Mario Antonio Rivera Hernandez RH242680
* Sergio Enrique Valencia Rosales VR242686
* Lazaro Moises Vargas Granados VG210810
```

---

# 📄 Estado del proyecto

**Estado:** En desarrollo 🚧

**Fase actual:** Fase 3 completada — Spring Security, cifrado de contraseñas con BCrypt y autenticación JWT.

El proyecto cuenta con los 10 módulos del sistema implementados con arquitectura por capas, persistencia real en base de datos (H2/MySQL), DTOs con validaciones, manejo centralizado de errores, documentación interactiva vía Swagger y **seguridad completa**: autenticación JWT sin estado, contraseñas cifradas con BCrypt y control de acceso por rol (la administración de usuarios y roles exige el rol `ADMIN`). Las siguientes fases incorporarán pruebas unitarias completas con Mockito, paginación y la interfaz web.

---

# 📜 Licencia

Este proyecto fue desarrollado con fines académicos.

© 2026 CFC Connect. Todos los derechos reservados.
