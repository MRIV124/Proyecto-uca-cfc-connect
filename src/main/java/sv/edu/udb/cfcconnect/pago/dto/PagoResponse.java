package sv.edu.udb.cfcconnect.pago.dto;

import java.time.LocalDate;
import sv.edu.udb.cfcconnect.pago.domain.Pago;

public class PagoResponse {

    private Long id;
    private Long clienteId;
    private String clienteNombre;
    private String tipoReferencia;
    private Long referenciaId;
    private Double monto;
    private String metodo;
    private String estado;
    private LocalDate fecha;

    public static PagoResponse fromEntity(Pago p) {
        PagoResponse r = new PagoResponse();
        r.id = p.getId();
        r.clienteId = p.getCliente().getId();
        r.clienteNombre = p.getCliente().getNombre();
        r.tipoReferencia = p.getTipoReferencia().name();
        r.referenciaId = p.getReferenciaId();
        r.monto = p.getMonto();
        r.metodo = p.getMetodo().name();
        r.estado = p.getEstado().name();
        r.fecha = p.getFecha();
        return r;
    }

    public Long getId() { return id; }
    public Long getClienteId() { return clienteId; }
    public String getClienteNombre() { return clienteNombre; }
    public String getTipoReferencia() { return tipoReferencia; }
    public Long getReferenciaId() { return referenciaId; }
    public Double getMonto() { return monto; }
    public String getMetodo() { return metodo; }
    public String getEstado() { return estado; }
    public LocalDate getFecha() { return fecha; }
}
