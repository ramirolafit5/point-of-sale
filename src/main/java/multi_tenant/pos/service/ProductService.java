package multi_tenant.pos.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import multi_tenant.pos.dto.Product.CreateProductRequestDTO;
import multi_tenant.pos.dto.Product.ProductResponseDTO;
import multi_tenant.pos.dto.Product.UpdateProductRequestDTO;
import multi_tenant.pos.handler.ResourceNotFoundException;
import multi_tenant.pos.mapper.ProductMapper;
import multi_tenant.pos.model.Category;
import multi_tenant.pos.model.Product;
import multi_tenant.pos.model.User;
import multi_tenant.pos.repository.CategoryRepository;
import multi_tenant.pos.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final UserService userService;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, UserService userService, ProductMapper productMapper, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.userService = userService;
        this.productMapper = productMapper;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public ProductResponseDTO createProduct(CreateProductRequestDTO dto) {
        Long storeId = getCurrentUserStoreId();

        // Validar que no exista otro producto con el mismo nombre en la misma tienda
        boolean exists = productRepository.existsByNameAndStoreId(dto.getName(), storeId);
        if (exists) {
            throw new IllegalArgumentException("Ya existe un producto con ese nombre en tu tienda");
        }

        // Crear producto y asociar tienda automáticamente
        Product product = productMapper.toEntity(dto);
        product.setStore(getCurrentUser().getStore());

        // Buscar categoría asociada al producto
        Category category = categoryRepository.findById(dto.getCategory())
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada"));
        product.setCategory(category);

        // Guardar en DB
        Product saved = productRepository.save(product);

        // Devolver DTO usando mapper
        return productMapper.toDTO(saved);
    }


    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getAllProducts() {
        Long storeId = getCurrentUserStoreId();

        List<Product> products = productRepository.findByStoreIdOrderByIdAsc(storeId);

        if (products.isEmpty()) {
            throw new ResourceNotFoundException("No hay productos en tu tienda");
        }

        return products.stream()
                    .map(productMapper::toDTO)
                    .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponseDTO getProductById(Long productId) {
        Long storeId = getCurrentUserStoreId();

        Product product = productRepository.findByIdAndStoreId(productId, storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        return productMapper.toDTO(product);
    }

    @Transactional
    public ProductResponseDTO updateProduct(Long productId, UpdateProductRequestDTO dto) {
        Long storeId = getCurrentUserStoreId();

        // 1. Buscar el producto asegurando que pertenezca a la tienda del usuario
        Product product = productRepository.findByIdAndStoreId(productId, storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        // 2. Validar que no exista otro producto con el mismo nombre en la tienda
        boolean exists = productRepository.existsByNameAndStoreIdAndIdNot(dto.getName(), storeId, productId);
        if (exists) {
            throw new IllegalArgumentException("Ya existe otro producto con ese nombre en tu tienda");
        }

        // 3. Mapear los datos del DTO al producto
        productMapper.updateEntityFromDto(dto, product);

        // 4. Actualizar la categoría
        Category category = categoryRepository.findById(dto.getCategory())
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada"));
        product.setCategory(category);

        // 5. Actualizar la fecha de modificación
        product.setUpdatedAt(LocalDateTime.now());

        // 6. Guardar los cambios
        Product updatedProduct = productRepository.save(product);

        // 7. Devolver el DTO
        return productMapper.toDTO(updatedProduct);
    }

    @Transactional
    public void deleteProduct(Long productId) {
        Long storeId = getCurrentUserStoreId();

        // Buscar producto asegurando que pertenezca a la tienda del usuario
        Product product = productRepository.findByIdAndStoreId(productId, storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        product.getStore().getProducts().remove(product);
        
        productRepository.delete(product);
    }

    // Metodos privados para ser reutilizados

    private Long getCurrentUserStoreId() {
        return userService.getCurrentUser().getStore().getId();
    }

    private User getCurrentUser() {
        return userService.getCurrentUser();
    }
}
