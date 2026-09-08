package sv.edu.udb.cfcconnect.inscripcion.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import sv.edu.udb.cfcconnect.inscripcion.domain.Inscripcion;

public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {
    List<Inscripcion> findByClienteId(Long clienteId);
    List<Inscripcion> findByCursoId(Long cursoId);
}
