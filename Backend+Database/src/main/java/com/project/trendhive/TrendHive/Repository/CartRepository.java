package com.project.trendhive.TrendHive.Repository;

import com.project.trendhive.TrendHive.Entity.Cart;
import com.project.trendhive.TrendHive.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart,Long> {
    Optional<Cart> findByUser(User user);
}
