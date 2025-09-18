package com.project.trendhive.TrendHive.Dto;
import java.util.List;
public class CartDto {
    private int totalItems;
    private double totalPrice;
    private List<CartItemDto> items;

    public CartDto(int totalItems, double totalPrice, List<CartItemDto> items) {
        this.totalItems = totalItems;
        this.totalPrice = totalPrice;
        this.items = items;
    }

    public CartDto() {

    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<CartItemDto> getItems() {
        return items;
    }

    public void setItems(List<CartItemDto> items) {
        this.items = items;
    }
}