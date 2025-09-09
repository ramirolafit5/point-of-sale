package multi_tenant.pos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import multi_tenant.pos.model.Movement;

@Repository
public interface MovementRepository extends JpaRepository<Movement, Long>{
    
}
