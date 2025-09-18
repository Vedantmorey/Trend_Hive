package com.project.trendhive.TrendHive.Dto;

public class OrderConfirmationDto {
    private String message;
    private Long orderId;

    public OrderConfirmationDto(String message, Long orderId) {
        this.message = message;
        this.orderId = orderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}