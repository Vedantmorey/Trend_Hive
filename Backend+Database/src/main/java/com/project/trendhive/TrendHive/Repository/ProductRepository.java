package com.project.trendhive.TrendHive.Repository;

import com.project.trendhive.TrendHive.Entity.Product;
import com.project.trendhive.TrendHive.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByCategoryIgnoreCase(String category, Pageable pageable);

    Page<Product> findByProductNameContainingIgnoreCase(String productName, Pageable pageable);

    Page<Product> findByProductNameContainingIgnoreCaseAndCategoryIgnoreCase(String productName, String category, Pageable pageable);

    List<Product> findByRetailer(User retailer);
}