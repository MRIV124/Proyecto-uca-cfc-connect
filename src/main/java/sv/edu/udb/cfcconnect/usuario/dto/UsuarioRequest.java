package sv.edu.udb.cfcconnect.usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(name = "UsuarioRequest", description = "Datos para crear o actualizar un usuario")
public class UsuarioRequest {

    @Schema(description = "Nombre completo del usuario", example = "Maria Hernandez", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 120)
    private String nombre;

    @Schema(description = "Correo electronico (identificador de inicio de sesion)",
            example = "maria.hernandez@cfc.edu.sv", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene un formato valido")
    private String email;

    @Schema(description = "Contrasena. Se guarda cifrada con BCrypt, nunca en texto plano.",
            example = "Cliente123*", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "La contrasena es obligatoria")
    @Size(min = 6, message = "La contrasena debe tener al menos 6 caracteres")
    private String password;

    @Schema(description = "Id del rol (1=ADMIN, 2=RECEPCIONISTA, 3=CLIENTE, 4=CONTABILIDAD)",
            example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El rol es obligatorio")
    @Positive
    private Long rolId;

    @Schema(description = "Si el usuario esta activo (inactivo no puede iniciar sesion)", example = "true")
    private Boolean estado = true;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Long getRolId() { return rolId; }
    public void setRolId(Long rolId) { this.rolId = rolId; }

    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }
}
