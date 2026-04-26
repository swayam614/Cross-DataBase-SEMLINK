// updated one
package com.semantic.search.controller;

import com.semantic.search.config.OntologyLoader;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.*;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

@RestController
@RequestMapping("/api/owl")
@CrossOrigin(origins = "*")
public class OwlController {

    @Autowired
    private OntologyLoader ontologyLoader;

    private final String BASE_PATH =
            System.getProperty("user.dir") + "/src/main/resources/owl/";

    private boolean fileUploaded = false;
    private String latestUploadedFile = null;

    // =========================================================
    // ✅ UPLOAD (FINAL CLEAN + GENERIC)
    // =========================================================
    @PostMapping("/upload")
    public ResponseEntity<String> uploadOwl(@RequestParam("file") MultipartFile file) {

        try {
            File dir = new File(BASE_PATH);
            if (!dir.exists()) dir.mkdirs();

            String fileName = file.getOriginalFilename();
            File targetFile = new File(BASE_PATH + fileName);

            Model baseModel = ModelFactory.createDefaultModel();
            if (targetFile.exists()) {
                baseModel.read(new FileInputStream(targetFile), null, "RDF/XML");
            }

            Model uploadedModel = ModelFactory.createDefaultModel();
            uploadedModel.read(file.getInputStream(), null, "RDF/XML");

            String SCHEMA_NS = "http://www.semanticsearch.com/schema#";

            Property hasPrice = ResourceFactory.createProperty(SCHEMA_NS + "hasPrice");
            Property hasRating = ResourceFactory.createProperty(SCHEMA_NS + "hasRating");

            Property priceTimestamp = ResourceFactory.createProperty(SCHEMA_NS + "priceTimestamp");
            Property ratingTimestamp = ResourceFactory.createProperty(SCHEMA_NS + "ratingTimestamp");

            Resource priceClass = ResourceFactory.createResource(SCHEMA_NS + "Price");
            Resource ratingClass = ResourceFactory.createResource(SCHEMA_NS + "Rating");

            // 🔥 SAFE ITERATION
            List<Statement> typeStatements = new ArrayList<>();
            StmtIterator it = uploadedModel.listStatements(null, RDF.type, (RDFNode) null);
            while (it.hasNext()) typeStatements.add(it.next());

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



            baseModel.add(uploadedModel);
            saveModel(baseModel, fileName);

            ontologyLoader.reloadOntology();

            latestUploadedFile = fileName;
            fileUploaded = true;

            return ResponseEntity.ok("Upload successful 🚀");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Upload failed ❌");
        }
    }

    // =========================================================
    // ✅ ENTITIES (FIXED — SEPARATE SCHEMA + UPLOADED)
    // =========================================================
    @GetMapping("/entities")
    public ResponseEntity<Map<String, List<String>>> getEntities() {

        try {
            Map<String, List<String>> result = new HashMap<>();

            String schemaNS = "http://www.semanticsearch.com/schema#";
            String uploadedNS = getBaseNamespace();

            Model schemaModel = loadModel("schema.owl");

            result.put("schemaClasses", getClasses(schemaModel, schemaNS));
            result.put("schemaProperties", getProperties(schemaModel, schemaNS));

            if (!fileUploaded || latestUploadedFile == null) {

                result.put("uploadedClasses", new ArrayList<>());
                result.put("uploadedProperties", new ArrayList<>());
                result.put("uploadedFileName", new ArrayList<>());

            } else {

                Model uploadedModel = loadModel(latestUploadedFile);

                result.put("uploadedClasses", getUsedClasses(uploadedModel, uploadedNS));
                result.put("uploadedProperties", getUsedProperties(uploadedModel, uploadedNS));
                result.put("uploadedFileName", List.of(latestUploadedFile));
            }

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new HashMap<>());
        }
    }

    // =========================================================
    // ✅ MAPPING API (UNCHANGED)
    // =========================================================
    public static class MappingRequest {
        public String sourceURI;
        public String targetURI;
        public String relation;
    }


@PostMapping("/map")
public ResponseEntity<String> applyMappings(@RequestBody List<MappingRequest> mappings) {

    try {
        Model linkModel = loadModel("link.owl");

        for (MappingRequest m : mappings) {

            // ✅ Convert to FULL URI
            String sourceURI = toSourceURI(m.sourceURI);
            String targetURI = toTargetURI(m.targetURI);

            // ✅ STRICT VALIDATION
            if (!isStrictValidURI(sourceURI) || !isStrictValidURI(targetURI)) {
                System.out.println("❌ BLOCKED BAD URI: " + m.sourceURI + " -> " + m.targetURI);
                continue;
            }

            // ✅ USE CONVERTED URIs (FIXED)
            // ❌ Skip useless mapping
            if (sourceURI.equals(targetURI)) {
                System.out.println("⚠️ Skipping useless mapping: " + sourceURI);
                continue;
            }

            Resource source = linkModel.createResource(sourceURI);
            Resource target = linkModel.createResource(targetURI);

// 🔥 AUTO DECIDE RELATION
            Property relation;

            if (isClass(sourceURI) && isClass(targetURI)) {
                relation = OWL.equivalentClass;
            } else if (isProperty(sourceURI) && isProperty(targetURI)) {
                relation = RDFS.subPropertyOf;
            } else {
                System.out.println("❌ Invalid mapping type: " + sourceURI + " -> " + targetURI);
                continue;
            }

            if (!linkModel.contains(source, relation, target)) {
                linkModel.add(source, relation, target);
            }
        }

        ensureRequiredMappings(linkModel);   // 🔥 ADD THIS LINE

        safeSaveModel(linkModel, "link.owl");
        ontologyLoader.reloadOntology();

        return ResponseEntity.ok("Mappings saved ✅");

    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500).body("Mapping failed ❌");
    }
}

    private void safeSaveModel(Model model, String fileName) {

        String tempFile = BASE_PATH + fileName + ".tmp";
        String finalFile = BASE_PATH + fileName;

        try (FileOutputStream out = new FileOutputStream(tempFile)) {

            model.write(out, "RDF/XML");

            // 🔥 Replace original only if success
            File temp = new File(tempFile);
            File original = new File(finalFile);

            if (original.exists()) original.delete();
            temp.renameTo(original);

        } catch (Exception e) {
            System.out.println("❌ SAVE FAILED - original file preserved");
            e.printStackTrace();
        }
    }

    // =========================================================
    // 🔧 HELPERS
    // =========================================================

    private Model loadModel(String fileName) {
        Model model = ModelFactory.createDefaultModel();
        try {
            model.read(new FileInputStream(BASE_PATH + fileName), null, "RDF/XML");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return model;
    }

    private void saveModel(Model model, String fileName) {
        try (FileOutputStream out = new FileOutputStream(BASE_PATH + fileName)) {
            model.write(out, "RDF/XML");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getBaseNamespace() {
        if (latestUploadedFile == null) return "";
        return "http://www.semanticsearch.com/" + latestUploadedFile.replace(".owl", "") + "#";
    }

    private List<String> getClasses(Model model, String namespace) {
        List<String> list = new ArrayList<>();
        ResIterator it = model.listSubjectsWithProperty(RDF.type, OWL.Class);
        while (it.hasNext()) {
            Resource r = it.next();
            if (r.getURI() != null && r.getURI().startsWith(namespace)) {
                list.add(r.getURI().substring(r.getURI().indexOf("#") + 1));
            }
        }
        return list;
    }

    private List<String> getProperties(Model model, String namespace) {

        List<String> list = new ArrayList<>();

        ResIterator dt = model.listSubjectsWithProperty(RDF.type, OWL.DatatypeProperty);
        while (dt.hasNext()) {
            Resource r = dt.next();
            if (r.getURI() != null && r.getURI().startsWith(namespace)) {
                list.add(r.getURI().substring(r.getURI().indexOf("#") + 1));
            }
        }

        ResIterator obj = model.listSubjectsWithProperty(RDF.type, OWL.ObjectProperty);
        while (obj.hasNext()) {
            Resource r = obj.next();
            if (r.getURI() != null && r.getURI().startsWith(namespace)) {
                list.add(r.getURI().substring(r.getURI().indexOf("#") + 1));
            }
        }

        return list;
    }

    private List<String> getUsedClasses(Model model, String namespace) {

        Set<String> classes = new HashSet<>();

        StmtIterator it = model.listStatements(null, RDF.type, (RDFNode) null);

        while (it.hasNext()) {
            Statement stmt = it.next();
            RDFNode obj = stmt.getObject();

            if (obj.isResource()) {
                String uri = obj.asResource().getURI();
                if (uri != null && uri.startsWith(namespace)) {
                    classes.add(uri.substring(uri.indexOf("#") + 1));
                }
            }
        }

        return new ArrayList<>(classes);
    }

    private List<String> getUsedProperties(Model model, String namespace) {

        Set<String> props = new HashSet<>();

        StmtIterator it = model.listStatements();

        while (it.hasNext()) {
            Statement stmt = it.next();
            String uri = stmt.getPredicate().getURI();

            if (uri != null && uri.startsWith(namespace)) {
                props.add(uri.substring(uri.indexOf("#") + 1));
            }
        }

        return new ArrayList<>(props);
    }

    private boolean isStrictValidURI(String uri) {
        try {
            if (uri == null) return false;

            if (!uri.startsWith("http://") && !uri.startsWith("https://")) return false;

            if (!uri.contains("#") && !uri.contains("/")) return false;

            ResourceFactory.createResource(uri); // Jena validation

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    private String toSourceURI(String uri) {

        if (uri == null || uri.trim().isEmpty()) return null;

        uri = uri.trim();

        if (uri.startsWith("http://")) return uri;

        // 🔥 ALWAYS from uploaded ontology
        return getBaseNamespace() + uri;
    }

    private String toTargetURI(String uri) {

        if (uri == null || uri.trim().isEmpty()) return null;

        uri = uri.trim();

        if (uri.startsWith("http://")) return uri;

        String schemaNS = "http://www.semanticsearch.com/schema#";

        // 🔥 MAP SPECIAL CASES
        if (uri.equalsIgnoreCase("name")) return schemaNS + "productName";
        if (uri.equalsIgnoreCase("link")) return schemaNS + "productLink";

        return schemaNS + uri;
    }

//    private String toFullURI(String uri) {
//
//        if (uri == null || uri.trim().isEmpty()) return null;
//
//        uri = uri.trim();
//
//        // Already full
//        if (uri.startsWith("http://") || uri.startsWith("https://")) return uri;
//
//        String schemaNS = "http://www.semanticsearch.com/schema#";
//        String uploadedNS = getBaseNamespace();
//
//        // 🔥 Schema keywords
//        Set<String> schemaTerms = Set.of(
//                "Product", "Price", "Rating", "Brand", "Category",
//                "productName", "priceValue", "ratingValue",
//                "hasPrice", "hasRating", "hasBrand", "hasCategory",
//                "hasSource", "productLink", "priceTimestamp", "ratingTimestamp"
//        );
//
//        // 🔥 Special mapping fixes
//        if (uri.equalsIgnoreCase("name")) return schemaNS + "productName";
//        if (uri.equalsIgnoreCase("link")) return schemaNS + "productLink";
//
//        if (schemaTerms.contains(uri)) {
//            return schemaNS + uri;
//        }
//
//
//
//        // 🔥 Default → uploaded ontology
//        return uploadedNS + uri;
//    }

    private boolean isClass(String uri) {
        return uri.endsWith("Product") ||
                uri.endsWith("Price") ||
                uri.endsWith("Rating") ||
                uri.endsWith("Brand") ||
                uri.endsWith("Category") ||
                uri.endsWith("Source");
    }

    private boolean isProperty(String uri) {
        return uri.contains("has") ||
                uri.contains("Value") ||
                uri.contains("name") ||
                uri.contains("link") ||
                uri.contains("Timestamp");
    }

    private void addIfMissing(Model model, String s, Property p, String o) {
        Resource subj = model.createResource(s);
        Resource obj = model.createResource(o);

        if (!model.contains(subj, p, obj)) {
            model.add(subj, p, obj);
        }
    }

    private void ensureRequiredMappings(Model linkModel) {

        String base = getBaseNamespace();
        String schema = "http://www.semanticsearch.com/schema#";

        // CLASS MAPPINGS
        addIfMissing(linkModel, base + "Product", OWL.equivalentClass, schema + "Product");
        addIfMissing(linkModel, base + "Price", OWL.equivalentClass, schema + "Price");
        addIfMissing(linkModel, base + "Rating", OWL.equivalentClass, schema + "Rating");
        addIfMissing(linkModel, base + "Brand", OWL.equivalentClass, schema + "Brand");
        addIfMissing(linkModel, base + "Category", OWL.equivalentClass, schema + "Category");
        addIfMissing(linkModel, base + "Source", OWL.equivalentClass, schema + "Source");

        // PROPERTY MAPPINGS
        addIfMissing(linkModel, base + "hasPrice", RDFS.subPropertyOf, schema + "hasPrice");
        addIfMissing(linkModel, base + "hasRating", RDFS.subPropertyOf, schema + "hasRating");
        addIfMissing(linkModel, base + "hasSource", RDFS.subPropertyOf, schema + "hasSource");
        addIfMissing(linkModel, base + "hasBrand", RDFS.subPropertyOf, schema + "hasBrand");
        addIfMissing(linkModel, base + "hasCategory", RDFS.subPropertyOf, schema + "hasCategory");

        addIfMissing(linkModel, base + "priceValue", RDFS.subPropertyOf, schema + "priceValue");
        addIfMissing(linkModel, base + "ratingValue", RDFS.subPropertyOf, schema + "ratingValue");

        addIfMissing(linkModel, base + "name", RDFS.subPropertyOf, schema + "productName");
        addIfMissing(linkModel, base + "link", RDFS.subPropertyOf, schema + "productLink");
    }
}