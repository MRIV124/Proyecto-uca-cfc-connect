package sv.edu.udb.cfcconnect.seguridad.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Credenciales enviadas por el cliente para iniciar sesion y obtener un token
 * JWT (endpoint {@code POST /auth/login}).
 *
 * <p>Los ejemplos del esquema son reales (son los usuarios de prueba que crea
 * el {@code DataSeeder}), para que en Swagger UI se pueda ejecutar la peticion
 * sin tener que inventar los valores.</p>
 */
@Schema(name = "AuthRequest", description = "Credenciales de inicio de sesion")
public class AuthRequest {

    @Schema(description = "Correo electronico del usuario",
            example = "admin@cfc.edu.sv",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene un formato valido")
    private String email;

    @Schema(description = "Contrasena del usuario (se compara contra el hash BCrypt)",
            example = "Admin123*",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "La contrasena es obligatoria")
    private String password;

    public AuthRequest() {
    }

    public AuthRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
