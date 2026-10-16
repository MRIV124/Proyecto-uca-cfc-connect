package sv.edu.udb.cfcconnect.curso.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import java.util.List;
import sv.edu.udb.cfcconnect.curso.dto.CursoRequest;
import sv.edu.udb.cfcconnect.curso.dto.CursoResponse;

public interface CursoService {
    List<CursoResponse> findAll();
    CursoResponse findById(Long id);
    CursoResponse create(CursoRequest request);
    CursoResponse update(Long id, CursoRequest request);
    void delete(Long id);

    /** Caso de exito / caso de fallo por negocio (CupoAgotadoException). */
    CursoResponse inscribirParticipante(Long id);

    /** Busqueda paginada y ordenada por nombre y/o disponibilidad de cupo. */
    Page<CursoResponse> buscar(String nombre, boolean soloConCupo, Pageable pageable);
}
