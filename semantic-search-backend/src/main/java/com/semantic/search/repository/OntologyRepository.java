//package com.semantic.search.repository;
//
//import com.semantic.search.config.OntologyLoader;
//import org.semanticweb.owlapi.model.*;
//import org.springframework.stereotype.Repository;
//
//import java.util.*;
//
//@Repository
//public class OntologyRepository {
//
//    private final OntologyLoader loader;
//
//    public OntologyRepository(OntologyLoader loader) {
//        this.loader = loader;
//    }
//
//    public List<Map<String, Object>> searchProducts(String keyword) {
//
//        List<Map<String, Object>> results = new ArrayList<>();
//
//        OWLOntologyManager manager = loader.getManager();
//
//        for (OWLOntology ontology : manager.getOntologies()) {
//
//            for (OWLNamedIndividual ind : ontology.getIndividualsInSignature()) {
//
//                Map<String, Object> productData = new HashMap<>();
//
//                boolean isProduct = false;
//
//                // 🔍 Check if it's Product
//                for (OWLClassAssertionAxiom axiom : ontology.getClassAssertionAxioms(ind)) {
//                    if (axiom.getClassesInSignature().stream()
//                            .anyMatch(c -> c.getIRI().getShortForm().equals("Product"))) {
//                        isProduct = true;
//                        break;
//                    }
//                }
//
//                if (!isProduct) continue;
//
//                // 🔥 Extract data properties
//                for (OWLDataPropertyAssertionAxiom ax : ontology.getDataPropertyAssertionAxioms(ind)) {
//
//                    String prop = ax.getProperty().asOWLDataProperty().getIRI().getShortForm();
//                    String value = ax.getObject().getLiteral();
//
//                    switch (prop) {
//                        case "productName":
//                            productData.put("name", value);
//                            break;
//                        case "price":
//                            productData.put("price", Double.parseDouble(value));
//                            break;
//                        case "rating":
//                            productData.put("rating", Double.parseDouble(value));
//                            break;
//                        case "productLink":
//                            productData.put("link", value);
//                            break;
//                    }
//                }
//
//                // 🔥 Extract source
//                for (OWLObjectPropertyAssertionAxiom ax : ontology.getObjectPropertyAssertionAxioms(ind)) {
//                    String prop = ax.getProperty().asOWLObjectProperty().getIRI().getShortForm();
//
//                    if (prop.equals("hasSource")) {
//                        String sourceIRI = ax.getObject().asOWLNamedIndividual().getIRI().getShortForm();
//                        productData.put("source", sourceIRI);
//                    }
//                }
//
//                // 🔍 FILTER by keyword
//                if (productData.containsKey("name")) {
//                    String name = productData.get("name").toString().toLowerCase();
//
//                    if (name.contains(keyword.toLowerCase())) {
//                        results.add(productData);
//                    }
//                }
//            }
//        }
//
//        return results;
//    }
//}

package com.semantic.search.repository;

import com.semantic.search.config.OntologyLoader;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
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

        Model model = loader.getModel();

        String queryStr =
                "PREFIX schema: <http://www.semanticsearch.com/schema#>\n" +
                        "\n" +
                        "SELECT ?name ?price ?link ?rating ?source ?brandName ?catName\n" +
                        "WHERE {\n" +
                        "    ?product a schema:Product ;\n" +
                        "             schema:productName ?name ;\n" +
                        "             schema:price ?price ;\n" +
                        "             schema:productLink ?link ;\n" +
                        "             schema:rating ?rating ;\n" +
                        "             schema:hasCategory ?cat ;\n" +
                        "             schema:hasBrand ?brand ;\n" +
                        "             schema:hasSource ?src .\n" +
                        "\n" +
                        "    BIND(STRAFTER(STR(?src), \"#\") AS ?source)\n" +
                        "    BIND(STRAFTER(STR(?brand), \"#\") AS ?brandName)\n" +
                        "    BIND(STRAFTER(STR(?cat), \"#\") AS ?catName)\n" +
                        "\n" +
                        "    FILTER (\n" +
                        "        CONTAINS(LCASE(?name), LCASE('" + keyword + "')) ||\n" +
                        "        CONTAINS(LCASE(?brandName), LCASE('" + keyword + "')) ||\n" +
                        "        CONTAINS(LCASE(?catName), LCASE('" + keyword + "'))\n" +
                        "    )\n" +
                        "}";

        Query query = QueryFactory.create(queryStr);

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {

            ResultSet rs = qexec.execSelect();

            while (rs.hasNext()) {
                QuerySolution sol = rs.next();

                Map<String, Object> map = new HashMap<>();

                map.put("name", sol.get("name").toString());

                // 🔥 FIXED: SAFE PARSING
                try {
                    map.put("price", Double.parseDouble(sol.get("price").asLiteral().getString()));
                } catch (Exception e) {
                    map.put("price", 0.0);
                }

                try {
                    map.put("rating", Double.parseDouble(sol.get("rating").asLiteral().getString()));
                } catch (Exception e) {
                    map.put("rating", 0.0);
                }

                map.put("link", sol.get("link").toString());
                map.put("source", sol.get("source").toString());

                results.add(map);
                System.out.println("👉 Product: " + sol.get("name"));
            }
        }

        return results;
    }
}