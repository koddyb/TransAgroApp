package com.example.transagro.model;

public class Driver {
    private int id;
    private String name;
    private String email;
    private String role; // "admin" ou "driver"

    public Driver(int id, String name, String email, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }

    // Vous pouvez ajouter aussi un setter si nécessaire :
    public void setRole(String role) { this.role = role; }
}
