package sv.edu.udb.cfcconnect.diplomado.dto;

import sv.edu.udb.cfcconnect.diplomado.domain.Diplomado;

public class DiplomadoResponse {

    private Long id;
    private String nombre;
    private Double precio;
    private Integer cupo;
    private Integer inscritos;
    private Boolean cupoDisponible;

    public static DiplomadoResponse fromEntity(Diplomado d) {
        DiplomadoResponse r = new DiplomadoResponse();
        r.id = d.getId();
        r.nombre = d.getNombre();
        r.precio = d.getPrecio();
        r.cupo = d.getCupo();
        r.inscritos = d.getInscritos();
        r.cupoDisponible = d.tieneCupoDisponible();
        return r;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public Double getPrecio() { return precio; }
    public Integer getCupo() { return cupo; }
    public Integer getInscritos() { return inscritos; }
    public Boolean getCupoDisponible() { return cupoDisponible; }
}
