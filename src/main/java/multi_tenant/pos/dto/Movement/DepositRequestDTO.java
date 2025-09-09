package multi_tenant.pos.dto.Movement;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class DepositRequestDTO {
    private BigDecimal amount;
    private String reason;
}
