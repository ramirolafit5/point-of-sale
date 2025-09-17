package multi_tenant.pos.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import multi_tenant.pos.dto.Sale.SaleDTO;
import multi_tenant.pos.dto.Sale.SaleItemDTO;
import multi_tenant.pos.dto.Sale.SaleResponseDTO;
import multi_tenant.pos.handler.DuplicateResourceException;
import multi_tenant.pos.handler.ResourceNotFoundException;
import multi_tenant.pos.mapper.SaleMapper;
import multi_tenant.pos.model.CashRegister;
import multi_tenant.pos.model.Sale;
import multi_tenant.pos.model.User;
import multi_tenant.pos.model.enums.SaleStatus;
import multi_tenant.pos.repository.CashRegisterRepository;
import multi_tenant.pos.repository.SaleRepository;

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final UserService userService;
    private final CashRegisterRepository cashRegisterRepository;
    private final SaleMapper saleMapper;

    public SaleService(SaleRepository saleRepository, UserService userService, CashRegisterRepository cashRegisterRepository, SaleMapper saleMapper) {
        this.saleRepository = saleRepository;
        this.userService = userService;
        this.cashRegisterRepository = cashRegisterRepository;
        this.saleMapper = saleMapper;
    }

    @Transactional
    public SaleResponseDTO crearVenta() {
        User currentUser = userService.getCurrentUser();

        CashRegister activeCashRegister = cashRegisterRepository
                .findByStoreIdAndActiveTrue(currentUser.getStore().getId());

        if (activeCashRegister == null) {
            throw new IllegalStateException("No hay caja activa para esta tienda");
        }

        // Verificar si ya existe una venta pendiente para esa caja
        Optional<Sale> pendingSale = saleRepository.findByCashRegisterIdAndStatus(
                activeCashRegister.getId(),
                SaleStatus.PENDING
        );

        if (pendingSale.isPresent()) {
            throw new DuplicateResourceException("Ya existe una venta pendiente. Confirme o cancele antes de crear otra.");
        }

        // Crear nueva venta
        Sale sale = new Sale();
        sale.setUser(currentUser);
        sale.setCashRegister(activeCashRegister);
        sale.setStatus(SaleStatus.PENDING);
        sale.setDate(LocalDateTime.now());

        Sale saleSaved = saleRepository.save(sale);
        return saleMapper.toDTO(saleSaved);
    }

    @Transactional
    public SaleResponseDTO confirmSale(Long saleId) {
        // 1. Buscar la venta y validar su estado
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada"));

        if (sale.getStatus() != SaleStatus.PENDING) {
            throw new IllegalStateException("Solo se pueden confirmar ventas en estado PENDING.");
        }
        
        // 4. Cambiar el estado de la venta
        sale.setStatus(SaleStatus.CONFIRMED);
        
        // 5. Impactar el balance de la caja registradora
        // Este método se encargará de sumar el total de la venta al balance de la caja
        // asociada a la venta.
        sale.impactCashRegister();

        // 6. Guardar los cambios
        // Al finalizar el método, @Transactional asegurará que tanto la venta
        // como la caja registradora se actualicen en la base de datos.
        // Aunque puedes guardar explícitamente por claridad si lo prefieres:
        // saleRepository.save(sale);
        // cashRegisterRepository.save(sale.getCashRegister());

        // 7. Construir y devolver la respuesta

        return saleMapper.toDTO(sale);
    }

    public SaleDTO getSaleById(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

        return toDTO(sale);
    }

    private SaleDTO toDTO(Sale sale) {
        SaleDTO dto = new SaleDTO();
        dto.setId(sale.getId());
        dto.setDate(sale.getDate());
        dto.setStatus(sale.getStatus().name());
        dto.setTotalAmount(sale.calculateTotal());

        List<SaleItemDTO> items = sale.getItems().stream().map(item -> {
            SaleItemDTO i = new SaleItemDTO();
            i.setId(item.getId());
            i.setProductName(item.getProduct().getName());
            i.setQuantity(item.getQuantity());
            i.setPrice(item.getPrice());
            i.setSubtotal(item.getSubtotal());
            return i;
        }).toList();

        dto.setItems(items);

        return dto;
    }
}
