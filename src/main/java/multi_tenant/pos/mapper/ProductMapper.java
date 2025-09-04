package multi_tenant.pos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import multi_tenant.pos.dto.Product.CreateProductRequestDTO;
import multi_tenant.pos.dto.Product.ProductResponseDTO;
import multi_tenant.pos.dto.Product.UpdateProductRequestDTO;
import multi_tenant.pos.model.Category;
import multi_tenant.pos.model.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    // De DTO de entrada a entidad
    @Mapping(target = "store", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sku", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "quantity", ignore = true)
    @Mapping(target = "category", ignore = true)
    Product toEntity(CreateProductRequestDTO dto);

    // De entidad a DTO de salida
    ProductResponseDTO toDTO(Product product);

    @Mapping(target = "store", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sku", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "quantity", ignore = true)
    @Mapping(target = "category", ignore = true)
    void updateEntityFromDto(UpdateProductRequestDTO dto, @MappingTarget Product product);


   /*  
    Esto quita la sugerencia del toDTO pero segun IA es recomendable hacer un ignore
    y manejar esto desde el servicio. Si lo manejamos con un ignore entonces veremos
    que en la salida del postman nos aparece como null el id, en cambio si hacemos
    el default nos aparecerá correcta la vinculacion.
    Por lo tanto lo usare para ver la referencia en la salida en postman
    */
    default Long mapCategoryToId(Category category) {
        return category != null ? category.getId() : null;
    } 
}
