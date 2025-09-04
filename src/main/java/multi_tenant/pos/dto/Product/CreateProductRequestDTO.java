package multi_tenant.pos.dto.Product;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateProductRequestDTO {
    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @Size(max = 200, message = "La descripción no puede superar 200 caracteres")
    private String description;

    @PositiveOrZero(message = "El precio debe ser mayor o igual a 0")
    private BigDecimal price;

    private Boolean active = true;

    private Long category;
}
