package com.project.trendhive.TrendHive.Service;

import com.project.trendhive.TrendHive.Dto.ProductDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

// In ProductService.java
public interface ProductService {
    // ...
    ProductDto getProductById(Long id); // A new method to support editing
    ProductDto updateProduct(Long id, ProductDto productDto, MultipartFile imageFile);
    void deleteProduct(Long id);
    List<ProductDto> getRetailerProducts(); // A new method for the dashboard
    List<ProductDto> findProducts(String category, String query, int page, int size);

    ProductDto addProduct(ProductDto productDto, String imageUrl);
}