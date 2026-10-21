package sv.edu.udb.cfcconnect.seguridad.service;

import sv.edu.udb.cfcconnect.seguridad.dto.AuthRequest;
import sv.edu.udb.cfcconnect.seguridad.dto.AuthResponse;
import sv.edu.udb.cfcconnect.seguridad.dto.SesionResponse;

/**
 * Servicio de autenticacion (Fase 3): valida las credenciales y emite el token
 * JWT correspondiente.
 */
public interface AuthService {

    /** Valida las credenciales y devuelve el token JWT emitido. */
    AuthResponse login(AuthRequest request);

    /** Devuelve los datos del usuario autenticado en la peticion actual. */
    SesionResponse sesionActual();
}
