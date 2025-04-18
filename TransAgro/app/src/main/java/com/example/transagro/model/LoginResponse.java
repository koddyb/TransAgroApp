package com.example.transagro.model;

public class LoginResponse {
    private boolean success;
    private Driver driver;
    private String error; // message d'erreur, le cas échéant

    // Constructeur par défaut requis par Gson
    public LoginResponse() { }

    public LoginResponse(boolean b, String message) {
    }

    // Getters
    public boolean isSuccess() { return success; }
    public Driver getDriver() { return driver; }
    public String getError() { return error; }
}
