package sv.edu.udb.cfcconnect.repository.domain;

public class Curso {

    private Long id;
    private String nombre;
    private Double precio;
    private Integer cupo;
    private Integer inscritos;

    // Constructor vacío
    public Curso() {
        this.inscritos = 0;
    }

    // Constructor completo
    public Curso(Long id, String nombre, Double precio, Integer cupo) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.cupo = cupo;
        this.inscritos = 0;
    }

    // Builder
    public static CursoBuilder builder() {
        return new CursoBuilder();
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Integer getCupo() {
        return cupo;
    }

    public void setCupo(Integer cupo) {
        this.cupo = cupo;
    }

    public Integer getInscritos() {
        return inscritos;
    }

    public void setInscritos(Integer inscritos) {
        this.inscritos = inscritos;
    }

    /**
     * Verifica si el curso todavía tiene cupos disponibles.
     *
     * @return true si hay cupos disponibles, false si el curso está lleno.
     */
    public Boolean tieneCupoDisponible() {
        if (cupo == null) {
            return false;
        }

        if (inscritos == null) {
            return true;
        }

        return inscritos < cupo;
    }

    // Builder
    public static class CursoBuilder {

        private Long id;
        private String nombre;
        private Double precio;
        private Integer cupo;
        private Integer inscritos = 0;

        public CursoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public CursoBuilder nombre(String nombre) {
            this.nombre = nombre;
            return this;
        }

        public CursoBuilder precio(Double precio) {
            this.precio = precio;
            return this;
        }

        public CursoBuilder cupo(Integer cupo) {
            this.cupo = cupo;
            return this;
        }

        public CursoBuilder inscritos(Integer inscritos) {
            this.inscritos = inscritos;
            return this;
        }

        public Curso build() {
            Curso curso = new Curso(id, nombre, precio, cupo);
            curso.setInscritos(inscritos);
            return curso;
        }
    }
}
