package multi_tenant.pos.dto.Movement;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ExtractionResponseDTO {
    private Long id;
    private BigDecimal amount;
    private LocalDateTime createdAt;
    private String reason;
    private String user;
    private Long cashRegister;
}
