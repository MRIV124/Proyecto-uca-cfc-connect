package sv.edu.udb.cfcconnect.pago.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public class PagoRequest {

    @NotNull(message = "El cliente es obligatorio")
    @Positive
    private Long clienteId;

    @NotNull(message = "El tipo de referencia es obligatorio")
    @Pattern(regexp = "INSCRIPCION|COTIZACION|ESPACIO|CATERING",
            message = "tipoReferencia debe ser: INSCRIPCION, COTIZACION, ESPACIO o CATERING")
    private String tipoReferencia;

    @NotNull(message = "El id de la referencia es obligatorio")
    @Positive
    private Long referenciaId;

    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser mayor a cero")
    private Double monto;

    @NotNull(message = "El metodo de pago es obligatorio")
    @Pattern(regexp = "EFECTIVO|TARJETA|TRANSFERENCIA|DEPOSITO",
            message = "metodo debe ser: EFECTIVO, TARJETA, TRANSFERENCIA o DEPOSITO")
    private String metodo;

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public String getTipoReferencia() { return tipoReferencia; }
    public void setTipoReferencia(String tipoReferencia) { this.tipoReferencia = tipoReferencia; }

    public Long getReferenciaId() { return referenciaId; }
    public void setReferenciaId(Long referenciaId) { this.referenciaId = referenciaId; }

    public Double getMonto() { return monto; }
    public void setMonto(Double monto) { this.monto = monto; }

    public String getMetodo() { return metodo; }
    public void setMetodo(String metodo) { this.metodo = metodo; }
}
