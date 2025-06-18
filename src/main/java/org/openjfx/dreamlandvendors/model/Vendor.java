package org.openjfx.dreamlandvendors.model;

import java.time.LocalDateTime;

public class Vendor {
    private int vendorId;
    private String name;
    private String phoneNumber;
    private LocalDateTime createdAt;

    // Default constructor
    public Vendor() {
    }

    // Constructor with essential fields
    public Vendor(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    // Constructor with all fields
    public Vendor(int vendorId, String name, String phoneNumber, LocalDateTime createdAt) {
        this.vendorId = vendorId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getVendorId() {
        return vendorId;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return name;
    }
}
