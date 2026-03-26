package com.semantic.search.model;

public class ProductResult {

    private String productName;
    private String source;
    private double price;
    private double rating;
    private String productLink;

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getProductLink() { return productLink; }
    public void setProductLink(String productLink) { this.productLink = productLink; }
}