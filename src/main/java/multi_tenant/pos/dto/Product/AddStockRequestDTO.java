package multi_tenant.pos.dto.Product;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class AddStockRequestDTO {
    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor que cero")
    @Max(value = 1000, message = "La cantidad no puede superar 1000 unidades")
    private Integer quantity;
}
