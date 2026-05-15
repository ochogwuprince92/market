package com.khane.market.service;

import com.khane.market.dto.product.ProductRequestDto;
import com.khane.market.dto.product.ProductResponseDto;
import com.khane.market.entity.product.Product;
import com.khane.market.entity.user.User;
import com.khane.market.exception.UserNotFoundException;
import com.khane.market.repository.ProductRepository;
import com.khane.market.repository.UserRepository;
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


    public List<ProductResponseDto> getAllProduct() {

//        Map to mapProductToUser()
        return productRepository.findAll()
                .stream()
                .map(this::mapToProductResponse)
                .toList();
    }

    public ProductResponseDto addProduct(ProductRequestDto productRequestDto) {

//      Map DTO (fields) to entity so that Repo can access it
        Product product = new Product();
        product.setName(productRequestDto.getName());
        product.setQuantity(productRequestDto.getQuantity());
        product.setPrice(productRequestDto.getPrice());
        product.setDescription(productRequestDto.getDescription());
        product.setCategory(productRequestDto.getCategory());

//      Save to db
        Product addedProduct = productRepository.save(product);
//      Return the save entity
        return mapToProductResponse(addedProduct);
    }

    public ProductResponseDto getProductById(UUID id) {

        Product product = productRepository.findById(id)
                .orElseThrow(()-> new UserNotFoundException("Item searched is not found"));

        return mapToProductResponse(product);
    }

    public ProductResponseDto updateProduct(UUID id, ProductRequestDto productRequestDto) {

        Product existingProduct = productRepository.findById(id)
                .orElseThrow(()-> new UserNotFoundException("Item searched is not found"));

//        Update fields
        existingProduct.setDescription(productRequestDto.getDescription());
        existingProduct.setPrice(productRequestDto.getPrice());
        existingProduct.setQuantity(productRequestDto.getQuantity());
        existingProduct.setCategory(productRequestDto.getCategory());

//        Save the product
        Product updatedProduct = productRepository.save(existingProduct);
        return mapToProductResponse(updatedProduct);

    }

    public void deleteProduct(UUID id) {

        Product existingProduct = productRepository.findById(id)
                .orElseThrow(()-> new UserNotFoundException("Item not found"));

        productRepository.delete(existingProduct);
    }

//    Map Product to User
    public ProductResponseDto addProductToUser(UUID userId, ProductRequestDto productRequestDto) {

        User user = userRepository.findById(userId)
                .orElseThrow(()-> new UserNotFoundException("User not found"));

//      Map product fields to entity
        Product product = new Product();
        product.setName(productRequestDto.getName());
        product.setQuantity(productRequestDto.getQuantity());
        product.setPrice(productRequestDto.getPrice());
        product.setDescription(productRequestDto.getDescription());
        product.setCategory(productRequestDto.getCategory());
        product.setUser(user);

//      Save the product
        Product saved = productRepository.save(product);

        return mapToProductResponse(saved);
    }

    public List<ProductResponseDto> getProductsByUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new UserNotFoundException("User not found"));

        return user.getProducts()
                .stream()
                .map(this::mapToProductResponse)
                .toList();
    }
//  Map product entity to product dto
    private ProductResponseDto mapToProductResponse (Product product) {

        // Safe check: get userId only if user exists
        UUID userId = product.getUser() != null ? product.getUser().getId() : null;

        return new ProductResponseDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getCategory(),
                product.getQuantity(),
                product.getPrice(),
                userId
        );
    }

}
