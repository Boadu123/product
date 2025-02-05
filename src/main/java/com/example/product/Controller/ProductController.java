package com.example.product.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.example.product.Model.ProductModel;
import com.example.product.Model.UserModel;
import com.example.product.Service.JwtService;
import com.example.product.Service.ProductService;

@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private JwtService jwtService;

    @GetMapping(value = "/products")
    public ResponseEntity<Map<String, Object>> getProducts() {
        Map<String, Object> response = new HashMap<>();

        try {
            // Get authentication object from SecurityContext
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // If user is not authenticated
            if (authentication == null || authentication.getPrincipal() == "anonymousUser") {
                response.put("status", "error");
                response.put("message", "User not authenticated");
                return ResponseEntity.status(401).body(response);
            }

            // Retrieve all products
            List<ProductModel> products = productService.getAllProducts();

            if (products.isEmpty()) {
                response.put("status", "error");
                response.put("message", "No products found.");
                response.put("details", new ArrayList<>());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            response.put("status", "success");
            response.put("message", "All products are available.");
            response.put("details", products);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "An error occurred while fetching products.");
            response.put("details", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping(value = "/products")
    public ResponseEntity<Map<String, Object>> createProduct(@Validated @RequestBody ProductModel productModel,
            BindingResult bindingResult) {

        Map<String, Object> response = new HashMap<>();

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // If user is not authenticated
            if (authentication == null || authentication.getPrincipal() == "anonymousUser") {
                response.put("status", "error");
                response.put("message", "User not authenticated");
                return ResponseEntity.status(401).body(response);
            }

            // Get the userId from the token (this assumes you have a method to extract the
            // // user ID
            Long userId = jwtService.getUserIdFromToken((String) authentication.getCredentials());

            // Check if there are validation errors in the request body
            if (bindingResult.hasErrors()) {
                response.put("status", "error");
                response.put("message", "Invalid input data");
                response.put("details", bindingResult.getAllErrors());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Retrieve the user from the database based on the userId
            Optional<UserModel> user = jwtService.getUserById(userId);
            if (user.isEmpty()) {
                response.put("status", "error");
                response.put("message", "User not found.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Set the user automatically on the productModel
            productModel.setUser(user.get());

            // Save the product
            ProductModel createdProduct = productService.createProduct(productModel);

            response.put("status", "success");
            response.put("message", "Product created successfully.");
            response.put("details", createdProduct);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "An error occurred while creating the product.");
            response.put("details", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping(value = "/products/{id}")
    public ResponseEntity<Map<String, Object>> getProductById(@PathVariable("id") Long productId) {
        Map<String, Object> response = new HashMap<>();

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // If user is not authenticated
            if (authentication == null || authentication.getPrincipal() == "anonymousUser") {
                response.put("status", "error");
                response.put("message", "User not authenticated");
                return ResponseEntity.status(401).body(response);
            }

            // Retrieve the product by ID
            Optional<ProductModel> product = productService.getProductById(productId);

            if (product.isEmpty()) {
                response.put("status", "error");
                response.put("message", "Product not found.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            response.put("status", "success");
            response.put("message", "Product found.");
            response.put("details", product.get());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "An error occurred while fetching the product.");
            response.put("details", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping(value = "/products/{id}")
    public ResponseEntity<Map<String, Object>> updateProduct(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable("id") Long productId,
            @Validated @RequestBody ProductModel productModel,
            BindingResult bindingResult) {

        Map<String, Object> response = new HashMap<>();

        try {
            // Validate the authentication header
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.put("status", "error");
                response.put("message", "Unauthorized access. No token provided.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            String token = authHeader.substring(7); // Extract token

            if (!jwtService.validateToken(token)) {
                response.put("status", "error");
                response.put("message", "Invalid or expired token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Get the userId from the token
            Long userId = jwtService.getUserIdFromToken(token);

            // Check if there are validation errors in the request body
            if (bindingResult.hasErrors()) {
                response.put("status", "error");
                response.put("message", "Invalid input data");
                response.put("details", bindingResult.getAllErrors());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Retrieve the product by ID
            Optional<ProductModel> existingProduct = productService.getProductById(productId);

            if (existingProduct.isEmpty()) {
                response.put("status", "error");
                response.put("message", "Product not found.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            ProductModel product = existingProduct.get();

            // Check if the logged-in user is the owner of the product
            if (product.getUser().getId() != (userId)) {
                response.put("status", "error");
                response.put("message", "You are not authorized to update this product.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            // Update the fields of the product
            product.setProductName(productModel.getProductName());
            product.setDescription(productModel.getDescription());
            product.setPrice(productModel.getPrice());
            product.setImage(productModel.getImage());

            // Save the updated product
            ProductModel updatedProduct = productService.createProduct(product);

            response.put("status", "success");
            response.put("message", "Product updated successfully.");
            response.put("details", updatedProduct);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "An error occurred while updating the product.");
            response.put("details", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping(value = "/products/{id}")
    public ResponseEntity<Map<String, Object>> deleteProduct(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable("id") Long productId) {

        Map<String, Object> response = new HashMap<>();

        try {
            // Validate the authentication header
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.put("status", "error");
                response.put("message", "Unauthorized access. No token provided.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            String token = authHeader.substring(7); // Extract token

            if (!jwtService.validateToken(token)) {
                response.put("status", "error");
                response.put("message", "Invalid or expired token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Get the userId from the token
            Long userId = jwtService.getUserIdFromToken(token);

            // Retrieve the product by ID
            Optional<ProductModel> existingProduct = productService.getProductById(productId);

            if (existingProduct.isEmpty()) {
                response.put("status", "error");
                response.put("message", "Product not found.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            ProductModel product = existingProduct.get();

            // Check if the logged-in user is the owner of the product
            if (product.getUser().getId() != (userId)) {
                response.put("status", "error");
                response.put("message", "You are not authorized to delete this product.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            // Delete the product
            productService.deleteProduct(productId);

            response.put("status", "success");
            response.put("message", "Product deleted successfully.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "An error occurred while deleting the product.");
            response.put("details", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
