package sv.edu.udb.cfcconnect.cotizacion.dto;

import java.time.LocalDate;
import sv.edu.udb.cfcconnect.cotizacion.domain.Cotizacion;

public class CotizacionResponse {

    private Long id;
    private Long clienteId;
    private String clienteNombre;
    private String tipo;
    private String descripcion;
    private Double total;
    private String estado;
    private LocalDate fecha;

    public static CotizacionResponse fromEntity(Cotizacion c) {
        CotizacionResponse r = new CotizacionResponse();
        r.id = c.getId();
        r.clienteId = c.getCliente().getId();
        r.clienteNombre = c.getCliente().getNombre();
        r.tipo = c.getTipo().name();
        r.descripcion = c.getDescripcion();
        r.total = c.getTotal();
        r.estado = c.getEstado().name();
        r.fecha = c.getFecha();
        return r;
    }

    public Long getId() { return id; }
    public Long getClienteId() { return clienteId; }
    public String getClienteNombre() { return clienteNombre; }
    public String getTipo() { return tipo; }
    public String getDescripcion() { return descripcion; }
    public Double getTotal() { return total; }
    public String getEstado() { return estado; }
    public LocalDate getFecha() { return fecha; }
}
