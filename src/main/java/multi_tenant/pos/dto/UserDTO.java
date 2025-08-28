package multi_tenant.pos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import multi_tenant.pos.model.enums.Rol;

@Data
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private Rol rol;
}
