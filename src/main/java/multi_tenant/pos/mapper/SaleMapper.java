package multi_tenant.pos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import multi_tenant.pos.dto.Sale.SaleItemResponseDTO;
import multi_tenant.pos.dto.Sale.SaleResponseDTO;
import multi_tenant.pos.model.Sale;
import multi_tenant.pos.model.SaleItem;

@Mapper(componentModel = "spring")
public interface SaleMapper {

    SaleResponseDTO toDTO(Sale sale);

    // Convierte la entidad SaleItem a un DTO de respuesta
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    SaleItemResponseDTO toDto(SaleItem saleItem);

    /* SaleItem toEntity(SaleItemRequestDTO dto); */
}
