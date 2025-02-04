package com.example.product.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Product name cannot be empty")
    private String productName;

    @Column(nullable = false)
    @NotBlank(message = "Description cannot be empty")
    private String description;

    @Column(nullable = false)
    @NotNull(message = "Price cannot be null")
    private Double price;

    @Column(nullable = false)
    @NotBlank(message = "Image URL cannot be empty")
    private String image;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private UserModel user;
}
