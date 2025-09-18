package com.project.trendhive.TrendHive.Service.ServiceImpl;

import com.project.trendhive.TrendHive.Dto.CartDto;
import com.project.trendhive.TrendHive.Dto.CartItemDto;
import com.project.trendhive.TrendHive.Entity.Cart;
import com.project.trendhive.TrendHive.Entity.CartItem;
import com.project.trendhive.TrendHive.Entity.Product;
import com.project.trendhive.TrendHive.Entity.User;
import com.project.trendhive.TrendHive.Repository.CartRepository;
import com.project.trendhive.TrendHive.Repository.ProductRepository;
import com.project.trendhive.TrendHive.Repository.UserRepository;
import com.project.trendhive.TrendHive.Service.CartService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;

    public CartServiceImpl(UserRepository userRepository, ProductRepository productRepository, CartRepository cartRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
    }

    // Helper method to get the current authenticated user
    private User getCurrentAuthenticatedUser() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found."));
    }

    // Helper method to get or create a user's cart
    private Cart getOrCreateCartForUser(User user) {
        Optional<Cart> existingCart = cartRepository.findByUser(user);
        if (existingCart.isPresent()) {
            return existingCart.get();
        } else {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return newCart;
        }
    }

    @Override
    @Transactional
    public CartDto addItemToCart(Long productId, int quantity) {
        User user = getCurrentAuthenticatedUser();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found."));

        if (product.getQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock for product: " + product.getProductName());
        }

        Cart cart = getOrCreateCartForUser(user);

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            cart.getItems().add(newItem);
        }

        Cart savedCart = cartRepository.save(cart);
        return mapToCartDto(savedCart);
    }

    @Override
    public CartDto getCart() {
        User user = getCurrentAuthenticatedUser();
        Cart cart = getOrCreateCartForUser(user);
        return mapToCartDto(cart);
    }

    @Override
    @Transactional
    public CartDto updateItemQuantity(Long productId, int quantity) {
        User user = getCurrentAuthenticatedUser();
        Cart cart = getOrCreateCartForUser(user);

        CartItem itemToUpdate = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item not found in cart."));

        if (quantity > 0) {
            itemToUpdate.setQuantity(quantity);
        } else {
            cart.getItems().remove(itemToUpdate);
        }

        Cart savedCart = cartRepository.save(cart);
        return mapToCartDto(savedCart);
    }

    @Override
    @Transactional
    public void removeItemFromCart(Long productId) {
        User user = getCurrentAuthenticatedUser();
        Cart cart = getOrCreateCartForUser(user);

        CartItem itemToRemove = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item not found in cart."));

        cart.getItems().remove(itemToRemove);
        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void clearCart() {
        User user = getCurrentAuthenticatedUser();
        Cart cart = getOrCreateCartForUser(user);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    @Override
    public double calculateTotalAmount() {
        User user = getCurrentAuthenticatedUser();
        Cart cart = getOrCreateCartForUser(user);
        return cart.getItems().stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
    }

    // Helper method to convert a Cart entity to a CartDto
    private CartDto mapToCartDto(Cart cart) {
        CartDto cartDto = new CartDto();

        if (cart.getItems().isEmpty()) {
            cartDto.setItems(Collections.emptyList());
            cartDto.setTotalItems(0);
            cartDto.setTotalPrice(0);
        } else {
            List<CartItemDto> itemDtos = cart.getItems().stream().map(cartItem -> {
                CartItemDto itemDto = new CartItemDto();
                itemDto.setProductId(cartItem.getProduct().getId());
                itemDto.setProductName(cartItem.getProduct().getProductName());
                itemDto.setPrice(cartItem.getProduct().getPrice());
                itemDto.setQuantity(cartItem.getQuantity());
                itemDto.setImageUrl(cartItem.getProduct().getImage_url());
                return itemDto;
            }).collect(Collectors.toList());

            cartDto.setItems(itemDtos);

            int totalItems = itemDtos.stream().mapToInt(CartItemDto::getQuantity).sum();
            double totalPrice = itemDtos.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();

            cartDto.setTotalItems(totalItems);
            cartDto.setTotalPrice(totalPrice);
        }
        return cartDto;
    }
}