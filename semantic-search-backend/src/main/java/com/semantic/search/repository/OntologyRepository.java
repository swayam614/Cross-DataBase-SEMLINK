package com.semantic.search.repository;

import com.semantic.search.config.OntologyLoader;
import org.semanticweb.owlapi.model.*;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class OntologyRepository {

    private final OntologyLoader loader;

    public OntologyRepository(OntologyLoader loader) {
        this.loader = loader;
    }

    public List<Map<String, Object>> searchProducts(String keyword) {

        List<Map<String, Object>> results = new ArrayList<>();

        OWLOntologyManager manager = loader.getManager();

        for (OWLOntology ontology : manager.getOntologies()) {

            for (OWLNamedIndividual ind : ontology.getIndividualsInSignature()) {

                Map<String, Object> productData = new HashMap<>();

                boolean isProduct = false;

                // 🔍 Check if it's Product
                for (OWLClassAssertionAxiom axiom : ontology.getClassAssertionAxioms(ind)) {
                    if (axiom.getClassesInSignature().stream()
                            .anyMatch(c -> c.getIRI().getShortForm().equals("Product"))) {
                        isProduct = true;
                        break;
                    }
                }

                if (!isProduct) continue;

                // 🔥 Extract data properties
                for (OWLDataPropertyAssertionAxiom ax : ontology.getDataPropertyAssertionAxioms(ind)) {

                    String prop = ax.getProperty().asOWLDataProperty().getIRI().getShortForm();
                    String value = ax.getObject().getLiteral();

                    switch (prop) {
                        case "productName":
                            productData.put("name", value);
                            break;
                        case "price":
                            productData.put("price", Double.parseDouble(value));
                            break;
                        case "rating":
                            productData.put("rating", Double.parseDouble(value));
                            break;
                        case "productLink":
                            productData.put("link", value);
                            break;
                    }
                }

                // 🔥 Extract source
                for (OWLObjectPropertyAssertionAxiom ax : ontology.getObjectPropertyAssertionAxioms(ind)) {
                    String prop = ax.getProperty().asOWLObjectProperty().getIRI().getShortForm();

                    if (prop.equals("hasSource")) {
                        String sourceIRI = ax.getObject().asOWLNamedIndividual().getIRI().getShortForm();
                        productData.put("source", sourceIRI);
                    }
                }

                // 🔍 FILTER by keyword
                if (productData.containsKey("name")) {
                    String name = productData.get("name").toString().toLowerCase();

                    if (name.contains(keyword.toLowerCase())) {
                        results.add(productData);
                    }
                }
            }
        }

        return results;
    }
}