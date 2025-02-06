package com.example.product.Repository;

import com.example.product.Model.ProductModel;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<ProductModel, Long> {
    List<ProductModel> findByProductNameContainingIgnoreCase(String productName);
}
