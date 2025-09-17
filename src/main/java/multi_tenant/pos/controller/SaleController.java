package multi_tenant.pos.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import multi_tenant.pos.dto.Sale.SaleDTO;
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

    @PostMapping("/{saleId}/confirmed")
    public ResponseEntity<SaleResponseDTO> saleConfirm(@PathVariable Long saleId) {
        SaleResponseDTO response = saleService.confirmSale(saleId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleDTO> getSale(@PathVariable Long id) {
        SaleDTO saleDTO = saleService.getSaleById(id);
        return ResponseEntity.ok(saleDTO);
    }
}
