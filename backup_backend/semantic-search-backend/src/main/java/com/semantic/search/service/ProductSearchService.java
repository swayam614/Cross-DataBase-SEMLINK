package com.semantic.search.service;

import com.semantic.search.model.ProductResult;
import com.semantic.search.repository.OntologyRepository;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductSearchService {

    private final OntologyRepository repository;

    public ProductSearchService(OntologyRepository repository) {
        this.repository = repository;
    }

    public List<ProductResult> searchProducts(String keyword) {

        ResultSet rs = repository.searchProducts(keyword);

        List<ProductResult> results = new ArrayList<>();

        while (rs.hasNext()) {

            QuerySolution sol = rs.next();

            ProductResult product = new ProductResult();

            product.setProductName(sol.get("name").toString());
            product.setSource(sol.get("source").toString());
            product.setPrice(sol.getLiteral("price").getDouble());
            product.setRating(sol.getLiteral("rating").getDouble());
            product.setProductLink(sol.get("link").toString());

            results.add(product);
        }

        return results;
    }
}