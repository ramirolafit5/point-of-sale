package multi_tenant.pos.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO para la petición de autenticación (login).
 */
@Data
public class AuthenticationRequestDTO {
    @NotBlank(message = "El nombre de usuario no puede estar vacío")
    private String username;
    @NotBlank(message = "La contraseña no puede estar vacía")
    private String password;
}
