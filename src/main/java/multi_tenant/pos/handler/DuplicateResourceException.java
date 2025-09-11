package multi_tenant.pos.handler;

// 409 Conflicto - Se usa cuando la solicitud no se puede completar por un conflicto con el estado actual del recurso.
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
