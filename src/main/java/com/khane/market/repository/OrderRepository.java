package com.khane.market.repository;

import com.khane.market.entity.order.Order;
import com.khane.market.entity.product.Product;
import com.khane.market.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByUser(User user);

    Optional<Order> findByPaystackReference(String paystackReference);
}
