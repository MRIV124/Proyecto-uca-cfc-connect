package sv.edu.udb.cfcconnect.diplomado.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "diplomados")
public class Diplomado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false)
    private Double precio;

    @Column(nullable = false)
    private Integer cupo;

    @Column(nullable = false)
    private Integer inscritos = 0;

    public Diplomado() {
    }

    /** Regla de negocio: hay cupo si los inscritos aun no alcanzan el limite. */
    public boolean tieneCupoDisponible() {
        int actuales = this.inscritos == null ? 0 : this.inscritos;
        return this.cupo != null && actuales < this.cupo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }

    public Integer getCupo() { return cupo; }
    public void setCupo(Integer cupo) { this.cupo = cupo; }

    public Integer getInscritos() { return inscritos; }
    public void setInscritos(Integer inscritos) { this.inscritos = inscritos; }
}
