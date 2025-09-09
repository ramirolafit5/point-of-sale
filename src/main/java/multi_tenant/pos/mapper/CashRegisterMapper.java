package multi_tenant.pos.mapper;

import org.mapstruct.Mapper;

import multi_tenant.pos.dto.CashRegisterD.CashRegisterDTO;
import multi_tenant.pos.model.CashRegister;

@Mapper(componentModel = "spring")
public interface CashRegisterMapper {
    CashRegisterDTO toOpenDTO(CashRegister cashRegister);

    CashRegisterDTO toCloseDTO(CashRegister cashRegister);
}
