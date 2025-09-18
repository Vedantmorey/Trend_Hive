package com.project.trendhive.TrendHive.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productName;
    private String description;
    private Double price;
    private String category;

    @Column(name = "image_url")
    private String image_url;

    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "retailer_id")
    @JsonIgnore // prevent infinite JSON recursion
    private User retailer;

    // Constructors
    public Product() {}

    public Product(Long id, String productName, String description, Double price, String category,
                   String image_url, Integer quantity, User retailer) {
        this.id = id;
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.category = category;
        this.image_url = image_url;
        this.quantity = quantity;
        this.retailer = retailer;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getImage_url() { return image_url; }
    public void setImage_url(String image_url) { this.image_url = image_url; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public User getRetailer() { return retailer; }
    public void setRetailer(User retailer) { this.retailer = retailer; }
}
