# Guía de ejecución y capturas para CFC Connect

## Antes de iniciar

El `pom.xml` requiere Java 21. Maven Wrapper no está incluido, así que usa el
Maven instalado o `mvnw.cmd`.

En IntelliJ:

1. Abre la carpeta del proyecto y espera la importación de Maven.
2. En **File → Project Structure → Project SDK**, elige un JDK 21.
3. En los ajustes de Maven, elige también un JRE compatible (JDK 21).
4. Ejecuta `CfcConnectApplication`.
5. Espera el mensaje de Spring Boot que confirma el arranque.

O en PowerShell, desde la raíz del repositorio:

```powershell
mvn clean test
mvn spring-boot:run
```

La app usa H2 en memoria y `DataSeeder` agrega registros de prueba. No requiere instalar MySQL para la demostración predeterminada.

## ⚠️ Antes de capturar: iniciar sesión

Desde la **Fase 3** toda la API está protegida con JWT, así que el panel web
pide credenciales al abrirse. Usa estos usuarios de prueba:

| Rol | Correo | Contraseña |
|---|---|---|
| ADMIN | `admin@cfc.edu.sv` | `Admin123*` |
| RECEPCIONISTA | `recepcion@cfc.edu.sv` | `Recepcion123*` |

> El usuario `ADMIN` puede ver **Usuarios** y **Roles**. El `RECEPCIONISTA`
> recibe un `403 Forbidden` en esos dos módulos: es la evidencia del control
> de acceso por rol.

## Capturas recomendadas

Guarda cada captura con un nombre claro, por ejemplo `01-arranque.png`. Asegúrate de que se vea la URL/operación y el resultado HTTP cuando corresponda.

1. **Arranque:** consola de IntelliJ con la aplicación iniciada.
2. **Login del panel:** abre `http://localhost:8080/`, captura la pantalla de
   inicio de sesión y luego el panel ya autenticado (aparece el nombre y el
   rol del usuario en la barra lateral).
3. **Panel web:** captura el resumen y, al entrar a cada módulo, las tablas y
   formularios para crear, buscar, editar y eliminar registros.
4. **Swagger:** abre `http://localhost:8080/swagger-ui.html` y captura la lista
   de controladores y rutas. Usa el botón **Authorize** con el token para
   evidenciar que los endpoints protegidos también funcionan desde Swagger.
5. **Base de datos:** abre `http://localhost:8080/h2-console`; conecta con JDBC
   URL `jdbc:h2:mem:cfcconnect`, usuario `sa`, sin contraseña. **Ojo:** el
   formulario viene con `jdbc:h2:~/test` por defecto y hay que reemplazarlo por
   `jdbc:h2:mem:cfcconnect`. Captura las tablas o una consulta SQL. El esquema
   de referencia está en `docs/schema.sql`.
6. **Contraseñas cifradas:** en la consola H2 ejecuta
   `SELECT email, password FROM usuarios;` y captura que los valores empiezan
   por `$2a$10$` (hash BCrypt) y **nunca** son la contraseña en texto plano.
7. **CRUD:** en el panel web, por cada módulo que el equipo presenta, captura crear, listar/buscar, editar y eliminar. Conserva el ID devuelto por la creación para consultar el registro. Swagger permite mostrar los códigos HTTP correspondientes.
8. **Validación:** envía un POST con un campo obligatorio ausente o inválido; captura la respuesta 400 y el detalle JSON.
9. **Error:** solicita un ID que no existe; captura la respuesta 404.
10. **Búsqueda personalizada:** usa `GET /espacios?soloDisponibles=true` y captura la consulta junto con la respuesta. Las funciones de búsqueda de repositorio que no tienen ruta HTTP no se pueden evidenciar desde Swagger.
11. **Módulos:** captura las operaciones específicas disponibles: inscribir participante en curso/diplomado, aprobar/rechazar cotización, reservar/liberar espacio, cambiar estado de inscripción y confirmar pago.
12. **Control de acceso (Fase 3):** con el usuario `RECEPCIONISTA` iniciar sesión, abre el módulo **Usuarios** y captura el mensaje de permisos restringidos; en Swagger, captura el `403` equivalente.
13. **Token expirado o inválido:** en Swagger, llama a `GET /clientes` con `Authorization: Bearer token.invalido` y captura el `401` con el mensaje en JSON.
14. **Git:** en la terminal ejecuta `git branch --all` y `git log --oneline --decorate --all`; captura ambas salidas.

## Estado de verificación

Las pruebas automatizadas se ejecutan con `mvn test` (31 pruebas: servicios,
emisión y validación de tokens, cifrado BCrypt, login, control de acceso por
rol, manejo de excepciones HTTP y migración de contraseñas heredadas). La
aplicación se verificó además de forma manual con el panel web y Swagger.
