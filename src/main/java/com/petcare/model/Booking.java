package com.petcare.model;

public class Booking {
    private long id;
    private String name;
    private String email;
    private String number;
    private String petType;
    private long serviceId;

    public Booking() {}

    public Booking(long id, String name, String email, String number, String petType, long serviceId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.number = number;
        this.petType = petType;
        this.serviceId = serviceId;
    }
    
    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }
    public String getPetType() { return petType; }
    public void setPetType(String petType) { this.petType = petType; }
    public long getServiceId() { return serviceId; }
    public void setServiceId(long serviceId) { this.serviceId = serviceId; }
}
