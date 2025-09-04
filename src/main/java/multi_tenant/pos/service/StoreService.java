package multi_tenant.pos.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import multi_tenant.pos.dto.Store.ConfirmPasswordToDeleteDTO;
import multi_tenant.pos.dto.Store.CreateStoreDTO;
import multi_tenant.pos.dto.Store.StoreResponseDTO;
import multi_tenant.pos.dto.Store.UpdateStoreDTO;
import multi_tenant.pos.handler.AccessDeniedException;
import multi_tenant.pos.handler.ResourceNotFoundException;
import multi_tenant.pos.mapper.StoreMapper;
import multi_tenant.pos.model.Store;
import multi_tenant.pos.model.User;
import multi_tenant.pos.repository.StoreRepository;

@Service
public class StoreService {

    private final UserService userService;
    private final StoreRepository storeRepository;
    private final StoreMapper storeMapper;
    private final PasswordEncoder passwordEncoder;

    public StoreService (StoreRepository storeRepository, StoreMapper storeMapper, UserService userService, PasswordEncoder passwordEncoder){
        this.storeRepository = storeRepository;
        this.storeMapper = storeMapper;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public StoreResponseDTO createStore(CreateStoreDTO dto) {
        // Convertimos el DTO a entidad
        Store store = storeMapper.toEntity(dto);

        // Seteamos la fecha de creación
        store.setFechaCreacion(LocalDateTime.now());

        // Guardamos en la base de datos
        Store savedStore = storeRepository.save(store);

        // Devolvemos el DTO de respuesta
        return storeMapper.toDTO(savedStore);
    }

    public List<StoreResponseDTO> getStores() {
        List<Store> stores = storeRepository.findAll();
        return stores.stream()
                       .map(storeMapper::toDTO)
                       .toList();
    }

    public StoreResponseDTO getStoreById(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Tienda no encontrada"));

        return storeMapper.toDTO(store);
    }

    public void deleteStore(Long storeId, ConfirmPasswordToDeleteDTO dto) {
        // 1. Obtener el usuario actual
        User currentUser = userService.getCurrentUser();

        // 2. Validar la clave del usuario actual
        if (!passwordEncoder.matches(dto.getPassword(), currentUser.getPassword())) {
            throw new AccessDeniedException("Contraseña incorrecta");
        }

        // 3. Verificar si la tienda existe antes de intentar eliminarla
        if (!storeRepository.existsById(storeId)) {
            throw new ResourceNotFoundException("Tienda no existente");
        }

        // 4. Eliminar la tienda
        storeRepository.deleteById(storeId);
    }

    public StoreResponseDTO updateStoreFromAdmin(Long storeId, UpdateStoreDTO updateStoreDTO) {
        // Buscar la tienda por su ID; lanzar excepción si no existe
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found with id: " + storeId));

        // Usamos el mapper
        storeMapper.updateToEntity(updateStoreDTO, store);

        // Guardar los cambios
        Store updatedStore = storeRepository.save(store);

        // Convertir la entidad actualizada a DTO y devolver
        return storeMapper.toDTO(updatedStore);
    } 

/* 
    Finalmente decidimos que esto solo lo haga el admin.
    public StoreResponseDTO updateStore(UpdateStoreDTO updateStoreDTO) {
        User currentUser = userService.getCurrentUser();
        Store store = currentUser.getStore();

        // Usamos el mapper
        storeMapper.updateToEntity(updateStoreDTO, store);

        // Guardar los cambios
        Store updatedStore = storeRepository.save(store);

        // Convertir la entidad actualizada a DTO y devolver
        return storeMapper.toDTO(updatedStore);
    }
*/

}
