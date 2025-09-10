package multi_tenant.pos.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import multi_tenant.pos.model.enums.SaleStatus;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sales")
public class Sale extends Movement {

    // Fecha y hora de la venta
    @Column(nullable = false)
    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SaleStatus status = SaleStatus.PENDING;

    // Total de la venta
    @Column(nullable = false)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    // Productos vendidos (detalle de la venta)
    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SaleItem> items = new ArrayList<>();

    @Override
    public void impactCashRegister() {
        // Sumar el total de la venta al balance de la caja
/*         this.getCashRegister().setBalance(
            this.getCashRegister().getBalance().add(this.getTotalAmount())
        ); */
    }

    public BigDecimal calculateTotal() {
        if (items == null || items.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return items.stream()
                    .map(SaleItem::getSubtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
