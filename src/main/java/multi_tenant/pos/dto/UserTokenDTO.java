package multi_tenant.pos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTokenDTO {
    private Long id;
    private String username;
    private String rol;  // Roles como strings, p. ej: ["ROLE_ADMIN", "ROLE_USER"]
    private String token;  // Campo para guardar el JWT
}
