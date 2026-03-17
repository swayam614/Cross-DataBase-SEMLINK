package com.semantic.search.config;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.File;

@Component
public class OntologyLoader {

    private OWLOntologyManager manager;
    private OWLOntology ontology;

    @PostConstruct
    public void init() {
        try {
            manager = OWLManager.createOWLOntologyManager();

            // 🔥 IRI MAPPING (MOST IMPORTANT)
            manager.getIRIMappers().add(iri -> {

                String base = "src/main/resources/owl/";

                if (iri.toString().equals("http://www.semanticsearch.com/schema")) {
                    return IRI.create(new File(base + "schema.owl"));
                }
                if (iri.toString().equals("http://www.semanticsearch.com/link")) {
                    return IRI.create(new File(base + "link.owl"));
                }
                if (iri.toString().equals("http://www.semanticsearch.com/amazon")) {
                    return IRI.create(new File(base + "amazon.owl"));
                }
                if (iri.toString().equals("http://www.semanticsearch.com/flipkart")) {
                    return IRI.create(new File(base + "flipkart.owl"));
                }
                if (iri.toString().equals("http://www.semanticsearch.com/shopify")) {
                    return IRI.create(new File(base + "shopify.owl"));
                }

                return null;
            });

            // ✅ Load ONLY master.owl
            File file = new File("src/main/resources/owl/master.owl");

            ontology = manager.loadOntologyFromOntologyDocument(file);

            System.out.println("✅ Ontology loaded successfully");
            System.out.println("📊 Total ontologies loaded: " + manager.getOntologies().size());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public OWLOntologyManager getManager() {
        return manager;
    }
}