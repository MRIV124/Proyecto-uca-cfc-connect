package sv.edu.udb.cfcconnect.rol.service;

import java.util.List;
import sv.edu.udb.cfcconnect.rol.dto.RolRequest;
import sv.edu.udb.cfcconnect.rol.dto.RolResponse;

public interface RolService {
    List<RolResponse> findAll();
    RolResponse findById(Long id);
    RolResponse create(RolRequest request);
    RolResponse update(Long id, RolRequest request);
    void delete(Long id);
}
