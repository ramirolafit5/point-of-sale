package multi_tenant.pos.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import multi_tenant.pos.dto.Store.ConfirmPasswordToDeleteDTO;
import multi_tenant.pos.dto.Store.CreateStoreDTO;
import multi_tenant.pos.dto.Store.StoreResponseDTO;
import multi_tenant.pos.dto.Store.UpdateStoreDTO;
import multi_tenant.pos.service.StoreService;

@RestController
@RequestMapping("/api/store")
public class StoreController {
    
    private final StoreService storeService;

    public StoreController (StoreService storeService){
        this.storeService = storeService;
    }

    /* @PreAuthorize("hasRole('ADMIN')") */
    @PostMapping("/create")
    public ResponseEntity<StoreResponseDTO> createProduct(@Valid @RequestBody CreateStoreDTO dto) {
        StoreResponseDTO response = storeService.createStore(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<StoreResponseDTO>> getStores() {
        List<StoreResponseDTO> stores = storeService.getStores();
        return ResponseEntity.ok(stores);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<StoreResponseDTO> getStoreById(@PathVariable Long id) {
        StoreResponseDTO store = storeService.getStoreById(id);
        return ResponseEntity.ok(store);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{storeId}")
    public ResponseEntity<Void> deleteStore(@PathVariable Long storeId,@RequestBody ConfirmPasswordToDeleteDTO dto) {
        storeService.deleteStore(storeId, dto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{storeId}")
    public ResponseEntity<StoreResponseDTO> updateStoreFromAdmin(@PathVariable Long storeId,@Valid @RequestBody UpdateStoreDTO dto) {
        StoreResponseDTO store = storeService.updateStoreFromAdmin(storeId, dto);
        return ResponseEntity.ok(store);
    }

/*     
    @PutMapping()
    public ResponseEntity<StoreResponseDTO> updateStore(@Valid @RequestBody UpdateStoreDTO dto) {
        StoreResponseDTO store = storeService.updateStore(dto);
        return ResponseEntity.ok(store);
    } 
*/

}
