package com.khane.practice.repository;

import com.khane.practice.entity.cart.Cart;
import com.khane.practice.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {
   Optional<Cart> findByUser(User user);
}
