package com.example.product.Service;

import com.example.product.Model.ProductModel;
import com.example.product.Repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductModel> getAllProducts() {
        return productRepository.findAll();
    }

    public ProductModel createProduct(ProductModel productModel) {
        return productRepository.save(productModel);
    }

    public Optional<ProductModel> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public ProductModel updateProduct(ProductModel productModel) {
        return productRepository.save(productModel);
    }

    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
    }
    
}
