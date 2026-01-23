package com.khane.practice.controller;

import com.khane.practice.entity.product.Product;
import com.khane.practice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public List<Product> getAllProduct(){
        return productService.getAllProduct();
    }
    @PostMapping("add")
    public Product addProduct(@RequestBody Product product){
        return productService.addProduct(product);
    }

}
