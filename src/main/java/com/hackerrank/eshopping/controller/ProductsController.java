package com.hackerrank.eshopping.controller;

import com.hackerrank.eshopping.model.Product;
import com.hackerrank.eshopping.service.ProductsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
public class ProductsController {

    @Autowired
    private ProductsService productsService;

    @PostMapping("/products")
    public ResponseEntity<Object> loadProduct(@Valid @RequestBody Product product) {
        Optional<Product> productMember = productsService.findById(product.getId());
        if(productMember.isPresent()) {
            return ResponseEntity.status(400).build();
        }
        productsService.add(product);

        URI location =
                ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                        .buildAndExpand(product.getId())
                        .toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<Object> loadProduct(@PathVariable long id, @Valid @RequestBody Product product) {
        Optional<Product> productMember = productsService.findById(id);
        if(!productMember.isPresent()) {
            return ResponseEntity.status(400).build();
        } else {
            Product productUpdated = productMember.get();
            productUpdated.setAvailability(product.getAvailability());
            productUpdated.setRetailPrice(product.getRetailPrice());
            productUpdated.setDiscountedPrice(product.getDiscountedPrice());
            productsService.add(productUpdated);

            return ResponseEntity.ok(productMember);
        }
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Object> getProduct(@PathVariable long id) {
        Optional<Product> productMember = productsService.findById(id);
        if(!productMember.isPresent()) {
            return ResponseEntity.status(404).build();
        } else {
            return ResponseEntity.ok(productMember);
        }
    }

    @RequestMapping(value = "/products", method = RequestMethod.GET, params = {"category"})
    public ResponseEntity<Object> getAllProductByCategory(@RequestParam(name = "category") String category) {
        List<Product> allProducts = productsService.findByCategory(category);
        return ResponseEntity.ok(allProducts);
    }

    @GetMapping("/products")
    public ResponseEntity<Object> getAllProduct() {
        List<Product> allProducts = productsService.findAll();
        return ResponseEntity.ok(allProducts);
    }
}
