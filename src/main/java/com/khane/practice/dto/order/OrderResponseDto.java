package com.khane.practice.dto.order;

import com.khane.practice.dto.product.ProductResponseDto;
import com.khane.practice.entity.order.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.catalina.LifecycleState;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class OrderResponseDto {

    private UUID id;
    private UUID userId;
    private UUID cartId;
    private OrderStatus status;
    private List<ProductResponseDto> products;
    private BigDecimal totalPrice;
    private LocalDateTime createdAt;


}
