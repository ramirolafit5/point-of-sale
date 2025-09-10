package multi_tenant.pos.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import multi_tenant.pos.dto.Sale.SaleItemRequestDTO;
import multi_tenant.pos.dto.Sale.SaleItemResponseDTO;
import multi_tenant.pos.dto.Sale.SaleResponseDTO;
import multi_tenant.pos.service.SaleService;

@RestController
@RequestMapping("/api/ventas")
public class SaleController {
    private final SaleService saleService;
    
    public SaleController (SaleService saleService){
        this.saleService = saleService;
    }

    @PostMapping("/create")
    public ResponseEntity<SaleResponseDTO> createSale() {
        SaleResponseDTO response = saleService.crearVenta();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

        // Agregar un item a una venta existente
    @PostMapping("/{saleId}/items")
    public ResponseEntity<SaleItemResponseDTO> agregarItem(@PathVariable Long saleId,@RequestBody SaleItemRequestDTO dto) {
        SaleItemResponseDTO response = saleService.agregarItem(saleId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
