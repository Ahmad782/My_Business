package com.mike.mybusiness;

public class Customer {
    String name;
    String number;
    String address;
    String dues;
    String email;

    public void setDues(String dues) {
        this.dues = dues;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public String getAddress() {
        return address;
    }

    public String getDues() {
        return dues;
    }

    public Customer(String name, String number, String address, String dues, String email) {
        this.name = name;
        this.number = number;
        this.address = address;
        this.dues = dues;
        this.email = email;
    }
}
