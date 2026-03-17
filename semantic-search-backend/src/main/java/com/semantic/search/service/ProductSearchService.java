package com.semantic.search.service;

import com.semantic.search.model.ProductResult;
import com.semantic.search.repository.OntologyRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ProductSearchService {

    private final OntologyRepository repository;

    public ProductSearchService(OntologyRepository repository) {
        this.repository = repository;
    }

    public List<ProductResult> searchProducts(String keyword) {

        List<Map<String, Object>> rawResults = repository.searchProducts(keyword);

        List<ProductResult> results = new ArrayList<>();

        for (Map<String, Object> map : rawResults) {

            ProductResult product = new ProductResult();

            product.setProductName((String) map.get("name"));
            product.setSource((String) map.get("source"));
            product.setPrice((Double) map.get("price"));
            product.setRating((Double) map.get("rating"));
            product.setProductLink((String) map.get("link"));

            results.add(product);
        }

        return results;
    }
}