package multi_tenant.pos.handler;

// 403 Acceso denegado - Se usa cuando el usuario está autenticado pero no tiene permisos para acceder al recurso o acción solicitada.
public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String message) {
        super(message);
    }
}
