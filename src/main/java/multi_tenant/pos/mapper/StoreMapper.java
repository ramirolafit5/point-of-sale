package multi_tenant.pos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import multi_tenant.pos.dto.Store.CreateStoreDTO;
import multi_tenant.pos.dto.Store.StoreResponseDTO;
import multi_tenant.pos.dto.Store.UpdateStoreDTO;
import multi_tenant.pos.model.Store;

@Mapper(componentModel = "spring")
public interface StoreMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "cashRegisters", ignore = true)
    Store toEntity(CreateStoreDTO dto);

    StoreResponseDTO toDTO(Store store);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "cashRegisters", ignore = true)
    Store updateToEntity(UpdateStoreDTO dto, @MappingTarget Store store);
}