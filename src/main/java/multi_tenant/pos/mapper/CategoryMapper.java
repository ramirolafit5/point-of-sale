package multi_tenant.pos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import multi_tenant.pos.dto.Category.CategoryResponseDTO;
import multi_tenant.pos.dto.Category.CreateCategoryRequestDTO;
import multi_tenant.pos.dto.Category.UpdateCategoryRequestDTO;
import multi_tenant.pos.model.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "store", ignore = true)
    Category toEntity(CreateCategoryRequestDTO dto);

    @Mapping(source = "store.id", target = "store")
    CategoryResponseDTO toDTO(Category category);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "store", ignore = true)
    void updateEntityFromDto(UpdateCategoryRequestDTO dto, @MappingTarget Category category);
}

