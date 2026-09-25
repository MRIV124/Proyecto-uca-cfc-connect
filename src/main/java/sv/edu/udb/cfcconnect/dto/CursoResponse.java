package sv.edu.udb.cfcconnect.dto;

import sv.edu.udb.cfcconnect.repository.domain.Curso;

public class CursoResponse {

    private Long id;
    private String nombre;
    private Double precio;
    private Integer cupo;
    private Integer inscritos;
    private Boolean cupoDisponible;

    public static CursoResponse fromEntity(Curso c) {
        CursoResponse r = new CursoResponse();
        r.id = c.getId();
        r.nombre = c.getNombre();
        r.precio = c.getPrecio();
        r.cupo = c.getCupo();
        r.inscritos = c.getInscritos();
        r.cupoDisponible = c.tieneCupoDisponible();
        return r;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public Double getPrecio() { return precio; }
    public Integer getCupo() { return cupo; }
    public Integer getInscritos() { return inscritos; }
    public Boolean getCupoDisponible() { return cupoDisponible; }
}
