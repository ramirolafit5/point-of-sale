package multi_tenant.pos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import multi_tenant.pos.model.enums.Rol;

@Data
public class RegisterUserDTO {
    @NotBlank(message = "El nombre de usuario no puede estar vacío")
    @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
    private String username;
    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 6, max = 100, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;
    @NotNull(message = "El rol no puede ser nulo")
    private Rol rol;
    @NotNull(message = "storeId no puede ser nulo")
    private Long storeId;

}
