//package com.semantic.search.service;
//
//import com.semantic.search.model.ProductResult;
//import com.semantic.search.repository.OntologyRepository;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//@Service
//public class ProductSearchService {
//
//    private final OntologyRepository repository;
//
//    public ProductSearchService(OntologyRepository repository) {
//        this.repository = repository;
//    }
//
//    public List<ProductResult> searchProducts(String keyword) {
//
//        List<Map<String, Object>> rawResults = repository.searchProducts(keyword);
//
//        List<ProductResult> results = new ArrayList<>();
//
//        for (Map<String, Object> map : rawResults) {
//
//            ProductResult product = new ProductResult();
//
//            product.setProductName((String) map.get("name"));
//            product.setSource((String) map.get("source"));
//            product.setPrice((Double) map.get("price"));
//            product.setRating((Double) map.get("rating"));
//            product.setProductLink((String) map.get("link"));
//
//            results.add(product);
//        }
//
//        return results;
//    }
//}


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
            product.setProductLink((String) map.get("link"));

<<<<<<< HEAD
            // 🕒 ADD TIMESTAMP (NEW)
            if (map.get("createdAt") != null) {
                product.setCreatedAt((String) map.get("createdAt"));
            }
=======
            // ✅ Safe numeric handling
            product.setPrice(convertToDouble(map.get("price")));
            product.setRating(convertToDouble(map.get("rating")));
>>>>>>> 3683453 (Add manual mapping and remove the bug of duplication by making price and rating as class and assumed that this two data properties are changing so add in that time stamp and make it temporal)

            results.add(product);
        }

        return results;
    }
<<<<<<< HEAD
}

=======

    private Double convertToDouble(Object value) {
        if (value == null) return null;

        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }

        try {
            return Double.parseDouble(value.toString());
        } catch (Exception e) {
            return null;
        }
    }
}
>>>>>>> 3683453 (Add manual mapping and remove the bug of duplication by making price and rating as class and assumed that this two data properties are changing so add in that time stamp and make it temporal)
