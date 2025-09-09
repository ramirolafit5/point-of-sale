package multi_tenant.pos.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import multi_tenant.pos.dto.CashRegisterD.CashRegisterDTO;
import multi_tenant.pos.service.CashRegisterService;

@RestController
@RequestMapping("/api/cash-registers")
public class CashRegisterController {

    private final CashRegisterService cashRegisterService;

    public CashRegisterController (CashRegisterService cashRegisterService){
        this.cashRegisterService = cashRegisterService;
    }

    // Endpoint para abrir caja
    @PostMapping("/open")
    public ResponseEntity<CashRegisterDTO> abrirCaja() {
        CashRegisterDTO cajaAbierta = cashRegisterService.abrirCaja();
        return ResponseEntity.ok(cajaAbierta);
    }

    // Endpoint para cerrar caja
    @PostMapping("/close")
    public ResponseEntity<CashRegisterDTO> cerrarCaja() {
        CashRegisterDTO cajaAbierta = cashRegisterService.cerrarCaja();
        return ResponseEntity.ok(cajaAbierta);
    }
}
