package multi_tenant.pos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import multi_tenant.pos.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long>{
    List<Category> findByStoreIdOrderByIdAsc(Long storeId);

    Optional<Category> findByIdAndStoreId(Long id, Long storeId);
}
