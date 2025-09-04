package multi_tenant.pos.dto.Store;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class StoreResponseDTO {
    private Long id;
    private String nombreTienda;
    private LocalDateTime fechaCreacion;
}
