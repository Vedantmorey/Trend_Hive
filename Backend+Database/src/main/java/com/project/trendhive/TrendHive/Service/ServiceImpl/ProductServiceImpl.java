package com.project.trendhive.TrendHive.Service.ServiceImpl;

import com.project.trendhive.TrendHive.Dto.ProductDto;
import com.project.trendhive.TrendHive.Entity.Product;
import com.project.trendhive.TrendHive.Entity.User;
import com.project.trendhive.TrendHive.Repository.ProductRepository;
import com.project.trendhive.TrendHive.Repository.UserRepository;
import com.project.trendhive.TrendHive.Service.FileStorageService;
import com.project.trendhive.TrendHive.Service.ProductService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final FileStorageService fileStorageService;


    public ProductServiceImpl(UserRepository userRepository, ProductRepository productRepository, ModelMapper modelMapper, FileStorageService fileStorageService) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public ProductDto addProduct(ProductDto productDto, String imageUrl) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = modelMapper.map(productDto, Product.class);
        product.setImage_url(imageUrl);
        product.setRetailer(user);

        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductDto.class);
    }

    @Override
    public List<ProductDto> findProducts(String category, String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage;

        if (query != null && category != null) {
            productPage = productRepository.findByProductNameContainingIgnoreCaseAndCategoryIgnoreCase(query, category, pageable);
        } else if (query != null) {
            productPage = productRepository.findByProductNameContainingIgnoreCase(query, pageable);
        } else if (category != null) {
            productPage = productRepository.findByCategoryIgnoreCase(category, pageable);
        } else {
            productPage = productRepository.findAll(pageable);
        }

        return productPage.getContent().stream()
                .map(p -> modelMapper.map(p, ProductDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        return modelMapper.map(product, ProductDto.class);
    }

    @Override
    public List<ProductDto> getRetailerProducts() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User retailer = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Retailer not found."));

        List<Product> products = productRepository.findByRetailer(retailer);
        return products.stream()
                .map(p -> modelMapper.map(p, ProductDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductDto updateProduct(Long id, ProductDto productDto, MultipartFile imageFile) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found."));

        // Security check: Only the owner can update the product
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!existingProduct.getRetailer().getEmail().equals(userEmail)) {
            throw new AccessDeniedException("You do not have permission to update this product.");
        }

        // Update fields from DTO
        existingProduct.setProductName(productDto.getProductName());
        existingProduct.setDescription(productDto.getDescription());
        existingProduct.setPrice(productDto.getPrice());
        existingProduct.setCategory(productDto.getCategory());
        existingProduct.setQuantity(productDto.getQuantity());

        // Handle image update if a new file is provided
        if (imageFile != null && !imageFile.isEmpty()) {
            String newImageUrl = fileStorageService.storeFile(imageFile);
            existingProduct.setImage_url(newImageUrl);
        }

        Product updatedProduct = productRepository.save(existingProduct);
        return modelMapper.map(updatedProduct, ProductDto.class);
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found."));

        // Security check: Only the owner can delete the product
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!product.getRetailer().getEmail().equals(userEmail)) {
            throw new AccessDeniedException("You do not have permission to delete this product.");
        }

        productRepository.delete(product);
    }
}