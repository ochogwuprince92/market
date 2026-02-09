package com.khane.practice.repository;

import com.khane.practice.entity.order.Order;
import com.khane.practice.entity.product.Product;
import com.khane.practice.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    Optional<Order> findByUser(User user);
}
