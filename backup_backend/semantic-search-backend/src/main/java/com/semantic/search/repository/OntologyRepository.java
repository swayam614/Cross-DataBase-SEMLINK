package com.semantic.search.repository;

import com.semantic.search.config.OntologyLoader;
import org.apache.jena.query.*;
import org.springframework.stereotype.Repository;

@Repository
public class OntologyRepository {

    private final OntologyLoader loader;

    public OntologyRepository(OntologyLoader loader) {
        this.loader = loader;
    }

    public ResultSet searchProducts(String keyword) {

        String queryString =
                "PREFIX schema: <http://www.semanticsearch.com/schema#> " +
                        "SELECT ?name ?source ?price ?rating ?link WHERE { " +
                        " ?p a schema:Product ; " +
                        "    schema:productName ?name ; " +
                        "    schema:price ?price ; " +
                        "    schema:rating ?rating ; " +
                        "    schema:productLink ?link ; " +
                        "    schema:hasSource ?source . " +
                        " FILTER regex(?name, '" + keyword + "', 'i') " +
                        "}";

        Query query = QueryFactory.create(queryString);

        QueryExecution qexec =
                QueryExecutionFactory.create(query, loader.getModel());

        return qexec.execSelect();
    }
}