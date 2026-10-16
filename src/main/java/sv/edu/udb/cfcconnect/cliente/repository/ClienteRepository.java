package sv.edu.udb.cfcconnect.cliente.repository;

import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import sv.edu.udb.cfcconnect.cliente.domain.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByCorreoIgnoreCase(String correo);

    /** Busqueda por nombre y/o correo (coincidencia parcial, sin distinguir mayusculas). "" = sin filtro. */
    @Query("select c from Cliente c where (:nombre = '' or lower(c.nombre) like lower(concat('%', :nombre, '%'))) "
            + "and (:correo = '' or lower(c.correo) like lower(concat('%', :correo, '%')))")
    Page<Cliente> buscar(@Param("nombre") String nombre, @Param("correo") String correo, Pageable pageable);
}
