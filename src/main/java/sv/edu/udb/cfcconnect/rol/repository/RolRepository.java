package sv.edu.udb.cfcconnect.rol.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import sv.edu.udb.cfcconnect.rol.domain.Rol;

public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByNombreIgnoreCase(String nombre);
}
