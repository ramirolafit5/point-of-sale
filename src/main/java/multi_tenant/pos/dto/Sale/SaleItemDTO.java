package multi_tenant.pos.dto.Sale;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class SaleItemDTO {
    private Long id;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subtotal;
}
