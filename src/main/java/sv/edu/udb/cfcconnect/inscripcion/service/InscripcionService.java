package sv.edu.udb.cfcconnect.inscripcion.service;

import java.util.List;
import sv.edu.udb.cfcconnect.inscripcion.dto.InscripcionRequest;
import sv.edu.udb.cfcconnect.inscripcion.dto.InscripcionResponse;

public interface InscripcionService {
    List<InscripcionResponse> findAll();
    InscripcionResponse findById(Long id);
    InscripcionResponse create(InscripcionRequest request);
    InscripcionResponse update(Long id, InscripcionRequest request);
    InscripcionResponse cambiarEstado(Long id, String estado);
    void delete(Long id);
}
