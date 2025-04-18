package com.example.transagro.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.transagro.model.Driver;
import com.example.transagro.network.ApiService;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DriverRepository {
    private static DriverRepository instance;
    private ApiService apiService;

    private DriverRepository() {
        Retrofit retrofit = new Retrofit.Builder()
                //.baseUrl("http://10.0.220.140:8084/") //API
                //.baseUrl("http://192.168.1.85/API_TransAgro/") //Wifi de la maison
                //.baseUrl("http://10.0.220.49/API_TransAgro/") //Wifi ECole
                .baseUrl("http://10.0.2.2:8084/") //quand j'utilise l'emulateur
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);
    }

    public static DriverRepository getInstance() {
        if (instance == null) {
            instance = new DriverRepository();
        }
        return instance;
    }

    public LiveData<List<Driver>> getDriversByCity(String ville) {
        MutableLiveData<List<Driver>> driversLiveData = new MutableLiveData<>();
        apiService.getDriversByCity(ville).enqueue(new Callback<List<Driver>>() {
            @Override
            public void onResponse(Call<List<Driver>> call, Response<List<Driver>> response) {
                if(response.isSuccessful()){
                    driversLiveData.postValue(response.body());
                } else {
                    driversLiveData.postValue(null);
                }
            }
            @Override
            public void onFailure(Call<List<Driver>> call, Throwable t) {
                driversLiveData.postValue(null);
            }
        });
        return driversLiveData;
    }
}
