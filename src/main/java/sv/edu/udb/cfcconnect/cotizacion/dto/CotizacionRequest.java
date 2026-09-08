package sv.edu.udb.cfcconnect.cotizacion.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class CotizacionRequest {

    @NotNull(message = "El cliente es obligatorio")
    @Positive
    private Long clienteId;

    @NotNull(message = "El tipo es obligatorio")
    @Pattern(regexp = "CURSO|DIPLOMADO|ESPACIO|CATERING|COMBINADO",
            message = "El tipo debe ser: CURSO, DIPLOMADO, ESPACIO, CATERING o COMBINADO")
    private String tipo;

    @Size(max = 255, message = "La descripcion no puede exceder 255 caracteres")
    private String descripcion;

    @NotNull(message = "El total es obligatorio")
    @PositiveOrZero(message = "El total no puede ser negativo")
    private Double total;

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }
}
