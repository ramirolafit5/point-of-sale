package multi_tenant.pos.dto.CashRegisterD;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CashRegisterDTO {
    private Long id;
    private BigDecimal balance;
    private LocalDateTime openedAt;
    private LocalDateTime closedAt;
    private Boolean active;
}
