package com.project.trendhive.TrendHive.Repository;

import com.project.trendhive.TrendHive.Entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Orders, Long> {
}