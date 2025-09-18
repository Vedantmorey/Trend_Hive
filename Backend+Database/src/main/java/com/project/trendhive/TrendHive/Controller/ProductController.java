package com.project.trendhive.TrendHive.Controller;

import com.project.trendhive.TrendHive.Dto.ProductDto;
import com.project.trendhive.TrendHive.Service.FileStorageService;
import com.project.trendhive.TrendHive.Service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final FileStorageService fileStorageService;
    private final ProductService productService;

    public ProductController(FileStorageService fileStorageService, ProductService productService) {
        this.fileStorageService = fileStorageService;
        this.productService = productService;
    }

    @PostMapping
    @PreAuthorize("hasRole('RETAILER')")
    public ResponseEntity<ProductDto> productEntries(@RequestPart("product") ProductDto productDto,
                                                     @RequestPart("image")MultipartFile imagefile){
        String imageUrl = fileStorageService.storeFile(imagefile);
        // Corrected to call the addProduct method from the service
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.addProduct(productDto,imageUrl));
    }

    // This is the correct GET mapping for filtering and pagination
    @GetMapping
    public ResponseEntity<List<ProductDto>> getProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<ProductDto> products = productService.findProducts(category, query, page, size);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable Long id) {
        ProductDto productDto = productService.getProductById(id);
        return ResponseEntity.ok(productDto);
    }

    @GetMapping("/retailer")
    @PreAuthorize("hasRole('RETAILER')")
    public ResponseEntity<List<ProductDto>> getRetailerProducts() {
        List<ProductDto> products = productService.getRetailerProducts();
        return ResponseEntity.ok(products);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('RETAILER')")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id,
                                                    @RequestPart("product") ProductDto productDto,
                                                    @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        ProductDto updatedProduct = productService.updateProduct(id, productDto, imageFile);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('RETAILER')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}