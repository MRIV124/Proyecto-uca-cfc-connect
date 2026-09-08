package sv.edu.udb.cfcconnect.usuario.service;

import java.util.List;
import sv.edu.udb.cfcconnect.usuario.dto.UsuarioRequest;
import sv.edu.udb.cfcconnect.usuario.dto.UsuarioResponse;

public interface UsuarioService {
    List<UsuarioResponse> findAll();
    UsuarioResponse findById(Long id);
    UsuarioResponse create(UsuarioRequest request);
    UsuarioResponse update(Long id, UsuarioRequest request);
    void delete(Long id);
}
