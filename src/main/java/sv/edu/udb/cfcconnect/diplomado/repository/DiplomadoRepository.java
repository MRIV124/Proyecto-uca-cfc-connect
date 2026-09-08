package sv.edu.udb.cfcconnect.diplomado.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.edu.udb.cfcconnect.diplomado.domain.Diplomado;

public interface DiplomadoRepository extends JpaRepository<Diplomado, Long> {
}
