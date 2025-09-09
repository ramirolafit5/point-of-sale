package multi_tenant.pos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import multi_tenant.pos.dto.Movement.ExtractionRequestDTO;
import multi_tenant.pos.dto.Movement.ExtractionResponseDTO;
import multi_tenant.pos.model.Extraction;

@Mapper(componentModel = "spring")
public interface ExtractionMapper {
    // De DTO de entrada a entidad

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "cashRegister", ignore = true)
    Extraction toEntity(ExtractionRequestDTO dto);

    // De entidad a DTO de salida
    @Mapping(target = "user", source = "user.username")
    @Mapping(target = "cashRegister", source = "cashRegister.id")
    ExtractionResponseDTO toDTO(Extraction extraction);
}
