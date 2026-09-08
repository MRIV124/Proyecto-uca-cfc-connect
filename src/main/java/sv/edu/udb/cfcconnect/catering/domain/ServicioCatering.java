package sv.edu.udb.cfcconnect.catering.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "servicios_catering")
public class ServicioCatering {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Coffee Break, Desayuno, Almuerzo, Cena, Refrigerio
    @Column(nullable = false, length = 80)
    private String nombre;

    @Column(name = "precio_por_persona", nullable = false)
    private Double precioPorPersona;

    public ServicioCatering() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Double getPrecioPorPersona() { return precioPorPersona; }
    public void setPrecioPorPersona(Double precioPorPersona) { this.precioPorPersona = precioPorPersona; }
}
