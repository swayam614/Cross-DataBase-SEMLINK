for (Statement stmt : typeStatements) {

    if (!stmt.getObject().isResource()) continue;

    Resource type = stmt.getObject().asResource();

    if (!type.getURI().endsWith("Product")) continue;

    Resource product = stmt.getSubject();

    List<Statement> props = new ArrayList<>();
    StmtIterator pit = product.listProperties();
    while (pit.hasNext()) props.add(pit.next());

    for (Statement pStmt : props) {

        if (!pStmt.getObject().isResource()) continue;

        Resource oldNode = pStmt.getObject().asResource();

        if (!oldNode.hasProperty(RDF.type)) continue;

        Resource nodeType = oldNode.getProperty(RDF.type).getObject().asResource();

        // ================= PRICE =================
        if (nodeType.getURI().endsWith("Price")) {

            // 🔥 CREATE NEW NODE
            Resource newPriceNode = uploadedModel.createResource(
                    oldNode.getURI() + "_v" + System.currentTimeMillis()
            );

            newPriceNode.addProperty(RDF.type, priceClass);

            // 🔥 COPY priceValue
            Statement valStmt = oldNode.getProperty(
                    ResourceFactory.createProperty(oldNode.getNameSpace(), "priceValue")
            );

            if (valStmt != null) {
                newPriceNode.addProperty(
                        ResourceFactory.createProperty(SCHEMA_NS + "priceValue"),
                        valStmt.getObject()
                );
            }

            // 🔥 ADD NEW TIMESTAMP
            newPriceNode.addLiteral(priceTimestamp,
                    uploadedModel.createTypedLiteral(
                            java.time.Instant.now().toString(),
                            XSDDatatype.XSDdateTime
                    )
            );

            // 🔥 LINK NEW NODE
            product.addProperty(hasPrice, newPriceNode);
        }

        // ================= RATING =================
        if (nodeType.getURI().endsWith("Rating")) {

            Resource newRatingNode = uploadedModel.createResource(
                    oldNode.getURI() + "_v" + System.currentTimeMillis()
            );

            newRatingNode.addProperty(RDF.type, ratingClass);

            // 🔥 COPY ratingValue
            Statement valStmt = oldNode.getProperty(
                    ResourceFactory.createProperty(oldNode.getNameSpace(), "ratingValue")
            );

            if (valStmt != null) {
                newRatingNode.addProperty(
                        ResourceFactory.createProperty(SCHEMA_NS + "ratingValue"),
                        valStmt.getObject()
                );
            }

            newRatingNode.addLiteral(ratingTimestamp,
                    uploadedModel.createTypedLiteral(
                            java.time.Instant.now().toString(),
                            XSDDatatype.XSDdateTime
                    )
            );

            product.addProperty(hasRating, newRatingNode);
        }
    }
}