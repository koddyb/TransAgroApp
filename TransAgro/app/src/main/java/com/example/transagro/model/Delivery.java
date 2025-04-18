package com.example.transagro.model;

public class Delivery {
    private int id;
    private String address;
    private double latitude;
    private double longitude;
    private boolean delivered;
    private String ville; // Ajout du champ ville

    public Delivery(int id, String address, double latitude, double longitude, boolean delivered, String ville) {
        this.id = id;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.delivered = delivered;
        this.ville = ville; // Initialisation du champ ville
    }

    // Getters existants...
    public int getId() { return id; }
    public String getAddress() { return address; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public boolean isDelivered() { return delivered; }
    public void setDelivered(boolean delivered) { this.delivered = delivered; }

    // Nouveau getter pour la ville
    public String getVille() {
        return ville;
    }

    // Si besoin, ajoutez un setter pour ville
    public void setVille(String ville) {
        this.ville = ville;
    }
}
