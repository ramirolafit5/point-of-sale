package multi_tenant.pos.dto.Sale;

import lombok.Data;

@Data
public class SaleItemRequestDTO {
    private Long productId;   // ID del producto a agregar
    private Integer quantity; // Cantidad a vender
}
