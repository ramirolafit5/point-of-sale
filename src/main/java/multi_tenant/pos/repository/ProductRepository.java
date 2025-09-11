package multi_tenant.pos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import multi_tenant.pos.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Verificar si ya existe un producto con ese nombre en la misma tienda
    boolean existsByNameAndStoreId(String name, Long storeId);

    // Buscar productos por tienda, opcional para listar todos de la tienda
    List<Product> findByStoreIdOrderByIdAsc(Long storeId);

    // Buscar producto por id y store, útil para update y delete con validación de pertenencia
    Optional<Product> findByIdAndStoreId(Long productId, Long storeId);

    // Valida que no exista otro producto con el mismo nombre
    boolean existsByNameAndStoreIdAndIdNot(String name, Long storeId, Long id);
}
