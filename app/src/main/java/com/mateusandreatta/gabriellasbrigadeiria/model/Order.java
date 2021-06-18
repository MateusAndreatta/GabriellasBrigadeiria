package com.mateusandreatta.gabriellasbrigadeiria.model;

import com.google.firebase.firestore.DocumentId;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Order implements Serializable {

    @DocumentId
    private String firestoreId;
    private ArrayList<Product> products;
    private Client client;
    private String status; /* Concluido/Em Andamento */
    private String details;
    private Double total;
    private Double deliveryFee;
    private Date date;
    private String deliveryTime;
    private String paymentMethod;
    private boolean enable = true;
    private boolean delivery;

    public Order() {
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getFirestoreId() {
        return firestoreId;
    }

    public void setFirestoreId(String firestoreId) {
        this.firestoreId = firestoreId;
    }

    public Double getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(Double deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public boolean isDelivery() {
        return delivery;
    }

    public void setDelivery(boolean delivery) {
        this.delivery = delivery;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
