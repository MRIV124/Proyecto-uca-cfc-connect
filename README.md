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

La **Fase 1** se enfocó en el análisis, diseño y construcción de la arquitectura base (en memoria). La **Fase 2**, ya completada, incorporó **persistencia real con JPA/Hibernate**, **CRUD completo** en los 10 módulos, **DTOs con validaciones**, **manejo centralizado de excepciones** y **documentación Swagger/OpenAPI**.

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
* [ ] Seguridad y autenticación con Spring Security + JWT (Fase 3).

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
| Spring Security / JWT | Autenticación y autorización           | ⏳ Planificado (Fase 3) |

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
│   │       │   ├── OpenApiConfig.java        # configuracion de Swagger/OpenAPI
│   │       │   └── DataSeeder.java           # carga datos de prueba al iniciar (CommandLineRunner)
│   │       │
│   │       ├── exception/
│   │       │   ├── ApiError.java             # formato estandar de error
│   │       │   ├── GlobalExceptionHandler.java  # @ControllerAdvice
│   │       │   ├── ResourceNotFoundException.java
│   │       │   ├── CupoAgotadoException.java
│   │       │   ├── EspacioOcupadoException.java
│   │       │   └── ReglaNegocioException.java
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
│       └── application-mysql.properties     # perfil MySQL
│
└── test/
    └── java/sv/edu/udb/cfcconnect/...
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
* [x] **CRUD completo** (`GET`, `GET /{id}`, `POST`, `PUT`, `DELETE`) en los 10 módulos, además de endpoints de negocio: inscribir participante, reservar/liberar espacio, aprobar/rechazar cotización y confirmar pago.
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

* [ ] Seguridad y autenticación con Spring Security + JWT.
* [ ] Paginación, filtros y ordenamiento en los listados (`GET`).
* [ ] Pruebas unitarias con Mockito para todos los servicios (casos de éxito y de fallo de negocio).
* [ ] Relación N:M real entre `Cotizacion` y los ítems que incluye (tabla `detalle_cotizacion`).

---

# 🔌 Endpoints disponibles

Todos los endpoints devuelven y reciben JSON. Los que reciben cuerpo (`POST` / `PUT`) validan el DTO con `@Valid` antes de ejecutar la lógica de negocio.

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
| PATCH | `/inscripciones/{id}/estado?estado=CONFIRMADA` | Cambiar estado (`PENDIENTE`, `CONFIRMADA`, `CANCELADA`, `FINALIZADA`) |
| DELETE | `/inscripciones/{id}` | Eliminar una inscripción |

## Cotizaciones — `/cotizaciones`

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/cotizaciones` | Listar todas las cotizaciones |
| GET | `/cotizaciones/{id}` | Obtener una cotización por id |
| POST | `/cotizaciones` | Solicitar una cotización (`clienteId`, `tipo`, `descripcion`, `total`) |
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
| PATCH | `/pagos/{id}/confirmar` | Confirmar el pago (estado → `PAGADO`) |
| DELETE | `/pagos/{id}` | Eliminar un pago |

`tipoReferencia`: `INSCRIPCION`, `COTIZACION`, `ESPACIO`, `CATERING`. `metodo`: `EFECTIVO`, `TARJETA`, `TRANSFERENCIA`, `DEPOSITO`.

## Usuarios — `/usuarios` y Roles — `/roles`

CRUD estándar (`GET`, `GET /{id}`, `POST`, `PUT`, `DELETE`). Un usuario requiere un `rolId` válido; el `password` nunca se devuelve en las respuestas.

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

Al iniciar, el `DataSeeder` carga automáticamente datos de prueba (roles, clientes, cursos, diplomados, espacios y catering) para poder probar la API de inmediato.

---

# 🧪 Pruebas

Para ejecutar las pruebas unitarias:

```bash
mvn test
```

Actualmente se incluyen pruebas básicas para verificar el funcionamiento de la capa de servicios. La ampliación de pruebas unitarias con JUnit 5 y Mockito (casos de éxito y de fallo de negocio para cada módulo) está planificada como siguiente paso.

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

## Fase 3 — Seguridad

* Spring Security.
* Autenticación con JWT.
* Autorización por rol (`ADMIN`, `RECEPCIONISTA`, `CLIENTE`, `CONTABILIDAD`).
* Cifrado de contraseñas con `PasswordEncoder` (BCrypt).
* Protección de endpoints según el rol del usuario autenticado.

## Fase 4 — Pruebas y refinamiento

* Pruebas unitarias completas con JUnit 5 + Mockito (casos de éxito y de fallo de negocio en cada servicio).
* Pruebas de integración de los endpoints.
* Paginación, filtros y ordenamiento en los listados.
* Relación N:M real entre `Cotizacion` y sus ítems (tabla `detalle_cotizacion`).

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

**Fase actual:** Fase 2 completada — Persistencia JPA, CRUD completo, validaciones, manejo de excepciones y Swagger.

El proyecto cuenta con los 10 módulos del sistema implementados con arquitectura por capas, persistencia real en base de datos (H2/MySQL), DTOs con validaciones, manejo centralizado de errores y documentación interactiva vía Swagger. Las siguientes fases incorporarán seguridad (Spring Security + JWT), pruebas unitarias completas y la interfaz web.

---

# 📜 Licencia

Este proyecto fue desarrollado con fines académicos.

© 2026 CFC Connect. Todos los derechos reservados.
