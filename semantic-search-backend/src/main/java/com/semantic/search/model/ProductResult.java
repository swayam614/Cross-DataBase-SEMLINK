//package com.semantic.search.model;
//
//public class ProductResult {
//
//    private String productName;
//    private String source;
//    private double price;
//    private double rating;
//    private String productLink;
//
//    public String getProductName() { return productName; }
//    public void setProductName(String productName) { this.productName = productName; }
//
//    public String getSource() { return source; }
//    public void setSource(String source) { this.source = source; }
//
//    public double getPrice() { return price; }
//    public void setPrice(double price) { this.price = price; }
//
//    public double getRating() { return rating; }
//    public void setRating(double rating) { this.rating = rating; }
//
//    public String getProductLink() { return productLink; }
//    public void setProductLink(String productLink) { this.productLink = productLink; }
//}


package com.semantic.search.model;

public class ProductResult {

    private String productName;
    private String source;
    private Double price;   // ✅ changed
    private Double rating;  // ✅ changed
    private String productLink;

    // 🕒 NEW FIELD
    private String createdAt;

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public String getProductLink() { return productLink; }
    public void setProductLink(String productLink) { this.productLink = productLink; }

    // 🕒 GETTER & SETTER FOR TIMESTAMP
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}

