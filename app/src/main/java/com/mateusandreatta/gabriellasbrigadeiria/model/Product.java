package com.mateusandreatta.gabriellasbrigadeiria.model;

import com.google.firebase.firestore.DocumentId;

public class Product {

    @DocumentId
    String firestoreId;
    String name;
    Double price;

    public Product() {

    }

    public Product(String firestoreId, String name, Double price) {
        this.firestoreId = firestoreId;
        this.name = name;
        this.price = price;
    }

    public Product(String name, Double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getFirestoreId() {
        return firestoreId;
    }

    public void setFirestoreId(String firestoreId) {
        this.firestoreId = firestoreId;
    }

    @Override
    public String toString() {
        return name + " - " + "R$ " + price;
    }
}
