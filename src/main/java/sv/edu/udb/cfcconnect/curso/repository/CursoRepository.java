package sv.edu.udb.cfcconnect.curso.repository;

import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import sv.edu.udb.cfcconnect.curso.domain.Curso;

public interface CursoRepository extends JpaRepository<Curso, Long> {

    /**
     * Busqueda por filtros con paginacion y ordenamiento.
     * nombre: coincidencia parcial sin distinguir mayusculas ("" = sin filtro).
     * soloConCupo: si es true, devuelve solo los que aun tienen cupo disponible.
     */
    @Query("select e from Curso e where lower(e.nombre) like lower(concat('%', :nombre, '%')) "
            + "and (:soloConCupo = false or e.inscritos < e.cupo)")
    Page<Curso> buscar(@Param("nombre") String nombre,
                       @Param("soloConCupo") boolean soloConCupo,
                       Pageable pageable);
}
