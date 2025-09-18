package com.project.trendhive.TrendHive.Repository;

import com.project.trendhive.TrendHive.Entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}