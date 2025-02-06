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

    public List<ProductModel> searchProductsByName(String productName) {
        // Log the search term
        System.out.println("Searching for products with name containing: " + productName);
    
        List<ProductModel> products = productRepository.findByProductNameContainingIgnoreCase(productName);
    
        // Log the result
        System.out.println("Found " + products.size() + " products.");
        
        return products;
    }
    

}
