package multi_tenant.pos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import multi_tenant.pos.dto.Store.CreateStoreDTO;
import multi_tenant.pos.dto.Store.StoreDTO;
import multi_tenant.pos.model.Store;

@Mapper(componentModel = "spring")
public interface StoreMapper {

    @Mapping(target = "id", ignore = true)
    Store fromDTO(CreateStoreDTO createStoreDTO);

    StoreDTO toDTO(Store store);
}
