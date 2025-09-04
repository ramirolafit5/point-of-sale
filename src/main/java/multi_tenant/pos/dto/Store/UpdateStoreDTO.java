package multi_tenant.pos.dto.Store;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateStoreDTO {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombreTienda;
}
