package com.khane.market.controller;

import com.khane.market.dto.product.ProductRequestDto;
import com.khane.market.dto.product.ProductResponseDto;
import com.khane.market.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public List<ProductResponseDto> getAllProduct() {
        return productService.getAllProduct();
    }

    @PostMapping("/add")
    public ResponseEntity<ProductResponseDto> addProduct(
            @RequestBody @Valid ProductRequestDto productRequestDto) {


        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productService.addProduct(productRequestDto));
    }

    @GetMapping("/{id}")
    public ProductResponseDto getProductById(@PathVariable UUID id) {
        return productService.getProductById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> updateProduct(
            @PathVariable UUID id,
            @RequestBody ProductRequestDto productRequestDto) {

        return ResponseEntity.ok(productService.updateProduct(id, productRequestDto));
    }

    @DeleteMapping("/{id}")
    public String deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return "Item deleted successfully.";
    }

    @PostMapping("/user/{userId}")
    public ProductResponseDto addProductToUser(
            @PathVariable UUID userId,
            @RequestBody @Valid ProductRequestDto productRequestDto) {

        return productService.addProductToUser(userId, productRequestDto);
    }

    @GetMapping("/user/{userId}")
    public List<ProductResponseDto> getProductsByUser(@PathVariable UUID userId) {
        return productService.getProductsByUser(userId);
    }
}