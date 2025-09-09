package multi_tenant.pos.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "deposit_movements")
public class Deposit extends Movement {

    private String reason; // motivo del deposito

    @Override
    public void impactCashRegister() {
        // Sumar el monto de la caja
        this.getCashRegister().setBalance(
            this.getCashRegister().getBalance().add(this.getAmount())
        );
    }
}
