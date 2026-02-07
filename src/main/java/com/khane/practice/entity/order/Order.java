package com.khane.practice.entity.order;

import com.khane.practice.entity.cart.Cart;
import com.khane.practice.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.ser.impl.UnknownSerializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

//    who place the order
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

//    cart for the order
    @OneToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @Column(nullable = false)
    private BigDecimal totalPrice;

    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
