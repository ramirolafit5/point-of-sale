package multi_tenant.pos.service;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import multi_tenant.pos.dto.Movement.DepositRequestDTO;
import multi_tenant.pos.dto.Movement.DepositResponseDTO;
import multi_tenant.pos.handler.DuplicateResourceException;
import multi_tenant.pos.mapper.DepositMapper;
import multi_tenant.pos.model.CashRegister;
import multi_tenant.pos.model.Deposit;
import multi_tenant.pos.model.Store;
import multi_tenant.pos.model.User;
import multi_tenant.pos.repository.CashRegisterRepository;
import multi_tenant.pos.repository.MovementRepository;

@Service
public class DepositService {
    
    private final MovementRepository movementRepository;
    private final CashRegisterRepository cashRegisterRepository;
    private final UserService userService;
    private final DepositMapper depositMapper;

    public DepositService (MovementRepository movementRepository, CashRegisterRepository cashRegisterRepository, UserService userService, DepositMapper depositMapper){
        this.movementRepository = movementRepository;
        this.cashRegisterRepository = cashRegisterRepository;
        this.userService = userService;
        this.depositMapper = depositMapper;
    }

    @Transactional
    public DepositResponseDTO registrarDeposito(DepositRequestDTO dto) {
        User currentUser = userService.getCurrentUser();
        Store store = currentUser.getStore();

        CashRegister caja = cashRegisterRepository.findByStoreIdAndActiveTrue(store.getId());
        if (caja == null) {
            throw new DuplicateResourceException("No hay caja activa para la tienda");
        }

        Deposit deposit = depositMapper.toEntity(dto);
        deposit.setCashRegister(caja);
        deposit.setUser(currentUser);

        deposit.impactCashRegister();

        Deposit saved = movementRepository.save(deposit);

        return depositMapper.toDTO(saved);
    }
}
