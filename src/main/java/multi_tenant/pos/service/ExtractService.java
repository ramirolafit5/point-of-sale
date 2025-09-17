package multi_tenant.pos.service;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import multi_tenant.pos.dto.Movement.ExtractionRequestDTO;
import multi_tenant.pos.dto.Movement.ExtractionResponseDTO;
import multi_tenant.pos.handler.BadRequestException;
import multi_tenant.pos.handler.DuplicateResourceException;
import multi_tenant.pos.mapper.ExtractionMapper;
import multi_tenant.pos.model.CashRegister;
import multi_tenant.pos.model.Extraction;
import multi_tenant.pos.model.Store;
import multi_tenant.pos.model.User;
import multi_tenant.pos.repository.CashRegisterRepository;
import multi_tenant.pos.repository.MovementRepository;

@Service
public class ExtractService {
    private final MovementRepository movementRepository;
    private final CashRegisterRepository cashRegisterRepository;
    private final UserService userService;
    private final ExtractionMapper extractionMapper;

    public ExtractService (MovementRepository movementRepository, CashRegisterRepository cashRegisterRepository, UserService userService, ExtractionMapper extractionMapper){
        this.movementRepository = movementRepository;
        this.cashRegisterRepository = cashRegisterRepository;
        this.userService = userService;
        this.extractionMapper = extractionMapper;
    }

    @Transactional
    public ExtractionResponseDTO registrarExtraccion(ExtractionRequestDTO dto) {
        User currentUser = userService.getCurrentUser();
        Store store = currentUser.getStore();

        CashRegister caja = cashRegisterRepository.findByStoreIdAndActiveTrue(store.getId());
        if (caja == null) {
            throw new DuplicateResourceException("No hay caja activa para la tienda");
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
}
