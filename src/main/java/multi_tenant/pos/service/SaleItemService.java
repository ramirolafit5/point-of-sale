package multi_tenant.pos.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import multi_tenant.pos.dto.Sale.SaleItemRequestDTO;
import multi_tenant.pos.dto.Sale.SaleItemResponseDTO;
import multi_tenant.pos.handler.ResourceNotFoundException;
import multi_tenant.pos.mapper.SaleMapper;
import multi_tenant.pos.model.Product;
import multi_tenant.pos.model.Sale;
import multi_tenant.pos.model.SaleItem;
import multi_tenant.pos.model.enums.SaleStatus;
import multi_tenant.pos.repository.ProductRepository;
import multi_tenant.pos.repository.SaleItemRepository;
import multi_tenant.pos.repository.SaleRepository;

@Service
public class SaleItemService {
    
    private final SaleRepository saleRepository;
    private final SaleMapper saleMapper;
    private final ProductRepository productRepository;
    private final SaleItemRepository saleItemRepository;

    public SaleItemService(SaleRepository saleRepository, SaleMapper saleMapper, ProductRepository productRepository, SaleItemRepository saleItemRepository) {
        this.saleRepository = saleRepository;
        this.saleMapper = saleMapper;
        this.productRepository = productRepository;
        this.saleItemRepository = saleItemRepository;
    }

    @Transactional
    public SaleItemResponseDTO agregarItem(Long saleId, SaleItemRequestDTO dto) {
        // 1. Buscar la venta
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada"));
        
        // Evaluar que esa venta este como pendiente
        if (sale.getStatus() != SaleStatus.PENDING) {
            throw new IllegalStateException("No se pueden agregar items a una venta confirmada/cancelada");
        }

        // 2. Buscar producto
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        // 3. Validar stock disponible
        if (product.getQuantity() < dto.getQuantity()) {
            throw new IllegalArgumentException("No hay stock suficiente para el producto: " + product.getName());
        }

        // 4. Descontar el stock del producto
        product.setQuantity(product.getQuantity() - dto.getQuantity());

        // 5. Crear SaleItem
        SaleItem item = new SaleItem();
        item.setSale(sale);
        item.setProduct(product);
        item.setQuantity(dto.getQuantity());
        item.setPrice(product.getPrice());
        item.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(dto.getQuantity())));

        // 6. Guardar el SaleItem
        saleItemRepository.save(item);

        // 7. Agregar a la venta y actualizar total
        sale.getItems().add(item);
        sale.setAmount(sale.calculateTotal());

        // Guardar el Sale con los nuevos datos
        saleRepository.save(sale);

        return saleMapper.toDto(item);
    }
    
}
