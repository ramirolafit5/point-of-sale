package multi_tenant.pos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import multi_tenant.pos.dto.Movement.DepositRequestDTO;
import multi_tenant.pos.dto.Movement.DepositResponseDTO;
import multi_tenant.pos.dto.Movement.ExtractionRequestDTO;
import multi_tenant.pos.dto.Movement.ExtractionResponseDTO;
import multi_tenant.pos.service.MovementService;

@RestController
@RequestMapping("/api/movimientos")
public class MovementController {

    @Autowired
    private MovementService movementService;

    // --- Registrar una extracción ---
    @PostMapping("/extracciones")
    public ResponseEntity<ExtractionResponseDTO> registrarExtraccion(@RequestBody ExtractionRequestDTO dto) {
        ExtractionResponseDTO resultado = movementService.registrarExtraccion(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }

    // --- Registrar un deposito ---
    @PostMapping("/depositos")
    public ResponseEntity<DepositResponseDTO> registrarDeposito(@RequestBody DepositRequestDTO dto) {
        DepositResponseDTO resultado = movementService.registrarDeposito(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }

/*     // --- Registrar una venta ---
    @PostMapping("/ventas")
    public ResponseEntity<Sale> registrarVenta(@RequestBody SaleDTO dto) {
        Sale resultado = movimientoService.registrarVenta(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }

    // --- Obtener todos los movimientos de una caja ---
    @GetMapping("/caja/{cajaId}")
    public ResponseEntity<List<Movement>> getMovimientosPorCaja(@PathVariable Long cajaId) {
        List<Movement> movimientos = movimientoService.getMovimientosPorCaja(cajaId);
        return ResponseEntity.ok(movimientos);
    } */
}

