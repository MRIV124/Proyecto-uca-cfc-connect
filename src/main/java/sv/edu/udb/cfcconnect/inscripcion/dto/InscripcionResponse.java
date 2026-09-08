package sv.edu.udb.cfcconnect.inscripcion.dto;

import java.time.LocalDate;
import sv.edu.udb.cfcconnect.inscripcion.domain.Inscripcion;

public class InscripcionResponse {

    private Long id;
    private Long clienteId;
    private String clienteNombre;
    private Long cursoId;
    private String cursoNombre;
    private LocalDate fecha;
    private String estado;

    public static InscripcionResponse fromEntity(Inscripcion i) {
        InscripcionResponse r = new InscripcionResponse();
        r.id = i.getId();
        r.clienteId = i.getCliente().getId();
        r.clienteNombre = i.getCliente().getNombre();
        r.cursoId = i.getCurso().getId();
        r.cursoNombre = i.getCurso().getNombre();
        r.fecha = i.getFecha();
        r.estado = i.getEstado().name();
        return r;
    }

    public Long getId() { return id; }
    public Long getClienteId() { return clienteId; }
    public String getClienteNombre() { return clienteNombre; }
    public Long getCursoId() { return cursoId; }
    public String getCursoNombre() { return cursoNombre; }
    public LocalDate getFecha() { return fecha; }
    public String getEstado() { return estado; }
}
