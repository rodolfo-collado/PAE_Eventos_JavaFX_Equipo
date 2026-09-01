package com.example.reto1.model;

import java.util.Objects;

public class Product {
    private String id;
    private String name;
    private double price;
    private double stock;

    public Product() {
    }

    public Product(String id, double price, String name, double stock) {
        this.id = id;
        this.price = price;
        this.name = name;
        this.stock = stock;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getStock() {
        return stock;
    }

    public void setStock(double stock) {
        this.stock = stock;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Double.compare(getPrice(), product.getPrice()) == 0 && Double.compare(getStock(), product.getStock()) == 0 && Objects.equals(getId(), product.getId()) && Objects.equals(getName(), product.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getPrice(), getStock());
    }

    @Override
    public String toString() {
        return String.format("[%s]  %s  |  Precio: $%.2f  |  Stock: %.0f", id, name, price, stock);
    }
}
