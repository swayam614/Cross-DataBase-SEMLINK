package com.semantic.search.utils;

import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.datatypes.xsd.XSDDatatype;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class TimestampUpdater {

    public static void main(String[] args) {
        try {

            // 📂 Folder containing all OWL files
            File folder = new File("src/main/resources/owl/");
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".owl"));

            if (files == null) {
                System.out.println("❌ No OWL files found!");
                return;
            }

            String ns = "http://www.semanticsearch.com/schema#";
            String defaultTime = "2020-01-01T00:00:00";

            for (File file : files) {

                System.out.println("\n📂 Processing: " + file.getName());

                // 🔹 Create model
                Model model = ModelFactory.createDefaultModel();

                // 🔹 Load file safely (handles spaces)
                FileInputStream input = new FileInputStream(file);
                model.read(input, null);

                // 🔹 Get timestamp property
                Property timestampProp = model.getProperty(ns + "timestamp");

                // 🔹 Find all products
                ResIterator products = model.listResourcesWithProperty(
                        RDF.type, model.getResource(ns + "Product")
                );

                while (products.hasNext()) {
                    Resource product = products.nextResource();

                    // 🔹 Add timestamp if missing
                    if (!product.hasProperty(timestampProp)) {

                        product.addProperty(
                                timestampProp,
                                model.createTypedLiteral(defaultTime, XSDDatatype.XSDdateTime)
                        );

                        System.out.println("✅ Added timestamp: " + product.getURI());
                    }
                }

                // 🔹 Save updated file
                FileOutputStream output = new FileOutputStream(file);
                model.write(output, "RDF/XML-ABBREV");

                System.out.println("💾 Saved: " + file.getName());
            }

            System.out.println("\n🎉 ALL FILES UPDATED SUCCESSFULLY!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}