package com.hackerrank.eshopping.service;

import com.hackerrank.eshopping.model.Product;
import com.hackerrank.eshopping.repository.ProductsRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductsService {

    private final ProductsRepository productsRepository;

    public ProductsService(ProductsRepository productsRepository) {
        this.productsRepository = productsRepository;
    }

    public void add(Product product) {
        productsRepository.save(product);
    }

    public Optional<Product> findById(Long id) {
        return productsRepository.findById(id);
    }

    public List<Product> findByCategory(String category) {
        return productsRepository.findByCategoryOrderByAvailabilityDiscountedPriceAscIdDesc(category);
    }

    public List<Product> findAll() {
        return productsRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }
}
