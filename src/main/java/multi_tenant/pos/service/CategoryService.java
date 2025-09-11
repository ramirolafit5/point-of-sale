package multi_tenant.pos.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import multi_tenant.pos.dto.Category.CategoryResponseDTO;
import multi_tenant.pos.dto.Category.CreateCategoryRequestDTO;
import multi_tenant.pos.dto.Category.UpdateCategoryRequestDTO;
import multi_tenant.pos.handler.DuplicateResourceException;
import multi_tenant.pos.handler.ResourceNotFoundException;
import multi_tenant.pos.mapper.CategoryMapper;
import multi_tenant.pos.model.Category;
import multi_tenant.pos.model.User;
import multi_tenant.pos.repository.CategoryRepository;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final UserService userService;

    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper, UserService userService) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.userService = userService;
    }

    @Transactional
    public CategoryResponseDTO createCategory(CreateCategoryRequestDTO dto) {
        Long storeId = getCurrentUserStoreId();

        // Validar que no exista otra categoria con el mismo nombre en la tienda
        boolean exists = categoryRepository.existsByNameAndStoreId(dto.getName(), storeId);
        if (exists) {
            throw new IllegalArgumentException("Ya existe una categoria con ese nombre en tu tienda");
        }

        // Crear la entidad y asociar a la tienda actual
        Category category = categoryMapper.toEntity(dto);
        category.setStore(getCurrentUser().getStore());

        // Guardar
        Category saved = categoryRepository.save(category);

        return categoryMapper.toDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> getAllCategories() {
        Long storeId = getCurrentUserStoreId();

        List<Category> categories = categoryRepository.findByStoreIdOrderByIdAsc(storeId);

        if (categories.isEmpty()) {
            throw new ResourceNotFoundException("No hay categorias en tu tienda");
        }

        return categories.stream()
                        .map(categoryMapper::toDTO)
                        .toList();
    }


    @Transactional(readOnly = true)
    public CategoryResponseDTO getCategoryById(Long categoryId) {
        Long storeId = getCurrentUserStoreId();

        // Busca la categoria segun el id de categoria y id de tienda
        Category category = categoryRepository.findByIdAndStoreId(categoryId, storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria no encontrada"));

        return categoryMapper.toDTO(category);
    }

    @Transactional
    public void deleteCategory(Long categoryId) {
        Long storeId = getCurrentUserStoreId();

        // Busca la categoria segun el id de categoria y id de tienda
        Category category = categoryRepository.findByIdAndStoreId(categoryId, storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria no encontrada"));

        category.getStore().getCategories().remove(category);
        
        categoryRepository.delete(category);
    }

    @Transactional
    public CategoryResponseDTO updateCategory(Long categoryId, UpdateCategoryRequestDTO dto) {
        Long storeId = getCurrentUserStoreId();

        // Buscamos la categoria, asegurando que sea de la tienda del usuario
        Category category = categoryRepository.findByIdAndStoreId(categoryId, storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria no encontrada"));

        // Validación extra: evitar nombres duplicados dentro de la misma tienda
        if (dto.getName() != null && !dto.getName().equalsIgnoreCase(category.getName())) {
            boolean exists = categoryRepository.existsByNameIgnoreCaseAndStoreId(dto.getName(), storeId);
            if (exists) {
                throw new DuplicateResourceException("Ya existe una categoria con ese nombre en tu tienda");
            }
        }

        // Usamos el mapper
        categoryMapper.updateEntityFromDto(dto, category);

        // Guardamos
        Category updatedCategory = categoryRepository.save(category);

        return categoryMapper.toDTO(updatedCategory);
    }

    // Metodos privados reutilizables

    private Long getCurrentUserStoreId() {
        return userService.getCurrentUser().getStore().getId();
    }

    private User getCurrentUser() {
        return userService.getCurrentUser();
    }
}
