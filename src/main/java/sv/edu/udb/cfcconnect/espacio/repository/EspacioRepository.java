package sv.edu.udb.cfcconnect.espacio.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import sv.edu.udb.cfcconnect.espacio.domain.Espacio;

public interface EspacioRepository extends JpaRepository<Espacio, Long> {
    List<Espacio> findByDisponibleTrue();
}
