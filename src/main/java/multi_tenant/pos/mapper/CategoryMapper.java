package multi_tenant.pos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import multi_tenant.pos.dto.Category.CategoryResponseDTO;
import multi_tenant.pos.dto.Category.CreateCategoryRequestDTO;
import multi_tenant.pos.dto.Category.UpdateCategoryRequestDTO;
import multi_tenant.pos.model.Category;
import multi_tenant.pos.model.Store;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "store", ignore = true)
    Category toEntity(CreateCategoryRequestDTO dto);

    CategoryResponseDTO toDTO(Category category);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "store", ignore = true)
    void updateEntityFromDto(UpdateCategoryRequestDTO dto, @MappingTarget Category category);

    /*  
    Esto quita la sugerencia del toDTO pero segun IA es recomendable hacer un ignore
    y manejar esto desde el servicio. Si lo manejamos con un ignore entonces veremos
    que en la salida del postman nos aparece como null el id, en cambio si hacemos
    el default nos aparecerá correcta la vinculacion.
    Por lo tanto lo usare para ver la referencia en la salida en postman
    */
    default Long mapStoreToId(Store store) { // Para quitar la sugerencia del toDTO
        return store != null ? store.getId() : null;
    }
}
