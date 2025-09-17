package multi_tenant.pos.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import multi_tenant.pos.dto.Product.AddStockRequestDTO;
import multi_tenant.pos.dto.Product.CreateProductRequestDTO;
import multi_tenant.pos.dto.Product.ProductResponseDTO;
import multi_tenant.pos.dto.Product.UpdateProductRequestDTO;
import multi_tenant.pos.service.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody CreateProductRequestDTO request) {
        ProductResponseDTO response = productService.createProduct(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getProductsOfCurrentUserStore() {
        List<ProductResponseDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id) {
        ProductResponseDTO product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable Long id,@Valid @RequestBody UpdateProductRequestDTO dto) {
        ProductResponseDTO updatedProduct = productService.updateProduct(id, dto);
        return ResponseEntity.ok(updatedProduct);
    }

    @PutMapping("/{id}/agregarStock")
    public ResponseEntity<ProductResponseDTO> addStock(@PathVariable Long id,@Valid @RequestBody AddStockRequestDTO dto) {
        ProductResponseDTO updatedProduct = productService.addStock(id, dto);
        return ResponseEntity.ok(updatedProduct);
    }

    @PutMapping("/{id}/quitarStock")
    public ResponseEntity<ProductResponseDTO> removeStock(@PathVariable Long id,@Valid @RequestBody AddStockRequestDTO dto) {
        ProductResponseDTO updatedProduct = productService.removeStock(id, dto);
        return ResponseEntity.ok(updatedProduct);
    }
}
