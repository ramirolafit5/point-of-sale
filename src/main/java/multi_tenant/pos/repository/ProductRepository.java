package multi_tenant.pos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import multi_tenant.pos.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByStoreIdOrderByIdAsc(Long storeId);

    Optional<Product> findByIdAndStoreId(Long id, Long storeId);

}
