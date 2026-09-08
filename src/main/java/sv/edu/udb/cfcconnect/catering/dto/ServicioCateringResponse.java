package sv.edu.udb.cfcconnect.catering.dto;

import sv.edu.udb.cfcconnect.catering.domain.ServicioCatering;

public class ServicioCateringResponse {

    private Long id;
    private String nombre;
    private Double precioPorPersona;

    public static ServicioCateringResponse fromEntity(ServicioCatering s) {
        ServicioCateringResponse r = new ServicioCateringResponse();
        r.id = s.getId();
        r.nombre = s.getNombre();
        r.precioPorPersona = s.getPrecioPorPersona();
        return r;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public Double getPrecioPorPersona() { return precioPorPersona; }
}
