package com.semantic.search.config;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.File;

@Component
public class OntologyLoader {

    private OWLOntologyManager manager;
    private OWLOntology ontology;

    private Model model;

    private final String BASE_PATH = "src/main/resources/owl/";

    @PostConstruct
    public void init() {
        loadOntology();
    }

    public void loadOntology() {
        try {
            // =========================
            // ✅ OWL API
            // =========================
            manager = OWLManager.createOWLOntologyManager();

            manager.getIRIMappers().add(iri -> {

                if (iri.toString().equals("http://www.semanticsearch.com/schema")) {
                    return IRI.create(new File(BASE_PATH + "schema.owl"));
                }
                if (iri.toString().equals("http://www.semanticsearch.com/link")) {
                    return IRI.create(new File(BASE_PATH + "link.owl"));
                }
                if (iri.toString().equals("http://www.semanticsearch.com/amazon")) {
                    return IRI.create(new File(BASE_PATH + "amazon.owl"));
                }
                if (iri.toString().equals("http://www.semanticsearch.com/flipkart")) {
                    return IRI.create(new File(BASE_PATH + "flipkart.owl"));
                }
                if (iri.toString().equals("http://www.semanticsearch.com/shopify")) {
                    return IRI.create(new File(BASE_PATH + "shopify.owl"));
                }
                if (iri.toString().equals("http://www.semanticsearch.com/master")) {
                    return IRI.create(new File(BASE_PATH + "master.owl"));
                }

                return null;
            });

            File file = new File(BASE_PATH + "master.owl");

            if (ontology != null) {
                manager.removeOntology(ontology);
            }

            ontology = manager.loadOntologyFromOntologyDocument(file);

            // =========================
            // 🔥 JENA MODEL (AUTO LOAD)
            // =========================
            model = ModelFactory.createDefaultModel();

            File folder = new File(BASE_PATH);
            File[] files = folder.listFiles();

            if (files != null) {
                for (File f : files) {
                    if (f.getName().endsWith(".owl")) {

                        String uri = f.toURI().toString(); // 🔥 IMPORTANT FIX

                        System.out.println("📂 Loading: " + uri);

                        model.read(uri); // ✅ NOW CORRECT
                    }
                }
            }

            System.out.println("✅ Ontology + ALL OWL files loaded successfully");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reloadOntology() {
        System.out.println("🔄 Reloading ontology + model...");
        loadOntology();
    }

    public OWLOntologyManager getManager() {
        return manager;
    }

    public OWLOntology getOntology() {
        return ontology;
    }

    public Model getModel() {
        return model;
    }
}