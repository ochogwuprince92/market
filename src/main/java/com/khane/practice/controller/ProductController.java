package com.khane.practice.controller;

import com.khane.practice.entity.product.Product;
import com.khane.practice.entity.user.User;
import com.khane.practice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public List<Product> getAllProduct(){
        return productService.getAllProduct();
    }
    @PostMapping("/add")
    public Product addProduct(@RequestBody Product product){
        return productService.addProduct(product);
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id){
        return productService.getAllProductById(id);
    }

    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable Long id,
                                 @RequestBody Product product){
        return productService.updateProduct(id, product);
    }

    @DeleteMapping("/{id}")
    public String deleteProduct(@PathVariable Long id){

        productService.deleteProduct(id);

        return "Item deleted successfully.";
    }

    @PostMapping("/{userId}/products")
    public Product addProductToUser(@PathVariable Long userId,
                                    @RequestBody Product product){

        return productService.addProductToUser(userId, product);
    }

    @GetMapping("/{userId}/products")
    public List<Product> getProductsByUser(@PathVariable Long userId){
        return productService.getProductsByUser(userId);
    }

}
