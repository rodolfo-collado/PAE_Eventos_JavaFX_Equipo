package com.example.reto2.model;

import java.util.Date;
import java.util.Objects;

public class Batch {
    private String id;
    private String provider;
    private Date date;
    private Double weight;

    public Batch() {
    }

    public Batch(String provider, String id, Date date, Double weight) {
        this.provider = provider;
        this.id = id;
        this.date = date;
        this.weight = weight;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Batch batch = (Batch) o;
        return Objects.equals(getId(), batch.getId()) && Objects.equals(getProvider(), batch.getProvider()) && Objects.equals(getDate(), batch.getDate()) && Objects.equals(getWeight(), batch.getWeight());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getProvider(), getDate(), getWeight());
    }

    @Override
    public String toString() {
        return "Batch{" +
                "id='" + id + '\'' +
                ", provider='" + provider + '\'' +
                ", date=" + date +
                ", weight=" + weight +
                '}';
    }
}
