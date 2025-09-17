package multi_tenant.pos.dto.Sale;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class SaleDTO {
    private Long id;
    private LocalDateTime date;
    private String status;
    private BigDecimal totalAmount;
    private List<SaleItemDTO> items;
}
