package sv.edu.udb.cfcconnect.inscripcion.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class InscripcionRequest {

    @NotNull(message = "El cliente es obligatorio")
    @Positive(message = "El id del cliente debe ser un numero positivo")
    private Long clienteId;

    @NotNull(message = "El curso es obligatorio")
    @Positive(message = "El id del curso debe ser un numero positivo")
    private Long cursoId;

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public Long getCursoId() { return cursoId; }
    public void setCursoId(Long cursoId) { this.cursoId = cursoId; }
}
