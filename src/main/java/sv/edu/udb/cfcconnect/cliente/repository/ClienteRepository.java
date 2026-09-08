package sv.edu.udb.cfcconnect.cliente.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import sv.edu.udb.cfcconnect.cliente.domain.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByCorreoIgnoreCase(String correo);
}
