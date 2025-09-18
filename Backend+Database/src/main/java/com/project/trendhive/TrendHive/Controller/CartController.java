package com.project.trendhive.TrendHive.Controller;

import com.project.trendhive.TrendHive.Dto.CartDto;
import com.project.trendhive.TrendHive.Service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CartDto> addItemToCart(@RequestParam Long productId, @RequestParam int quantity) {
        CartDto updatedCart = cartService.addItemToCart(productId, quantity);
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedCart);
    }

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CartDto> getCart() {
        CartDto cartDto = cartService.getCart();
        return ResponseEntity.ok(cartDto);
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CartDto> updateItemQuantity(@RequestParam Long productId, @RequestParam int quantity) {
        CartDto updatedCart = cartService.updateItemQuantity(productId, quantity);
        return ResponseEntity.ok(updatedCart);
    }

    @DeleteMapping("/remove/{productId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Void> removeItemFromCart(@PathVariable Long productId) {
        cartService.removeItemFromCart(productId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/clear")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Void> clearCart() {
        cartService.clearCart();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/total")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Double> getCartTotal() {
        double total = cartService.calculateTotalAmount();
        return ResponseEntity.ok(total);
    }
}