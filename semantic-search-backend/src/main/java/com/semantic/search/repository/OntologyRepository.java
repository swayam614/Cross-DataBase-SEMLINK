//package com.semantic.search.repository;
//
//import com.semantic.search.config.OntologyLoader;
//import org.apache.jena.query.*;
//import org.apache.jena.rdf.model.Model;
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
//        Model model = loader.getModel();
//
//        String queryStr =
//                "PREFIX schema: <http://www.semanticsearch.com/schema#>\n" +
//                        "\n" +
//                        "SELECT ?name ?price ?link ?rating ?source ?brandName ?catName ?time\n" +
//                        "WHERE {\n" +
//                        "    ?product a schema:Product ;\n" +
//                        "             schema:productName ?name ;\n" +
//                        "             schema:price ?price ;\n" +
//                        "             schema:productLink ?link ;\n" +
//                        "             schema:rating ?rating ;\n" +
//                        "             schema:hasCategory ?cat ;\n" +
//                        "             schema:hasBrand ?brand ;\n" +
//                        "             schema:hasSource ?src ;\n" +
//                        "             schema:createdAt ?time .\n" +
//                        "\n" +
//                        "    BIND(STRAFTER(STR(?src), \"#\") AS ?source)\n" +
//                        "    BIND(STRAFTER(STR(?brand), \"#\") AS ?brandName)\n" +
//                        "    BIND(STRAFTER(STR(?cat), \"#\") AS ?catName)\n" +
//                        "\n" +
//                        "    FILTER (\n" +
//                        "        CONTAINS(LCASE(?name), LCASE('" + keyword + "')) ||\n" +
//                        "        CONTAINS(LCASE(?brandName), LCASE('" + keyword + "')) ||\n" +
//                        "        CONTAINS(LCASE(?catName), LCASE('" + keyword + "'))\n" +
//                        "    )\n" +
//                        "}\n" +
//                        "ORDER BY DESC(?time)";
//
//        Query query = QueryFactory.create(queryStr);
//
//        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
//
//            ResultSet rs = qexec.execSelect();
//
//            while (rs.hasNext()) {
//                QuerySolution sol = rs.next();
//
//                Map<String, Object> map = new HashMap<>();
//
//                map.put("name", sol.get("name").toString());
//
//                // 🔥 FIXED: SAFE PARSING
//                try {
//                    map.put("price", Double.parseDouble(sol.get("price").asLiteral().getString()));
//                } catch (Exception e) {
//                    map.put("price", 0.0);
//                }
//
//                try {
//                    map.put("rating", Double.parseDouble(sol.get("rating").asLiteral().getString()));
//                } catch (Exception e) {
//                    map.put("rating", 0.0);
//                }
//
//                map.put("link", sol.get("link").toString());
//                map.put("source", sol.get("source").toString());
//
//                results.add(map);
//                System.out.println("👉 Product: " + sol.get("name"));
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
                        "SELECT ?name ?price ?link ?rating ?source ?brandName ?catName ?time\n" +
                        "WHERE {\n" +
                        "    ?product a schema:Product ;\n" +
                        "             schema:productName ?name ;\n" +
                        "             schema:price ?price ;\n" +
                        "             schema:productLink ?link ;\n" +
                        "             schema:rating ?rating ;\n" +
                        "             schema:hasCategory ?cat ;\n" +
                        "             schema:hasBrand ?brand ;\n" +
                        "             schema:hasSource ?src ;\n" +
                        "             schema:createdAt ?time .\n" +
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
                        "}\n" +
                        "ORDER BY DESC(?time)";

        Query query = QueryFactory.create(queryStr);

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {

            ResultSet rs = qexec.execSelect();

            while (rs.hasNext()) {
                QuerySolution sol = rs.next();

                Map<String, Object> map = new HashMap<>();

                map.put("name", sol.get("name").toString());

                // 🔥 SAFE PRICE PARSING
                try {
                    map.put("price", Double.parseDouble(sol.get("price").asLiteral().getString()));
                } catch (Exception e) {
                    map.put("price", 0.0);
                }

                // 🔥 SAFE RATING PARSING
                try {
                    map.put("rating", Double.parseDouble(sol.get("rating").asLiteral().getString()));
                } catch (Exception e) {
                    map.put("rating", 0.0);
                }

                map.put("link", sol.get("link").toString());
                map.put("source", sol.get("source").toString());

                // 🕒 ADD TIMESTAMP (IMPORTANT)
                try {
                    map.put("createdAt", sol.get("time").asLiteral().getString());
                } catch (Exception e) {
                    map.put("createdAt", "");
                }

                results.add(map);

                System.out.println("👉 Product: " + sol.get("name") +
                        " | Time: " + map.get("createdAt"));
            }
        }

        return results;
    }
}

