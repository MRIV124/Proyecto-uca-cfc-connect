package sv.edu.udb.cfcconnect.seguridad.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Respuesta del endpoint {@code POST /auth/login}: contiene el token JWT
 * emitido y los datos minimos del usuario autenticado.
 *
 * <p>El token debe enviarse en las siguientes peticiones dentro de la
 * cabecera {@code Authorization: Bearer <token>}.</p>
 */
@Schema(name = "AuthResponse", description = "Token JWT emitido y datos del usuario")
public class AuthResponse {

    @Schema(description = "Token JWT en formato compacto",
            example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBjZmMuZWR1LnN2In0.8kZ1...")
    private String token;

    @Schema(description = "Tipo de autorizacion a usar en la cabecera", example = "Bearer")
    private String tokenType = "Bearer";

    @Schema(description = "Validez del token en segundos", example = "3600")
    private long expiresIn;

    @Schema(description = "Momento exacto de expiracion del token", example = "2026-10-20T19:35:04")
    private String expiracion;

    @Schema(description = "Correo del usuario autenticado", example = "admin@cfc.edu.sv")
    private String email;

    @Schema(description = "Nombre del usuario autenticado", example = "Administrador CFC")
    private String nombre;

    @Schema(description = "Rol del usuario autenticado", example = "ADMIN",
            allowableValues = {"ADMIN", "RECEPCIONISTA", "CLIENTE", "CONTABILIDAD"})
    private String rol;

    public AuthResponse() {
    }

    public AuthResponse(String token, long expiresIn, String expiracion, String email, String nombre, String rol) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.expiracion = expiracion;
        this.email = email;
        this.nombre = nombre;
        this.rol = rol;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public long getExpiresIn() { return expiresIn; }
    public void setExpiresIn(long expiresIn) { this.expiresIn = expiresIn; }

    public String getExpiracion() { return expiracion; }
    public void setExpiracion(String expiracion) { this.expiracion = expiracion; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}
