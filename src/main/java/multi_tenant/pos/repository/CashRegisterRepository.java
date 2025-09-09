package multi_tenant.pos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import multi_tenant.pos.model.CashRegister;

@Repository
public interface CashRegisterRepository extends JpaRepository<CashRegister, Long> {
    // Busca la caja activa de una tienda
    CashRegister findByStoreIdAndActiveTrue(Long storeId);
}
