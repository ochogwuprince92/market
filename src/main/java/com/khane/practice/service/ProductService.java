package com.khane.practice.service;

import com.khane.practice.entity.product.Product;
import com.khane.practice.entity.user.User;
import com.khane.practice.repository.ProductRepository;
import com.khane.practice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    //Either use @RequiredArgsConstructor or constructor
    public ProductService(ProductRepository productRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public List<Product> getAllProduct() {
        return productRepository.findAll();
    }

    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    public Product getAllProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Item searched is not found"));
    }

    public Product updateProduct(Long id, Product product) {

        Product existingProduct = productRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Item searched is not found"));

//        Update fields
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setQuantity(product.getQuantity());
        existingProduct.setCategory(product.getCategory());

//        Save the product
        return productRepository.save(existingProduct);

    }

    public void deleteProduct(Long id) {

        Product existingProduct = productRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Item not found"));

        productRepository.delete(existingProduct);
    }

    public Product addProductToUser(Long userId, Product product) {

        User user = userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("User not found"));

//      Map product to user
        product.setUser(user);
//      Optional
        user.getProducts().add(product);

//      Save the product
        return productRepository.save(product);
    }

    public List<Product> getProductsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("User not found"));

        return user.getProducts();
    }
}
