package com.mateusandreatta.gabriellasbrigadeiria.model;

import java.io.Serializable;

public class Client implements Serializable {
    private String name;
    private String address;
    private String addressDetails;
    private String phone;

    public Client() {
    }

    public Client(String name) {
        this.name = name;
    }

    public Client(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public Client(String name, String address, String addressDetails, String phone) {
        this.name = name;
        this.address = address;
        this.addressDetails = addressDetails;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddressDetails() {
        return addressDetails;
    }

    public void setAddressDetails(String addressDetails) {
        this.addressDetails = addressDetails;
    }
}
