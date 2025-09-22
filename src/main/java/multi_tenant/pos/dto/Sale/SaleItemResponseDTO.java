package multi_tenant.pos.dto.Sale;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class SaleItemResponseDTO {
    private Long id;
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subtotal;
}
