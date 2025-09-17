package multi_tenant.pos.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import multi_tenant.pos.dto.Product.AddStockRequestDTO;
import multi_tenant.pos.dto.Product.CreateProductRequestDTO;
import multi_tenant.pos.dto.Product.ProductResponseDTO;
import multi_tenant.pos.dto.Product.UpdateProductRequestDTO;
import multi_tenant.pos.model.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    /* 
     * AQUI USAMOS LA FORMA INVERSA A LA QUE USAMOS EN CATEGORY, ESTA BUENO SABER AMBAS !
     */

    
    //El ignoreByDefault ignora el mappeo de category entre tipo Long y Category pero lo controlamos 
    //manualmente en el servicio. Ademas de ignorar aquellos que estan en la entidad pero no vienen 
    //en el dto
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "active", source = "active")
    //Los demas se ignoran prq se controlan manualmente en el servicio
    Product toEntity(CreateProductRequestDTO dto);

    // Map de entidad a DTO de salida
    //Mapeamos manualmente uno por uno los que seran devueltos en el dto
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "active", source = "active")
    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "category", source = "category.id")
    ProductResponseDTO toDTO(Product product);

    // Map de DTO de actualización a entidad
    //Mapeamos uno por uno lo que podra ser modificado
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "active", source = "active")
    void updateEntityFromDto(UpdateProductRequestDTO dto, @MappingTarget Product product);

    /* aca uso la forma de category (mas sencilla a mi gusto) */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "sku", ignore = true)
    @Mapping(target = "price", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "store", ignore = true)
    @Mapping(target = "category", ignore = true)
    Product stockToEntity(AddStockRequestDTO dto);
}
