package multi_tenant.pos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import multi_tenant.pos.dto.RegisterUserDTO;
import multi_tenant.pos.dto.UserDTO;
import multi_tenant.pos.dto.UserTokenDTO;
import multi_tenant.pos.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    /* 
     * El método fromDTO convierte un RegisterUserDTO (el origen) en una entidad User (el destino).
     * Entonces si la clase de destino tiene más campos que la clase de origen, necesitas una aclaración.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "store", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    User fromDTO(RegisterUserDTO dto); // Convierte DTO de registro → User (para persistir)

    /* 
     * Aca no se hacen mappings prq la entidad destino tiene menos campos que la origen
     */
    UserDTO toDTO(User user); // Convierte entidad User → DTO (para responses)

    // Mapear User → UserTokenDTO, enum Rol → String automáticamente
    @Mapping(target = "rol", source = "rol") // MapStruct convierte Rol.name() a String
    @Mapping(target = "token", ignore = true)
    UserTokenDTO toUserTokenDTO(User user);
}
