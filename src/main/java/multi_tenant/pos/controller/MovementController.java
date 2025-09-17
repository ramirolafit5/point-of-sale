package multi_tenant.pos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import multi_tenant.pos.dto.Movement.DepositRequestDTO;
import multi_tenant.pos.dto.Movement.DepositResponseDTO;
import multi_tenant.pos.dto.Movement.ExtractionRequestDTO;
import multi_tenant.pos.dto.Movement.ExtractionResponseDTO;
import multi_tenant.pos.dto.Sale.SaleItemRequestDTO;
import multi_tenant.pos.dto.Sale.SaleItemResponseDTO;
import multi_tenant.pos.service.DepositService;
import multi_tenant.pos.service.ExtractService;
import multi_tenant.pos.service.SaleItemService;

@RestController
@RequestMapping("/api/movimientos")
public class MovementController {

    @Autowired
    private ExtractService extractService;

    @Autowired
    private DepositService depositService;

    @Autowired
    private SaleItemService saleItemService;

    // --- Registrar una extracción ---
    @PostMapping("/extracciones")
    public ResponseEntity<ExtractionResponseDTO> registrarExtraccion(@RequestBody ExtractionRequestDTO dto) {
        ExtractionResponseDTO resultado = extractService.registrarExtraccion(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }

    // --- Registrar un deposito ---
    @PostMapping("/depositos")
    public ResponseEntity<DepositResponseDTO> registrarDeposito(@RequestBody DepositRequestDTO dto) {
        DepositResponseDTO resultado = depositService.registrarDeposito(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }

    // Agregar un item a una venta existente
    @PostMapping("/{saleId}/items")
    public ResponseEntity<SaleItemResponseDTO> agregarItem(@PathVariable Long saleId,@RequestBody SaleItemRequestDTO dto) {
        SaleItemResponseDTO response = saleItemService.agregarItem(saleId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

