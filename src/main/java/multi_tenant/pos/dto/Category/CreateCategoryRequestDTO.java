package multi_tenant.pos.dto.Category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateCategoryRequestDTO {
    @NotBlank(message = "El nombre es obligatorio")
    private String name;
    @Size(max = 200, message = "La descripción no puede superar 200 caracteres")
    private String description;
}