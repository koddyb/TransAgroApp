package com.example.transagro.repository;

import com.example.transagro.network.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdminRepository {

    private static AdminRepository instance;
    private ApiService apiService;

    private AdminRepository() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.220.140:8084/") //API
                //.baseUrl("http://192.168.1.85/API_TransAgro/") //Wifi de la maison
                //.baseUrl("http://10.0.220.49/API_TransAgro/") //Wifi ECole
                //.baseUrl("http://10.0.2.2:8084/") //quand j'utilise l'emulateur
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);
    }

    public static AdminRepository getInstance() {
        if (instance == null) {
            instance = new AdminRepository();
        }
        return instance;
    }

    // Supprimez assignVehicle puisque nous n'en avons plus besoin

    public void assignDelivery(int driverId, int deliveryId, final AdminRepository.AdminCallback callback) {
        apiService.assignDelivery(driverId, deliveryId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()){
                    callback.onSuccess("Livraison assignée avec succès");
                } else {
                    callback.onFailure("Echec de l'assignation: " + response.message());
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onFailure("Echec de l'appel API: " + t.getMessage());
            }
        });
    }


    public interface AdminCallback {
        void onSuccess(String message);
        void onFailure(String error);
    }
}
