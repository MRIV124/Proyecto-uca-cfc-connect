package sv.edu.udb.cfcconnect.service.implementation;

import org.springframework.stereotype.Service;
import sv.edu.udb.cfcconnect.repository.CursoRepository;
import sv.edu.udb.cfcconnect.repository.domain.Curso;
import sv.edu.udb.cfcconnect.service.CursoService;

import java.util.List;

@Service
public class CursoServiceImpl implements CursoService {

    private final CursoRepository repository;

    public CursoServiceImpl(CursoRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Curso> findAll() {
        return repository.findAll();
    }

    @Override
    public Curso findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Curso save(Curso curso) {
        return repository.save(curso);
    }

    @Override
    public Curso update(Long id, Curso curso) {
        return repository.update(id, curso);
    }

    @Override
    public void delete(Long id) {
        repository.delete(id);
    }
}
