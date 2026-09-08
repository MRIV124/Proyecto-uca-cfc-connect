package sv.edu.udb.cfcconnect.curso.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.edu.udb.cfcconnect.curso.domain.Curso;

public interface CursoRepository extends JpaRepository<Curso, Long> {
}
