package multi_tenant.pos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import multi_tenant.pos.dto.RegisterUserDTO;
import multi_tenant.pos.dto.UserDTO;
import multi_tenant.pos.dto.UserTokenDTO;
import multi_tenant.pos.model.User;
import multi_tenant.pos.model.enums.Rol;

@Mapper(componentModel = "spring")
public interface UserMapper {
    /* 
     * Aca no se hacen mappings prq la entidad destino tiene menos campos que la origen
     */
    UserDTO toDTO(User user); // Convierte entidad User → DTO (para responses)

    /* 
     * El método fromDTO convierte un RegisterUserDTO (el origen) en una entidad User (el destino).
     * Entonces si la clase de destino tiene más campos que la clase de origen, necesitas una aclaración.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "store", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    User fromDTO(RegisterUserDTO dto); // Convierte DTO de registro → User (para persistir)

    /* 
     * Explicacion similar a la anterior
     */
    @Mapping(source = "rol", target = "rol", qualifiedByName = "rolToString")
    @Mapping(target = "token", ignore = true)
    UserTokenDTO toUserTokenDTO(User user);

    @Named("rolToString") // <-- Aquí se define el nombre del método
    default String rolToString(Rol rol) {
        return rol.name();
    }
}
