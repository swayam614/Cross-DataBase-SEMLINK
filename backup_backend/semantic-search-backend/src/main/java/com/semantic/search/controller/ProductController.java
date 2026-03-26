package com.semantic.search.controller;

import com.semantic.search.model.ProductResult;
import com.semantic.search.service.ProductSearchService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin
public class ProductController {

    private final ProductSearchService service;

    public ProductController(ProductSearchService service) {
        this.service = service;
    }

    @GetMapping("/search")
    public List<ProductResult> searchProducts(@RequestParam String query) {

        return service.searchProducts(query);
    }
}