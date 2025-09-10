package multi_tenant.pos.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import multi_tenant.pos.dto.Sale.SaleItemRequestDTO;
import multi_tenant.pos.dto.Sale.SaleItemResponseDTO;
import multi_tenant.pos.dto.Sale.SaleResponseDTO;
import multi_tenant.pos.handler.ConflictException;
import multi_tenant.pos.handler.ResourceNotFoundException;
import multi_tenant.pos.mapper.SaleMapper;
import multi_tenant.pos.model.CashRegister;
import multi_tenant.pos.model.Product;
import multi_tenant.pos.model.Sale;
import multi_tenant.pos.model.SaleItem;
import multi_tenant.pos.model.User;
import multi_tenant.pos.model.enums.SaleStatus;
import multi_tenant.pos.repository.CashRegisterRepository;
import multi_tenant.pos.repository.ProductRepository;
import multi_tenant.pos.repository.SaleItemRepository;
import multi_tenant.pos.repository.SaleRepository;

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final UserService userService;
    private final CashRegisterRepository cashRegisterRepository;
    private final SaleMapper saleMapper;
    private final ProductRepository productRepository;
    private final SaleItemRepository saleItemRepository;

    public SaleService(SaleRepository saleRepository, UserService userService, CashRegisterRepository cashRegisterRepository, SaleMapper saleMapper, ProductRepository productRepository, SaleItemRepository saleItemRepository) {
        this.saleRepository = saleRepository;
        this.userService = userService;
        this.cashRegisterRepository = cashRegisterRepository;
        this.saleMapper = saleMapper;
        this.productRepository = productRepository;
        this.saleItemRepository = saleItemRepository;
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
            throw new ConflictException("Ya existe una venta pendiente. Confirme o cancele antes de crear otra.");
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
    public SaleItemResponseDTO agregarItem(Long saleId, SaleItemRequestDTO dto) {
        // 1. Buscar la venta
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada"));

        if (sale.getStatus() != SaleStatus.PENDING) {
            throw new IllegalStateException("No se pueden agregar items a una venta confirmada/cancelada");
        }

        // 2. Buscar producto
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        // 3. Crear SaleItem
        SaleItem item = new SaleItem();
        item.setSale(sale);
        item.setProduct(product);
        item.setQuantity(dto.getQuantity());
        item.setPrice(product.getPrice());
        item.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(dto.getQuantity())));

        // 4. Guardar el SaleItem explícitamente para que tenga ID
        saleItemRepository.save(item);

        // 5. Agregar a la venta y actualizar total
        sale.getItems().add(item);
        sale.setTotalAmount(sale.calculateTotal());
        saleRepository.save(sale);

        // 6. Construir DTO
        SaleItemResponseDTO response = new SaleItemResponseDTO();
        response.setId(item.getId()); // ahora ya tiene valor
        response.setProductId(product.getId());
        response.setProductName(product.getName());
        response.setQuantity(item.getQuantity());
        response.setPrice(item.getPrice());
        response.setSubtotal(item.getSubtotal());

        return response;
    }
}
