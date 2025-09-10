package multi_tenant.pos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import multi_tenant.pos.model.SaleItem;

public interface SaleItemRepository extends JpaRepository<SaleItem, Long>{
    
}
