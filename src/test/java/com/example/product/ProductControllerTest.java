package com.example.product;

import com.example.product.Model.ProductModel;
import com.example.product.Service.JwtService;
import com.example.product.Service.ProductService;
import com.example.product.Controller.ProductController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class ProductControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
    }

    @Test
    @WithMockUser
    public void testGetProducts_Authenticated_Success() throws Exception {
        // Mock a product list
        ProductModel product1 = new ProductModel(1L, "Product 1", "Description", 100.0, "image1.jpg", null);
        ProductModel product2 = new ProductModel(2L, "Product 2", "Description", 150.0, "image2.jpg", null);

        when(productService.getAllProducts()).thenReturn(List.of(product1, product2));

        mockMvc.perform(MockMvcRequestBuilders.get("/products"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("All products are available."));
    }

    @Test
    public void testCreateProduct_Authenticated_Success() throws Exception {
        // Mock the product creation
        // ProductModel product = new ProductModel(null, "Product 1", "Description", 100.0, "image1.jpg", null);

        // Assuming user is authenticated, and returning product model with ID
        ProductModel savedProduct = new ProductModel(1L, "Product 1", "Description", 100.0, "image1.jpg", null);

        when(jwtService.getUserIdFromToken(anyString())).thenReturn(1L);
        when(productService.createProduct(any(ProductModel.class))).thenReturn(savedProduct);

        mockMvc.perform(MockMvcRequestBuilders.post("/products")
                .contentType("application/json")
                .content("{\"productName\":\"Product 1\", \"description\":\"Description\", \"price\":100.0, \"image\":\"image1.jpg\"}")
                .header("Authorization", "Bearer validtoken"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Product created successfully."));
    }

    @Test
    public void testGetProductById_Success() throws Exception {
        // Mock the product retrieval by ID
        ProductModel product = new ProductModel(1L, "Product 1", "Description", 100.0, "image1.jpg", null);
        when(productService.getProductById(1L)).thenReturn(Optional.of(product));

        mockMvc.perform(MockMvcRequestBuilders.get("/products/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Product found."));
    }

    @Test
    public void testUpdateProduct_Authenticated_Success() throws Exception {
        // Mock existing product
        ProductModel existingProduct = new ProductModel(1L, "Old Product", "Old Description", 100.0, "oldimage.jpg", null);
        ProductModel updatedProduct = new ProductModel(1L, "Updated Product", "Updated Description", 150.0, "updatedimage.jpg", null);

        when(productService.getProductById(1L)).thenReturn(Optional.of(existingProduct));
        when(productService.createProduct(any(ProductModel.class))).thenReturn(updatedProduct);

        mockMvc.perform(MockMvcRequestBuilders.put("/products/1")
                .contentType("application/json")
                .content("{\"productName\":\"Updated Product\", \"description\":\"Updated Description\", \"price\":150.0, \"image\":\"updatedimage.jpg\"}")
                .header("Authorization", "Bearer validtoken"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Product updated successfully."));
    }

    @Test
    public void testDeleteProduct_Authenticated_Success() throws Exception {
        // Mock product deletion
        ProductModel product = new ProductModel(1L, "Product 1", "Description", 100.0, "image1.jpg", null);
        when(productService.getProductById(1L)).thenReturn(Optional.of(product));

        mockMvc.perform(MockMvcRequestBuilders.delete("/products/1")
                .header("Authorization", "Bearer validtoken"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Product deleted successfully."));
    }

    @Test
    public void testSearchProducts_Authenticated_Success() throws Exception {
        // Mock search result
        ProductModel product = new ProductModel(1L, "Product 1", "Description", 100.0, "image1.jpg", null);
        when(productService.searchProductsByName("Product")).thenReturn(List.of(product));

        mockMvc.perform(MockMvcRequestBuilders.get("/products/search")
                .param("productName", "Product"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Products found."));
    }
}

