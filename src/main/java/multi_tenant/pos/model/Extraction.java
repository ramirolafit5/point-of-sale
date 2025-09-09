package multi_tenant.pos.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import multi_tenant.pos.handler.ConflictException;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "extraction_movements")
public class Extraction extends Movement {

    private String reason; // motivo de la extracción

    @Override
    public void impactCashRegister() {
        BigDecimal balance = getCashRegister().getBalance();
        if (getAmount().compareTo(balance) > 0) {
            throw new ConflictException("El monto excede el balance de la caja");
        }
        getCashRegister().setBalance(balance.subtract(getAmount()));
    }
}

