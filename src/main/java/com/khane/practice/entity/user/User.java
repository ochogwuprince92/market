package com.khane.practice.entity.user;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.khane.practice.entity.cart.Cart;
import com.khane.practice.entity.product.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.*;

@Entity
@Table(name = "users", indexes = { @Index(name = "idx_email", columnList = "email"),
                                @Index(name = "idx_name", columnList = "name")}
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @NotNull
    @NotBlank(message = "Name is required")
    private String name;
    private String username;

    @Email(message = "Email cannot be empty")
    private String email;
    private String password;

//    Map user to product
    @OneToMany(mappedBy = "user",
            cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Product> products = new ArrayList<>();

//    Map user to cart; one user to one cart
    @OneToOne(mappedBy = "user")
    @JsonManagedReference
    private Cart cart;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<UserRole> roles = new HashSet<>();
}