package multi_tenant.pos.dto.Store;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ConfirmPasswordToDeleteDTO {
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}
