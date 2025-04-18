package com.example.transagro.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Query;
import com.example.transagro.model.Delivery;
import com.example.transagro.model.Driver;
import com.example.transagro.model.LoginResponse;
import java.util.List;

public interface ApiService {

    @FormUrlEncoded
    @POST("login.php")
    Call<LoginResponse> login(@Field("email") String email, @Field("password") String password);

    @GET("getDeliveries.php")
    Call<List<Delivery>> getDeliveries(@Query("driverId") int driverId);

    @FormUrlEncoded
    @POST("markDelivery.php")
    Call<Void> markDeliveryAsDelivered(@Field("deliveryId") int deliveryId);

    // Endpoints d'assignation pour la partie administrateur
//    @FormUrlEncoded
//    @POST("assignVehicle.php")
//    Call<Void> assignVehicle(@Field("driverId") int driverId, @Field("vehicleId") int vehicleId);

    @GET("getNonAssignedDeliveries.php")
    Call<List<Delivery>> getNonAssignedDeliveries();

    @GET("getDriversByCity.php")
    Call<List<Driver>> getDriversByCity(@Query("ville") String ville);

    @FormUrlEncoded
    @POST("assignDelivery.php")
    Call<Void> assignDelivery(@Field("driverId") int driverId, @Field("deliveryId") int deliveryId);
}
