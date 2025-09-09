package multi_tenant.pos.service;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import multi_tenant.pos.dto.Movement.DepositRequestDTO;
import multi_tenant.pos.dto.Movement.DepositResponseDTO;
import multi_tenant.pos.dto.Movement.ExtractionRequestDTO;
import multi_tenant.pos.dto.Movement.ExtractionResponseDTO;
import multi_tenant.pos.handler.BadRequestException;
import multi_tenant.pos.handler.ConflictException;
import multi_tenant.pos.mapper.DepositMapper;
import multi_tenant.pos.mapper.ExtractionMapper;
import multi_tenant.pos.model.CashRegister;
import multi_tenant.pos.model.Deposit;
import multi_tenant.pos.model.Extraction;
import multi_tenant.pos.model.Store;
import multi_tenant.pos.model.User;
import multi_tenant.pos.repository.CashRegisterRepository;
import multi_tenant.pos.repository.MovementRepository;

@Service
public class MovementService {

    private final MovementRepository movementRepository;
    private final CashRegisterRepository cashRegisterRepository;
    private final UserService userService;
    private final ExtractionMapper extractionMapper;
    private final DepositMapper depositMapper;

    public MovementService (MovementRepository movementRepository, CashRegisterRepository cashRegisterRepository, UserService userService, ExtractionMapper extractionMapper, DepositMapper depositMapper){
        this.movementRepository = movementRepository;
        this.cashRegisterRepository = cashRegisterRepository;
        this.userService = userService;
        this.extractionMapper = extractionMapper;
        this.depositMapper = depositMapper;
    }

    @Transactional
    public ExtractionResponseDTO registrarExtraccion(ExtractionRequestDTO dto) {
        User currentUser = userService.getCurrentUser();
        Store store = currentUser.getStore();

        CashRegister caja = cashRegisterRepository.findByStoreIdAndActiveTrue(store.getId());
        if (caja == null) {
            throw new ConflictException("No hay caja activa para la tienda");
        }

        /* Usamos compare prq estamos usando bigdecimal para el ingreso de los datos en el dto */
        if (dto.getAmount().compareTo(caja.getBalance()) > 0) {
            throw new BadRequestException("El monto excede el balance de la caja");
        }

        Extraction extraccion = extractionMapper.toEntity(dto);
        extraccion.setCashRegister(caja);
        extraccion.setUser(currentUser);

        extraccion.impactCashRegister();

        Extraction saved = movementRepository.save(extraccion);

        return extractionMapper.toDTO(saved);
    }

    @Transactional
    public DepositResponseDTO registrarDeposito(DepositRequestDTO dto) {
        User currentUser = userService.getCurrentUser();
        Store store = currentUser.getStore();

        CashRegister caja = cashRegisterRepository.findByStoreIdAndActiveTrue(store.getId());
        if (caja == null) {
            throw new ConflictException("No hay caja activa para la tienda");
        }

        Deposit deposit = depositMapper.toEntity(dto);
        deposit.setCashRegister(caja);
        deposit.setUser(currentUser);

        deposit.impactCashRegister();

        Deposit saved = movementRepository.save(deposit);

        return depositMapper.toDTO(saved);
    }

}

































/* @Service
public class MovimientoService {

    @Autowired
    private MovementRepository movementRepository;

    @Transactional
    public Movement registrarMovimiento(Movement movement) {
        CashRegister cashRegister = movement.getCashRegister();
        if (!cashRegister.getActive()) {
            throw new IllegalStateException("No hay caja activa");
        }

        // Polimorfismo: cada tipo de movement sabe cómo impactar la caja
        movement.impactCashRegister();

        return movementRepository.save(movement);
    }
} */
