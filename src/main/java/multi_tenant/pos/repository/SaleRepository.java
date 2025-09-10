package multi_tenant.pos.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import multi_tenant.pos.model.Sale;
import multi_tenant.pos.model.enums.SaleStatus;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    // Busca una venta pendiente en una caja en particular
    Optional<Sale> findByCashRegisterIdAndStatus(Long cashRegisterId, SaleStatus status);
}
