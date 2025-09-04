package multi_tenant.pos.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import multi_tenant.pos.dto.Product.CreateProductRequestDTO;
import multi_tenant.pos.dto.Product.ProductResponseDTO;
import multi_tenant.pos.dto.Product.UpdateProductRequestDTO;
import multi_tenant.pos.handler.ResourceNotFoundException;
import multi_tenant.pos.handler.UnauthorizedException;
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
        // Obtener usuario logueado
        User currentUser = userService.getCurrentUser(); // versión que devuelve User

        // Crear producto y asociar tienda automáticamente
        Product product = productMapper.toEntity(dto);
        product.setStore(currentUser.getStore());

        // Buscar categoria asociada al producto y 
        Category category = categoryRepository.findById(dto.getCategory())
            .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada"));
        product.setCategory(category);

        // Guardar en DB
        Product saved = productRepository.save(product);

        // Devolver DTO usando mapper
        return productMapper.toDTO(saved);
    }

    public List<ProductResponseDTO> getProductsOfCurrentUserStore() {
        User currentUser = userService.getCurrentUser(); // devuelve la entidad User
        Long storeId = currentUser.getStore().getId();  // sacamos la store asociada

        List<Product> products = productRepository.findByStoreIdOrderByIdAsc(storeId);
        return products.stream()
                       .map(productMapper::toDTO)
                       .toList();
    }

    public ProductResponseDTO getProductById(Long productId) {
        User currentUser = userService.getCurrentUser();
        Long storeId = currentUser.getStore().getId();

        //Directamente con la consulta esa verifica que ese producto pertenezca a la bdd
        Product product = productRepository.findByIdAndStoreId(productId, storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado o no pertenece a tu tienda"));

        return productMapper.toDTO(product);
    }

    @Transactional
    public ProductResponseDTO updateProduct(Long productId, UpdateProductRequestDTO dto) {
        User currentUser = userService.getCurrentUser();
        Long storeId = currentUser.getStore().getId();

        // Buscamos el producto, asegurando que sea de la tienda del usuario
        Product product = productRepository.findByIdAndStoreId(productId, storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        // Usamos el mapper
        productMapper.updateEntityFromDto(dto, product);

        Category category = categoryRepository.findById(dto.getCategory())
            .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada"));
        product.setCategory(category);


        // Actualizamos la fecha de modificación
        product.setUpdatedAt(LocalDateTime.now());

        // Guardamos
        Product updatedProduct = productRepository.save(product);

        return productMapper.toDTO(updatedProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        User currentUser = userService.getCurrentUser();
        Long storeId = currentUser.getStore().getId();

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        // Validar que el producto sea de la tienda del usuario logueado
        if (!product.getStore().getId().equals(storeId)) {
            throw new UnauthorizedException("No existe este producto en tu tienda");
        }

        productRepository.delete(product);
    }

}
