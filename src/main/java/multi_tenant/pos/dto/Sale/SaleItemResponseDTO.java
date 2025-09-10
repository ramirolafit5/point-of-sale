package multi_tenant.pos.dto.Sale;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class SaleItemResponseDTO {
    private Long id;              // ID del ítem de venta
    private Long productId;       // ID del producto
    private String productName;   // Nombre del producto
    private Integer quantity;     // Cantidad agregada
    private BigDecimal price; // Precio unitario
    private BigDecimal subtotal;  // quantity * unitPrice
}
