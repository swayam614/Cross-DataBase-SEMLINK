package com.semantic.search.controller;

import com.semantic.search.model.ProductResult;
import com.semantic.search.service.ProductSearchService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*") // ✅ allow frontend access
public class ProductController {

    private final ProductSearchService service;

    public ProductController(ProductSearchService service) {
        this.service = service;
    }

    @GetMapping("/search")
    public List<ProductResult> searchProducts(
            @RequestParam(name = "q") String query // ✅ better param name
    ) {

        if (query == null || query.trim().isEmpty()) {
            throw new RuntimeException("Query parameter 'q' is required");
        }

        return service.searchProducts(query.trim());
    }
}