package com.project.trendhive.TrendHive.Service;


import com.project.trendhive.TrendHive.Dto.OrderConfirmationDto;

public interface OrderService {
    OrderConfirmationDto processOrder(String paymentMethod);
}