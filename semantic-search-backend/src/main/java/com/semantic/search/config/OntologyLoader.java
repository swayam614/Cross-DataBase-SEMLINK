//package com.semantic.search.config;
//
//import org.apache.jena.rdf.model.Model;
//import org.apache.jena.rdf.model.ModelFactory;
//import org.semanticweb.owlapi.apibinding.OWLManager;
//import org.semanticweb.owlapi.model.*;
//import org.springframework.stereotype.Component;
//import org.apache.jena.rdf.model.*;
//import org.apache.jena.vocabulary.RDF;
//import org.apache.jena.datatypes.xsd.XSDDatatype;
//
//import jakarta.annotation.PostConstruct;
//import java.io.File;
//
//@Component
//public class OntologyLoader {
//
//    private OWLOntologyManager manager;
//    private OWLOntology ontology;
//
//    private Model model;
//
//    private final String BASE_PATH = "src/main/resources/owl/";
//
//    @PostConstruct
//    public void init() {
//        loadOntology();
//    }
//
//    public void loadOntology() {
//        try {
//            // =========================
//            // ✅ OWL API
//            // =========================
//            manager = OWLManager.createOWLOntologyManager();
//
//            manager.getIRIMappers().add(iri -> {
//
//                if (iri.toString().equals("http://www.semanticsearch.com/schema")) {
//                    return IRI.create(new File(BASE_PATH + "schema.owl"));
//                }
//                if (iri.toString().equals("http://www.semanticsearch.com/link")) {
//                    return IRI.create(new File(BASE_PATH + "link.owl"));
//                }
//                if (iri.toString().equals("http://www.semanticsearch.com/amazon")) {
//                    return IRI.create(new File(BASE_PATH + "amazon.owl"));
//                }
//                if (iri.toString().equals("http://www.semanticsearch.com/flipkart")) {
//                    return IRI.create(new File(BASE_PATH + "flipkart.owl"));
//                }
//                if (iri.toString().equals("http://www.semanticsearch.com/shopify")) {
//                    return IRI.create(new File(BASE_PATH + "shopify.owl"));
//                }
//                if (iri.toString().equals("http://www.semanticsearch.com/master")) {
//                    return IRI.create(new File(BASE_PATH + "master.owl"));
//                }
//
//                return null;
//            });
//
//            File file = new File(BASE_PATH + "master.owl");
//
//            if (ontology != null) {
//                manager.removeOntology(ontology);
//            }
//
//            ontology = manager.loadOntologyFromOntologyDocument(file);
//
//            // =========================
//            // 🔥 JENA MODEL (AUTO LOAD)
//            // =========================
//            model = ModelFactory.createDefaultModel();
//
//            File folder = new File(BASE_PATH);
//            File[] files = folder.listFiles();
//
//            if (files != null) {
//                for (File f : files) {
//                    if (f.getName().endsWith(".owl")) {
//
//                        String uri = f.toURI().toString(); // 🔥 IMPORTANT FIX
//
//                        System.out.println("📂 Loading: " + uri);
//
//                        model.read(uri); // ✅ NOW CORRECT
//                    }
//                }
//            }
//
//            System.out.println("✅ Ontology + ALL OWL files loaded successfully");
//
//            // =========================
//// 🕒 ADD DEFAULT TIMESTAMP
//// =========================
//            String NS = "http://www.semanticsearch.com/schema#";
//
//// Property
//            Property createdAt = model.getProperty(NS + "createdAt");
//
//// Class
//            Resource productClass = model.getResource(NS + "Product");
//
//// Default timestamp (project start date)
//            Literal defaultTime = model.createTypedLiteral(
//                    "2026-01-01T00:00:00",
//                    org.apache.jena.datatypes.xsd.XSDDatatype.XSDdateTime
//            );
//
//// Iterate all products
//            ResIterator products = model.listResourcesWithProperty(
//                    org.apache.jena.vocabulary.RDF.type,
//                    productClass
//            );
//
//            while (products.hasNext()) {
//                Resource product = products.nextResource();
//
//                // If createdAt NOT present → add default
//                if (!product.hasProperty(createdAt)) {
//                    product.addProperty(createdAt, defaultTime);
//                }
//            }
//
//            System.out.println("🕒 Default timestamps assigned where missing");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void reloadOntology() {
//        System.out.println("🔄 Reloading ontology + model...");
//        loadOntology();
//    }
//
//    public OWLOntologyManager getManager() {
//        return manager;
//    }
//
//    public OWLOntology getOntology() {
//        return ontology;
//    }
//
//    public Model getModel() {
//        return model;
//    }
//}


package com.semantic.search.config;

<<<<<<< HEAD
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.datatypes.xsd.XSDDatatype;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

=======
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
>>>>>>> 3683453 (Add manual mapping and remove the bug of duplication by making price and rating as class and assumed that this two data properties are changing so add in that time stamp and make it temporal)
import org.springframework.stereotype.Component;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.rdf.model.InfModel;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;

@Component
public class OntologyLoader {

    private Model model;

    private final String BASE_PATH = "src/main/resources/owl/";

    @PostConstruct
    public void init() {
        loadOntology();
    }

    public void loadOntology() {
        try {

            // 🔹 Step 1: Load base model
            Model baseModel = ModelFactory.createDefaultModel();

            File folder = new File(BASE_PATH);

            if (!folder.exists()) {
                System.out.println("❌ OWL folder not found!");
                return;
            }

            File[] files = folder.listFiles();

            System.out.println("📂 Loading OWL files...");

<<<<<<< HEAD
                        String uri = f.toURI().toString();
=======
            int fileCount = 0;
>>>>>>> 3683453 (Add manual mapping and remove the bug of duplication by making price and rating as class and assumed that this two data properties are changing so add in that time stamp and make it temporal)

            for (File file : files) {

<<<<<<< HEAD
                        model.read(uri);
=======
                if (file.isFile() && file.getName().endsWith(".owl")) {

                    try (FileInputStream fis = new FileInputStream(file)) {

                        System.out.println("➡️ Loading: " + file.getName());

                        baseModel.read(fis, null, "RDF/XML");
                        fileCount++;

                    } catch (Exception e) {
                        System.out.println("❌ Failed loading: " + file.getName());
                        e.printStackTrace();
>>>>>>> 3683453 (Add manual mapping and remove the bug of duplication by making price and rating as class and assumed that this two data properties are changing so add in that time stamp and make it temporal)
                    }
                }
            }

            System.out.println("📁 Total OWL files loaded: " + fileCount);
            System.out.println("📊 Base Triples Loaded: " + baseModel.size());

            // 🔥 Step 2: Apply OWL Reasoner
            Reasoner reasoner = ReasonerRegistry.getOWLReasoner();

            // Bind schema (important for better inference)
            reasoner = reasoner.bindSchema(baseModel);

            InfModel infModel = ModelFactory.createInfModel(reasoner, baseModel);

            // 🔥 Step 3: Store inferred model
            model = infModel;

            System.out.println("📊 Total Triples AFTER reasoning: " + model.size());
            System.out.println("✅ Ontology fully loaded WITH reasoning 🚀");

            // =========================
            // 🕒 ADD DEFAULT TIMESTAMP
            // =========================
            addDefaultTimestamps();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addDefaultTimestamps() {

        String NS = "http://www.semanticsearch.com/schema#";

        Property createdAt = model.getProperty(NS + "createdAt");
        Resource productClass = model.getResource(NS + "Product");

        if (createdAt == null) {
            System.out.println("❌ createdAt property not found in schema!");
            return;
        }

        Literal defaultTime = model.createTypedLiteral(
                "2026-01-01T00:00:00",
                XSDDatatype.XSDdateTime
        );

        ResIterator products = model.listResourcesWithProperty(RDF.type, productClass);

        while (products.hasNext()) {
            Resource product = products.nextResource();

            if (!product.hasProperty(createdAt)) {

                // Safety: remove any incorrect existing values
//                product.removeAll(createdAt);

                // Add default timestamp
                product.addProperty(createdAt, defaultTime);

                System.out.println("✔ Timestamp added to: " + product.getURI());
            }
        }

        System.out.println("🕒 Default timestamps assigned where missing");
    }

    public void reloadOntology() {
        System.out.println("🔄 Reloading ontology...");
        loadOntology();
    }

    public Model getModel() {
        return model;
    }
}

