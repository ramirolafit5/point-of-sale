package multi_tenant.pos.dto.Movement;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class DepositRequestDTO {
    @Positive(message = "El monto debe ser mayor a 0")
    private BigDecimal amount;
    @NotBlank(message = "La razón es obligatoria")
    private String reason;
}
