package com.zosh.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.zosh.exceptions.ProductException;
import com.zosh.modal.Product;
import com.zosh.modal.Seller;
import com.zosh.request.CreateProductRequest;

public interface ProductService {
    public Product createProduct(CreateProductRequest req, Seller seller) throws ProductException;

    public void deleteProduct(Long productId) throws ProductException;

    public Product updateProduct(Long productId, Product product) throws ProductException;

    Product findProductById(Long productId) throws ProductException;

    List<Product> searchProduct(String query);

    public Page<Product> getAllProduct(
            String category,
            String brand,
            String color,
            String sizes,
            Integer minPrice,
            Integer maxPrice,
            Integer minDiscount,
            String sort,
            String stock,
            Integer pageNumber

    );

    List<Product> getProductBySellerId(Long sellerId);

}
