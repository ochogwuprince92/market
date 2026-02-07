package com.khane.practice.controller;

import com.khane.practice.dto.product.ProductRequestDto;
import com.khane.practice.dto.product.ProductResponseDto;
import com.khane.practice.entity.product.Product;
import com.khane.practice.entity.user.User;
import com.khane.practice.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<ProductResponseDto> getAllProduct(){
        return productService.getAllProduct();
    }
    @PostMapping("/add")
    public ResponseEntity <ProductResponseDto> addProduct(
            @RequestBody @Valid ProductRequestDto productRequestDto){

        ProductResponseDto productResponseDto = productService.addProduct(productRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(productResponseDto);
    }

    @GetMapping("/{id}")
    public ProductResponseDto getProductById(@PathVariable UUID id){
        return productService.getProductById(id);
    }

//    Update product
    @PutMapping("/{id}")
    public ResponseEntity <ProductResponseDto> updateProduct(
            @PathVariable UUID id,
            @RequestBody ProductRequestDto productRequestDto){

        ProductResponseDto productResponseDto = productService.updateProduct(id, productRequestDto);
        return ResponseEntity.ok(productResponseDto);
    }

    @DeleteMapping("/{id}")
    public String deleteProduct(@PathVariable UUID id){

        productService.deleteProduct(id);

        return "Item deleted successfully.";
    }

    @PostMapping("/{userId}/products")
    public ProductResponseDto addProductToUser(@PathVariable UUID userId,
                                    @RequestBody @Valid ProductRequestDto productRequestDto){

        return productService.addProductToUser(userId, productRequestDto);
    }

    @GetMapping("/{userId}/products")
    public List<ProductResponseDto> getProductsByUser(@PathVariable UUID userId){
        return productService.getProductsByUser(userId);
    }

}
