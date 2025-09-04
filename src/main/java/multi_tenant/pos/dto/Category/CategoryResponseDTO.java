package multi_tenant.pos.dto.Category;

import lombok.Data;

@Data
public class CategoryResponseDTO {
    private Long id;
    private String name;
    private Long store;
}
