package com.project.trendhive.TrendHive.Service;

import com.project.trendhive.TrendHive.Dto.CartDto;
import com.project.trendhive.TrendHive.Entity.User;

public interface CartService {
    CartDto addItemToCart(Long productId, int quantity);
    CartDto getCart();
    CartDto updateItemQuantity(Long productId, int quantity);
    void removeItemFromCart(Long productId);
    void clearCart();
    double calculateTotalAmount();
}