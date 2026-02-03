package com.khane.practice.service;

import com.khane.practice.entity.product.Product;
import com.khane.practice.entity.user.User;
import com.khane.practice.exception.UserNotFoundException;
import com.khane.practice.repository.ProductRepository;
import com.khane.practice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;


    public List<Product> getAllProduct() {
        return productRepository.findAll();
    }

    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    public Product getAllProductById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(()-> new UserNotFoundException("Item searched is not found"));
    }

    public Product updateProduct(UUID id, Product product) {

        Product existingProduct = productRepository.findById(id)
                .orElseThrow(()-> new UserNotFoundException("Item searched is not found"));

//        Update fields
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setQuantity(product.getQuantity());
        existingProduct.setCategory(product.getCategory());

//        Save the product
        return productRepository.save(existingProduct);

    }

    public void deleteProduct(UUID id) {

        Product existingProduct = productRepository.findById(id)
                .orElseThrow(()-> new UserNotFoundException("Item not found"));

        productRepository.delete(existingProduct);
    }

    public Product addProductToUser(UUID userId, Product product) {

        User user = userRepository.findById(userId)
                .orElseThrow(()-> new UserNotFoundException("User not found"));

//      Map product to user
        product.setUser(user);
//      Optional
        user.getProducts().add(product);

//      Save the product
        return productRepository.save(product);
    }

    public List<Product> getProductsByUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new UserNotFoundException("User not found"));

        return user.getProducts();
    }
}
