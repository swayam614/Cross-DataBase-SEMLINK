package com.semantic.search.controller;

import com.semantic.search.config.OntologyLoader;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.OWL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@RestController
@RequestMapping("/api/owl")
@CrossOrigin(origins = "*")
public class OwlController {

    @Autowired
    private OntologyLoader ontologyLoader;

    // 🔗 LINKING LOGIC (Improved + No duplicates + Efficient)
    private void addLinks(Model uploadedModel, Model linkModel, Model masterModel) {

        String NS = "http://www.semanticsearch.com/schema#";
        Property hasName = uploadedModel.getProperty(NS + "productName");

        // Only iterate products having name
        ResIterator newSubjects = uploadedModel.listSubjectsWithProperty(hasName);

        while (newSubjects.hasNext()) {
            org.apache.jena.rdf.model.Resource newProduct = newSubjects.next();

            String newName = newProduct.getProperty(hasName).getString();

            // Compare with master data
            ResIterator existingSubjects = masterModel.listSubjectsWithProperty(hasName);

            while (existingSubjects.hasNext()) {
                org.apache.jena.rdf.model.Resource existingProduct = existingSubjects.next();

                String existingName = existingProduct.getProperty(hasName).getString();

                if (newName.equalsIgnoreCase(existingName)) {

                    // ✅ Avoid duplicate owl:sameAs
                    if (!linkModel.contains(newProduct, OWL.sameAs, existingProduct)) {
                        linkModel.add(newProduct, OWL.sameAs, existingProduct);
                    }
                }
            }
        }
    }

    // ✅ DOWNLOAD SCHEMA
    @GetMapping("/download-schema")
    public ResponseEntity<Resource> downloadSchema() {
        try {
            Resource file = new ClassPathResource("owl/schema.owl");

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=schema.owl")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(file);

        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadOwl(@RequestParam("file") MultipartFile file) {

        try {
            // 🔹 STEP 0: DEFINE PATH
            String projectDir = System.getProperty("user.dir");
            String uploadDir = projectDir + "/src/main/resources/owl/";

            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 🔹 STEP 1: SAVE FILE
            String fileName = file.getOriginalFilename();

            // (optional) prevent overwrite
            File savedFile = new File(uploadDir + fileName);
            if (savedFile.exists()) {
                fileName = System.currentTimeMillis() + "_" + fileName;
                savedFile = new File(uploadDir + fileName);
            }

            file.transferTo(savedFile);

            // 🔥 STEP 2: READ FROM SAVED FILE (FIXED)
            InputStream inputStream = new FileInputStream(savedFile);

            Model uploadedModel = ModelFactory.createDefaultModel();
            uploadedModel.read(inputStream, null);

            // 🔍 STEP 3: VALIDATION
            if (!uploadedModel.contains(null, RDF.type)) {
                return ResponseEntity.badRequest().body("Invalid OWL file");
            }

            // 📂 STEP 4: LOAD EXISTING MODELS
            Model masterModel = ModelFactory.createDefaultModel();
            masterModel.read(new ClassPathResource("owl/master.owl").getInputStream(), null);

            Model linkModel = ModelFactory.createDefaultModel();
            linkModel.read(new ClassPathResource("owl/link.owl").getInputStream(), null);

            // 🔗 STEP 5: LINKING
            addLinks(uploadedModel, linkModel, masterModel);

            // ➕ STEP 6: MERGE
            masterModel.add(uploadedModel);

            // 💾 STEP 7: SAVE UPDATED FILES
            String basePath = System.getProperty("user.dir") + "/src/main/resources/owl/";

            try (FileOutputStream masterOut = new FileOutputStream(basePath + "master.owl");
                 FileOutputStream linkOut = new FileOutputStream(basePath + "link.owl")) {

                masterModel.write(masterOut, "RDF/XML");
                linkModel.write(linkOut, "RDF/XML");
            }

            // 🔥 STEP 8: RELOAD ONTOLOGY
            ontologyLoader.reloadOntology();

            return ResponseEntity.ok("OWL uploaded, stored, linked, merged, and reloaded successfully 🚀");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error processing OWL file");
        }
    }
}