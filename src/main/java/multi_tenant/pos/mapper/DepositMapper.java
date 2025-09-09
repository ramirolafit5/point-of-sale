package multi_tenant.pos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import multi_tenant.pos.dto.Movement.DepositRequestDTO;
import multi_tenant.pos.dto.Movement.DepositResponseDTO;
import multi_tenant.pos.model.Deposit;

@Mapper(componentModel = "spring")
public interface DepositMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "cashRegister", ignore = true)
    Deposit toEntity(DepositRequestDTO dto);

    // De entidad a DTO de salida
    @Mapping(target = "user", source = "user.username")
    @Mapping(target = "cashRegister", source = "cashRegister.id")
    DepositResponseDTO toDTO(Deposit deposit);
}
