package multi_tenant.pos.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import multi_tenant.pos.dto.CashRegisterD.CashRegisterDTO;
import multi_tenant.pos.handler.ConflictException;
import multi_tenant.pos.mapper.CashRegisterMapper;
import multi_tenant.pos.model.CashRegister;
import multi_tenant.pos.model.Store;
import multi_tenant.pos.model.User;
import multi_tenant.pos.repository.CashRegisterRepository;

@Service
public class CashRegisterService {
    private final CashRegisterRepository cashRegisterRepository;
    private final UserService userService;
    private final CashRegisterMapper cashRegisterMapper;

    public CashRegisterService (CashRegisterRepository cashRegisterRepository, UserService userService, CashRegisterMapper cashRegisterMapper) {
        this.cashRegisterRepository = cashRegisterRepository;
        this.userService = userService;
        this.cashRegisterMapper = cashRegisterMapper;
    }

    @Transactional
    public CashRegisterDTO abrirCaja() {
        User currentUser = userService.getCurrentUser();
        Store store = currentUser.getStore();

        // Verificar si ya existe una caja activa para la tienda
        CashRegister cajaActiva = cashRegisterRepository.findByStoreIdAndActiveTrue(store.getId());
        if (cajaActiva != null) {
            throw new ConflictException("Ya existe una caja abierta para esta tienda");
        }

        // Crear la caja con valores iniciales
        CashRegister caja = new CashRegister();
        caja.setStore(store);
        caja.setBalance(BigDecimal.ZERO);
        caja.setActive(true);
        caja.setOpenedAt(LocalDateTime.now());

        CashRegister saved = cashRegisterRepository.save(caja);
        return cashRegisterMapper.toOpenDTO(saved);
    }

    @Transactional
    public CashRegisterDTO cerrarCaja() {
        User currentUser = userService.getCurrentUser();
        Store store = currentUser.getStore();

        // Buscar la caja activa
        CashRegister cajaActiva = cashRegisterRepository.findByStoreIdAndActiveTrue(store.getId());
        if (cajaActiva == null) {
            throw new ConflictException("No hay ninguna caja abierta para esta tienda");
        }

        // Cerrar la caja
        cajaActiva.setActive(false);
        cajaActiva.setClosedAt(LocalDateTime.now());

        CashRegister saved = cashRegisterRepository.save(cajaActiva);
        return cashRegisterMapper.toCloseDTO(saved);
    }
}
