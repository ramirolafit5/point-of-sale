package multi_tenant.pos.service;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import multi_tenant.pos.dto.Category.CategoryResponseDTO;
import multi_tenant.pos.dto.Category.CreateCategoryRequestDTO;
import multi_tenant.pos.dto.Category.UpdateCategoryRequestDTO;
import multi_tenant.pos.handler.ResourceNotFoundException;
import multi_tenant.pos.handler.UnauthorizedException;
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

    public CategoryResponseDTO createCategory(CreateCategoryRequestDTO dto) {
        User currentUser = userService.getCurrentUser();

        Category category = categoryMapper.toEntity(dto);
        category.setStore(currentUser.getStore());

        Category saved = categoryRepository.save(category);

        return categoryMapper.toDTO(saved);
    }

    public List<CategoryResponseDTO> getCategoriesOfCurrentUserStore() {
        User currentUser = userService.getCurrentUser(); // devuelve la entidad User
        Long storeId = currentUser.getStore().getId();  // sacamos la store asociada

        List<Category> categories = categoryRepository.findByStoreIdOrderByIdAsc(storeId);
        return categories.stream()
                       .map(categoryMapper::toDTO)
                       .toList();
    }

    public CategoryResponseDTO getCategoryById(Long categoryId) {
        User currentUser = userService.getCurrentUser();
        Long storeId = currentUser.getStore().getId();

        //Directamente con la consulta esa verifica que ese producto pertenezca a la bdd
        Category category = categoryRepository.findByIdAndStoreId(categoryId, storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria no encontrada o no pertenece a tu tienda"));

        return categoryMapper.toDTO(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        User currentUser = userService.getCurrentUser();
        Long storeId = currentUser.getStore().getId();

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria no encontrado"));

        // Validar que la categoria sea de la tienda del usuario logueado
        if (!category.getStore().getId().equals(storeId)) {
            throw new UnauthorizedException("No existe esta categoria en tu tienda");
        }

        categoryRepository.delete(category);
    }

    @Transactional
    public CategoryResponseDTO updateCategory(Long categoryId, UpdateCategoryRequestDTO dto) {
        User currentUser = userService.getCurrentUser();
        Long storeId = currentUser.getStore().getId();

        // Buscamos la categoria, asegurando que sea de la tienda del usuario
        Category category = categoryRepository.findByIdAndStoreId(categoryId, storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria no encontrada"));

        // Usamos el mapper
        categoryMapper.updateEntityFromDto(dto, category);

        // Guardamos
        Category updatedCategory = categoryRepository.save(category);

        return categoryMapper.toDTO(updatedCategory);
    }
}
