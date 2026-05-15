package com.khane.market.repository;

import com.khane.market.entity.cart.Cart;
import com.khane.market.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {
   Optional<Cart> findByUser(User user);
}
