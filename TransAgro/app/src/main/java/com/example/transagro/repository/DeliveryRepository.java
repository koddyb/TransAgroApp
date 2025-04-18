package com.example.transagro.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.transagro.model.Delivery;
import com.example.transagro.network.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DeliveryRepository {
    private static DeliveryRepository instance;
    private ApiService apiService;
    private MutableLiveData<List<Delivery>> deliveriesLiveData;

    private DeliveryRepository() {
        deliveriesLiveData = new MutableLiveData<>();
        Retrofit retrofit = new Retrofit.Builder()
                //.baseUrl("http://10.0.220.140:8084/") //API
                //.baseUrl("http://192.168.1.85/API_TransAgro/") //Wifi de la maison
                //.baseUrl("http://10.0.220.49/API_TransAgro/") //Wifi ECole
                .baseUrl("http://10.0.2.2:8084/") //quand j'utilise l'emulateur
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);
    }

    public static DeliveryRepository getInstance() {
        if (instance == null) {
            instance = new DeliveryRepository();
        }
        return instance;
    }

    public LiveData<List<Delivery>> getDeliveries(int driverId) {
        MutableLiveData<List<Delivery>> deliveriesLiveData = new MutableLiveData<>();
        apiService.getDeliveries(driverId).enqueue(new Callback<List<Delivery>>() {
            @Override
            public void onResponse(Call<List<Delivery>> call, Response<List<Delivery>> response) {
                if(response.isSuccessful()){
                    Log.d("DeliveryRepo", "Livraisons assignées reçues : " + (response.body() != null ? response.body().size() : 0));
                    deliveriesLiveData.postValue(response.body());
                } else {
                    Log.e("DeliveryRepo", "Erreur réponse: " + response.message());
                    deliveriesLiveData.postValue(null);
                }
            }
            @Override
            public void onFailure(Call<List<Delivery>> call, Throwable t) {
                Log.e("DeliveryRepo", "Echec appel API: " + t.getMessage());
                deliveriesLiveData.postValue(null);
            }
        });
        return deliveriesLiveData;
    }



    public void markDeliveryAsDelivered(int deliveryId) {
        apiService.markDeliveryAsDelivered(deliveryId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // Optionnel: mettre à jour localement si nécessaire
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Gestion d'erreur
            }
        });
    }

    public LiveData<List<Delivery>> getNonAssignedDeliveries() {
        MutableLiveData<List<Delivery>> nonAssignedDeliveries = new MutableLiveData<>();
        apiService.getNonAssignedDeliveries().enqueue(new Callback<List<Delivery>>() {
            @Override
            public void onResponse(Call<List<Delivery>> call, Response<List<Delivery>> response) {
                if(response.isSuccessful()){
                    Log.d("DeliveryRepo", "Livraisons non affectées reçues : " + response.body().size());
                    nonAssignedDeliveries.postValue(response.body());
                } else {
                    Log.e("DeliveryRepo", "Erreur réponse: " + response.message());
                    nonAssignedDeliveries.postValue(null);
                }
            }
            @Override
            public void onFailure(Call<List<Delivery>> call, Throwable t) {
                Log.e("DeliveryRepo", "Echec appel API: " + t.getMessage());
                nonAssignedDeliveries.postValue(null);
            }
        });
        return nonAssignedDeliveries;
    }


}
