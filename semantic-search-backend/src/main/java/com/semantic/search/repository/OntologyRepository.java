//package com.semantic.search.repository;
//
//import com.semantic.search.config.OntologyLoader;
//import org.apache.jena.query.*;
<<<<<<< HEAD
//import org.apache.jena.rdf.model.Model;
=======
//import org.apache.jena.rdf.model.InfModel;
//import org.apache.jena.rdf.model.Model;
//import org.apache.jena.rdf.model.ModelFactory;
//import org.apache.jena.reasoner.Reasoner;
//import org.apache.jena.reasoner.ReasonerRegistry;
>>>>>>> 3683453 (Add manual mapping and remove the bug of duplication by making price and rating as class and assumed that this two data properties are changing so add in that time stamp and make it temporal)
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
<<<<<<< HEAD
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
=======
//        String lowerKeyword = keyword.toLowerCase();
//
//        String queryStr =
//                "PREFIX schema: <http://www.semanticsearch.com/schema#>\n" +
//                        "\n" +
//                        "SELECT ?name ?source ?price ?rating ?link ?pt ?rt ?latestPriceTime ?latestRatingTime\n" +
//                        "WHERE {\n" +
//                        "\n" +
//                        "    ?product a schema:Product ;\n" +
//                        "             schema:productName ?name ;\n" +
//                        "             schema:hasSource ?src ;\n" +
//                        "             schema:productLink ?link .\n" +
//                        "\n" +
//                        "    FILTER(CONTAINS(LCASE(?name), \"" + lowerKeyword + "\"))\n" +
//                        "\n" +
//                        "    # 🔥 Latest Price (FIXED)\n" +
//                        "    {\n" +
//                        "        SELECT ?product (MAX(?pt) AS ?latestPriceTime)\n" +
//                        "        WHERE {\n" +
//                        "            ?product schema:hasPrice ?pNode .\n" +
//                        "            ?pNode schema:priceTimestamp ?pt .\n" +
//                        "        }\n" +
//                        "        GROUP BY ?product\n" +
//                        "    }\n" +
//                        "\n" +
//                        "    ?product schema:hasPrice ?pNode .\n" +
//                        "    ?pNode schema:priceTimestamp ?pt ;\n" +
//                        "           schema:priceValue ?price .\n" +
//                        "\n" +
//                        "    FILTER(?pt = ?latestPriceTime)\n" +
//                        "\n" +
//                        "    # 🔥 Latest Rating (FIXED)\n" +
//                        "    {\n" +
//                        "        SELECT ?product (MAX(?rt) AS ?latestRatingTime)\n" +
//                        "        WHERE {\n" +
//                        "            ?product schema:hasRating ?rNode .\n" +
//                        "            ?rNode schema:ratingTimestamp ?rt .\n" +
//                        "        }\n" +
//                        "        GROUP BY ?product\n" +
//                        "    }\n" +
//                        "\n" +
//                        "    ?product schema:hasRating ?rNode .\n" +
//                        "    ?rNode schema:ratingTimestamp ?rt ;\n" +
//                        "           schema:ratingValue ?rating .\n" +
//                        "\n" +
//                        "    FILTER(?rt = ?latestRatingTime)\n" +
//                        "\n" +
//                        "    BIND(STRAFTER(STR(?src), \"#\") AS ?source)\n" +
//                        "}";
//
//        System.out.println("🔥 QUERY:\n" + queryStr);
//
//        Query query = QueryFactory.create(queryStr);
//
//        Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
//        InfModel infModel = ModelFactory.createInfModel(reasoner, model);
//
//        try (QueryExecution qexec = QueryExecutionFactory.create(query, infModel)) {
//
//            ResultSet rs = qexec.execSelect();
//
>>>>>>> 3683453 (Add manual mapping and remove the bug of duplication by making price and rating as class and assumed that this two data properties are changing so add in that time stamp and make it temporal)
//            while (rs.hasNext()) {
//                QuerySolution sol = rs.next();
//
//                Map<String, Object> map = new HashMap<>();
//
<<<<<<< HEAD
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
=======
//                // ✅ Name
//                map.put("name", sol.contains("name")
//                        ? sol.getLiteral("name").getString()
//                        : "");
//
//                // ✅ Link
//                map.put("link", sol.contains("link")
//                        ? sol.getLiteral("link").getString()
//                        : "");
//
//                // ✅ Source
//                map.put("source", sol.contains("source")
//                        ? sol.get("source").toString()
//                        : "");
//
//                // ✅ Price (nullable)
//                if (sol.contains("price")) {
//                    try {
//                        map.put("price", sol.getLiteral("price").getDouble());
//                    } catch (Exception e) {
//                        map.put("price", Double.parseDouble(
//                                sol.getLiteral("price").getLexicalForm()
//                        ));
//                    }
//                } else {
//                    map.put("price", null);
//                }
//
//                // ✅ Rating (nullable)
//                if (sol.contains("rating")) {
//                    try {
//                        map.put("rating", sol.getLiteral("rating").getDouble());
//                    } catch (Exception e) {
//                        map.put("rating", Double.parseDouble(
//                                sol.getLiteral("rating").getLexicalForm()
//                        ));
//                    }
//                } else {
//                    map.put("rating", null);
//                }
//
//
//                // ✅ Price Timestamp
//                map.put("priceTimestamp", sol.contains("pt")
//                        ? sol.getLiteral("pt").toString()
//                        : "");
//
//// ✅ Rating Timestamp
//                map.put("ratingTimestamp", sol.contains("rt")
//                        ? sol.getLiteral("rt").toString()
//                        : "");
//
//// ✅ Latest Price Timestamp
//                map.put("latestPriceTime", sol.contains("latestPriceTime")
//                        ? sol.getLiteral("latestPriceTime").toString()
//                        : "");
//
//// ✅ Latest Rating Timestamp
//                map.put("latestRatingTime", sol.contains("latestRatingTime")
//                        ? sol.getLiteral("latestRatingTime").toString()
//                        : "");
//
//                System.out.println("FINAL MAP: " + map);
//
//                results.add(map);
>>>>>>> 3683453 (Add manual mapping and remove the bug of duplication by making price and rating as class and assumed that this two data properties are changing so add in that time stamp and make it temporal)
//            }
//        }
//
//        return results;
//    }
//}


        package com.semantic.search.repository;

import com.semantic.search.config.OntologyLoader;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
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

        String lowerKeyword = keyword.toLowerCase();

        String queryStr =
                "PREFIX schema: <http://www.semanticsearch.com/schema#>\n" +
                        "\n" +
<<<<<<< HEAD
                        "SELECT ?name ?price ?link ?rating ?source ?brandName ?catName ?time\n" +
=======
                        "SELECT ?name ?source ?price ?rating ?link ?pt ?rt ?latestPriceTime ?latestRatingTime\n" +
>>>>>>> 3683453 (Add manual mapping and remove the bug of duplication by making price and rating as class and assumed that this two data properties are changing so add in that time stamp and make it temporal)
                        "WHERE {\n" +
                        "\n" +
                        "    ?product a schema:Product ;\n" +
                        "             schema:productName ?name ;\n" +
<<<<<<< HEAD
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
=======
                        "             schema:hasSource ?src ;\n" +
                        "             schema:productLink ?link .\n" +
                        "\n" +
                        "    FILTER(CONTAINS(LCASE(?name), \"" + lowerKeyword + "\"))\n" +
                        "\n" +
                        "    OPTIONAL {\n" +
                        "        {\n" +
                        "            SELECT ?product (MAX(?pt) AS ?latestPriceTime)\n" +
                        "            WHERE {\n" +
                        "                ?product schema:hasPrice ?pNode .\n" +
                        "                ?pNode schema:priceTimestamp ?pt .\n" +
                        "            }\n" +
                        "            GROUP BY ?product\n" +
                        "        }\n" +
                        "\n" +
                        "        ?product schema:hasPrice ?pNode .\n" +
                        "        ?pNode schema:priceTimestamp ?pt ;\n" +
                        "               schema:priceValue ?price .\n" +
                        "\n" +
                        "        FILTER(?pt = ?latestPriceTime)\n" +
                        "    }\n" +
                        "\n" +
                        "    OPTIONAL {\n" +
                        "        {\n" +
                        "            SELECT ?product (MAX(?rt) AS ?latestRatingTime)\n" +
                        "            WHERE {\n" +
                        "                ?product schema:hasRating ?rNode .\n" +
                        "                ?rNode schema:ratingTimestamp ?rt .\n" +
                        "            }\n" +
                        "            GROUP BY ?product\n" +
                        "        }\n" +
                        "\n" +
                        "        ?product schema:hasRating ?rNode .\n" +
                        "        ?rNode schema:ratingTimestamp ?rt ;\n" +
                        "               schema:ratingValue ?rating .\n" +
                        "\n" +
                        "        FILTER(?rt = ?latestRatingTime)\n" +
                        "    }\n" +
                        "\n" +
                        "    BIND(STRAFTER(STR(?src), \"#\") AS ?source)\n" +
                        "}";
>>>>>>> 3683453 (Add manual mapping and remove the bug of duplication by making price and rating as class and assumed that this two data properties are changing so add in that time stamp and make it temporal)

        System.out.println("🔥 QUERY:\n" + queryStr);

        Query query = QueryFactory.create(queryStr);

        // 🔥 CRITICAL FIX: Use RDFS Reasoner (for subPropertyOf mapping)
        Reasoner reasoner = ReasonerRegistry.getRDFSReasoner();
        InfModel infModel = ModelFactory.createInfModel(reasoner, model);

        try (QueryExecution qexec = QueryExecutionFactory.create(query, infModel)) {

            ResultSet rs = qexec.execSelect();

            while (rs.hasNext()) {
                QuerySolution sol = rs.next();

                Map<String, Object> map = new HashMap<>();

                map.put("name", sol.contains("name")
                        ? sol.getLiteral("name").getString()
                        : "");

<<<<<<< HEAD
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
=======
                map.put("link", sol.contains("link")
                        ? sol.getLiteral("link").getString()
                        : "");

                map.put("source", sol.contains("source")
                        ? sol.get("source").toString()
                        : "");

                if (sol.contains("price")) {
                    map.put("price", sol.getLiteral("price").getDouble());
                } else {
                    map.put("price", null);
                }

                if (sol.contains("rating")) {
                    map.put("rating", sol.getLiteral("rating").getDouble());
                } else {
                    map.put("rating", null);
>>>>>>> 3683453 (Add manual mapping and remove the bug of duplication by making price and rating as class and assumed that this two data properties are changing so add in that time stamp and make it temporal)
                }

                map.put("priceTimestamp", sol.contains("pt")
                        ? sol.getLiteral("pt").toString()
                        : "");

                map.put("ratingTimestamp", sol.contains("rt")
                        ? sol.getLiteral("rt").toString()
                        : "");

                map.put("latestPriceTime", sol.contains("latestPriceTime")
                        ? sol.getLiteral("latestPriceTime").toString()
                        : "");

                map.put("latestRatingTime", sol.contains("latestRatingTime")
                        ? sol.getLiteral("latestRatingTime").toString()
                        : "");

                System.out.println("FINAL MAP: " + map);

                // 🕒 ADD TIMESTAMP (IMPORTANT)
                try {
                    map.put("createdAt", sol.get("time").asLiteral().getString());
                } catch (Exception e) {
                    map.put("createdAt", "");
                }

                results.add(map);
<<<<<<< HEAD

                System.out.println("👉 Product: " + sol.get("name") +
                        " | Time: " + map.get("createdAt"));
=======
>>>>>>> 3683453 (Add manual mapping and remove the bug of duplication by making price and rating as class and assumed that this two data properties are changing so add in that time stamp and make it temporal)
            }
        }

        return results;
    }
}

