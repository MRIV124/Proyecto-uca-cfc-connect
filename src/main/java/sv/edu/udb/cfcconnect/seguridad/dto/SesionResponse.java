package sv.edu.udb.cfcconnect.seguridad.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * Datos de la sesion activa del usuario autenticado (endpoint
 * {@code GET /auth/me}). Permite verificar a que endpoints se tiene acceso
 * con el token vigente.
 */
@Schema(name = "SesionResponse", description = "Datos de la sesion activa")
public class SesionResponse {

    @Schema(description = "Correo del usuario autenticado", example = "admin@cfc.edu.sv")
    private String email;

    @Schema(description = "Nombre del usuario autenticado", example = "Administrador CFC")
    private String nombre;

    @Schema(description = "Rol del usuario autenticado", example = "ADMIN")
    private String rol;

    @Schema(description = "Autoridades (roles) del usuario", example = "[\"ROLE_ADMIN\"]")
    private List<String> autoridades;

    public SesionResponse() {
    }

    public SesionResponse(String email, String nombre, String rol, List<String> autoridades) {
        this.email = email;
        this.nombre = nombre;
        this.rol = rol;
        this.autoridades = autoridades;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public List<String> getAutoridades() { return autoridades; }
    public void setAutoridades(List<String> autoridades) { this.autoridades = autoridades; }
}
