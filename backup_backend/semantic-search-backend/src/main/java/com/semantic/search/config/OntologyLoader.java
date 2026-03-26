package com.semantic.search.config;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class OntologyLoader {

    private Model model;

    public OntologyLoader() {

        model = ModelFactory.createDefaultModel();

        loadFile("ontologies/schema.owl");
        loadFile("ontologies/amazon.owl");
        loadFile("ontologies/flipkart.owl");
        loadFile("ontologies/shopify.owl");
        loadFile("ontologies/link.owl");
        loadFile("ontologies/master.owl");

        System.out.println("Total triples loaded: " + model.size());
    }

    private void loadFile(String path) {

        InputStream inputStream = getClass()
                .getClassLoader()
                .getResourceAsStream(path);

        model.read(inputStream, null);

        System.out.println("Loaded ontology: " + path);
    }

    public Model getModel() {
        return model;
    }
}