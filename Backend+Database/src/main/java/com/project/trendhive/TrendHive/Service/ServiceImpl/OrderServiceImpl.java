package com.project.trendhive.TrendHive.Service.ServiceImpl;


import com.project.trendhive.TrendHive.Dto.OrderConfirmationDto;
import com.project.trendhive.TrendHive.Entity.Cart;
import com.project.trendhive.TrendHive.Entity.OrderItem;
import com.project.trendhive.TrendHive.Entity.Orders;
import com.project.trendhive.TrendHive.Entity.User;
import com.project.trendhive.TrendHive.Repository.OrderRepository;
import com.project.trendhive.TrendHive.Repository.UserRepository;
import com.project.trendhive.TrendHive.Service.CartService;
import com.project.trendhive.TrendHive.Service.OrderService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final UserRepository userRepository;
    private final CartService cartService;
    private final OrderRepository orderRepository;

    public OrderServiceImpl(UserRepository userRepository, CartService cartService, OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.cartService = cartService;
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public OrderConfirmationDto processOrder(String paymentMethod) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found."));
        Cart cart = user.getCart();

        if (cart == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty.");
        }

        // Fixed: Use the correct class name 'Orders'
        Orders orders = new Orders();
        orders.setUser(user);
        orders.setOrderDate(LocalDateTime.now());
        orders.setPaymentMethod(paymentMethod);
        orders.setTotalAmount(cartService.calculateTotalAmount());

        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setProduct(cartItem.getProduct());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setPrice(cartItem.getProduct().getPrice());
                    // Fixed: Set the reference to the newly created orders
                    orderItem.setOrder(orders);
                    return orderItem;
                })
                .collect(Collectors.toList());

        orders.setItems(orderItems);

        // Save the orders and clear the cart
        orderRepository.save(orders);
        cartService.clearCart();

        return new OrderConfirmationDto("Payment successful! Orders confirmed.", orders.getId());
    }
}