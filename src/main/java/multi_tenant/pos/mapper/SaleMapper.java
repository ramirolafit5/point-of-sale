package multi_tenant.pos.mapper;

import org.mapstruct.Mapper;

import multi_tenant.pos.dto.Sale.SaleResponseDTO;
import multi_tenant.pos.model.Sale;

@Mapper(componentModel = "spring")
public interface SaleMapper {

    SaleResponseDTO toDTO(Sale sale);
}
