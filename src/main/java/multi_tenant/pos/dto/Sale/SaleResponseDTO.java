package multi_tenant.pos.dto.Sale;

import java.math.BigDecimal;

import lombok.Data;
import multi_tenant.pos.model.enums.SaleStatus;

@Data
public class SaleResponseDTO {
    private Long id;
    private SaleStatus status;
    private BigDecimal amount;
}
