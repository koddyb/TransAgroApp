package com.example.transagro.repository;

import com.example.transagro.model.LoginResponse;
import com.example.transagro.network.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AuthRepository {
    private static AuthRepository instance;
    private ApiService apiService;

    private AuthRepository() {
        Retrofit retrofit = new Retrofit.Builder()
                //.baseUrl("http://10.0.220.140:8084/") //API
                //.baseUrl("http://192.168.1.85/API_TransAgro/") //Wifi de la maison
                //.baseUrl("http://10.0.220.49/API_TransAgro/") //Wifi ECole
                .baseUrl("http://10.0.2.2:8084/") //quand j'utilise l'emulateur
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);
    }

    public static AuthRepository getInstance() {
        if (instance == null) {
            instance = new AuthRepository();
        }
        return instance;
    }

    public void login(String email, String password, final AuthCallback callback) {
        Call<LoginResponse> call = apiService.login(email, password);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if(response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure("Erreur lors de la connexion");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    public interface AuthCallback {
        void onSuccess(LoginResponse loginResponse);
        void onFailure(String message);
    }
}
