package sv.edu.udb.cfcconnect.usuario.service.implementation;

import java.util.List;
import org.springframework.stereotype.Service;
import sv.edu.udb.cfcconnect.exception.ReglaNegocioException;
import sv.edu.udb.cfcconnect.exception.ResourceNotFoundException;
import sv.edu.udb.cfcconnect.rol.domain.Rol;
import sv.edu.udb.cfcconnect.rol.repository.RolRepository;
import sv.edu.udb.cfcconnect.usuario.domain.Usuario;
import sv.edu.udb.cfcconnect.usuario.dto.UsuarioRequest;
import sv.edu.udb.cfcconnect.usuario.dto.UsuarioResponse;
import sv.edu.udb.cfcconnect.usuario.repository.UsuarioRepository;
import sv.edu.udb.cfcconnect.usuario.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository repository;
    private final RolRepository rolRepository;

    public UsuarioServiceImpl(UsuarioRepository repository, RolRepository rolRepository) {
        this.repository = repository;
        this.rolRepository = rolRepository;
    }

    @Override
    public List<UsuarioResponse> findAll() {
        return repository.findAll().stream().map(UsuarioResponse::fromEntity).toList();
    }

    @Override
    public UsuarioResponse findById(Long id) {
        return UsuarioResponse.fromEntity(getEntity(id));
    }

    @Override
    public UsuarioResponse create(UsuarioRequest request) {
        repository.findByEmailIgnoreCase(request.getEmail()).ifPresent(u -> {
            throw new ReglaNegocioException("Ya existe un usuario registrado con el correo " + request.getEmail());
        });

        Usuario usuario = new Usuario();
        aplicarDatos(usuario, request);
        return UsuarioResponse.fromEntity(repository.save(usuario));
    }

    @Override
    public UsuarioResponse update(Long id, UsuarioRequest request) {
        Usuario usuario = getEntity(id);

        repository.findByEmailIgnoreCase(request.getEmail())
                .filter(u -> !u.getId().equals(id))
                .ifPresent(u -> {
                    throw new ReglaNegocioException("Ya existe otro usuario con el correo " + request.getEmail());
                });

        aplicarDatos(usuario, request);
        return UsuarioResponse.fromEntity(repository.save(usuario));
    }

    @Override
    public void delete(Long id) {
        repository.delete(getEntity(id));
    }

    private void aplicarDatos(Usuario usuario, UsuarioRequest request) {
        Rol rol = rolRepository.findById(request.getRolId())
                .orElseThrow(() -> ResourceNotFoundException.of("Rol", request.getRolId()));

        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(request.getPassword());
        usuario.setRol(rol);
        usuario.setEstado(request.getEstado() == null || request.getEstado());
    }

    private Usuario getEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Usuario", id));
    }
}
