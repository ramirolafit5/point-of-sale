package multi_tenant.pos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import multi_tenant.pos.model.Sale;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    
}
