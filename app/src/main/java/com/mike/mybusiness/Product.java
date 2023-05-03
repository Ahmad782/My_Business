package com.mike.mybusiness;

public class Product {
    String name;
    String barcode;
    String stock;
    float price;
    String stockDate;
    String company;
    String expiry;

    public String getName() {
        return name;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getStock() {
        return stock;
    }

    public String getStockDate() {
        return stockDate;
    }

    public String getCompany() {
        return company;
    }

    public String getExpiry() {
        return expiry;
    }

    public Bill getBillItem() {
        return new Bill(name, company, 1, price);
    }

    public Product(String name, String barcode, String stock, float price, String stockDate, String company, String expiry) {
        this.name = name;
        this.barcode = barcode;
        this.stock = stock;
        this.price = price;
        this.stockDate = stockDate;
        this.company = company;
        this.expiry = expiry;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public float getPrice() {
        return price;
    }
}
