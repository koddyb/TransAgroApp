package com.example.transagro.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.transagro.model.Delivery;
import com.example.transagro.repository.DeliveryRepository;

import java.util.List;

public class DeliveryViewModel extends AndroidViewModel {
    private DeliveryRepository repository;
    private LiveData<List<Delivery>> deliveries;
    private MutableLiveData<List<Delivery>> nonAssignedDeliveries;

    public DeliveryViewModel(@NonNull Application application) {
        super(application);
        repository = DeliveryRepository.getInstance();
        nonAssignedDeliveries = new MutableLiveData<>();
        loadNonAssignedDeliveries();
    }

    public LiveData<List<Delivery>> getDeliveries(int driverId) {
        return repository.getDeliveries(driverId);
    }

    public void markDeliveryDelivered(int deliveryId) {
        repository.markDeliveryAsDelivered(deliveryId);
    }
    private void loadNonAssignedDeliveries() {
        // Ici, on observe la LiveData renvoyée par le repository
        repository.getNonAssignedDeliveries().observeForever(new Observer<List<Delivery>>() {
            @Override
            public void onChanged(List<Delivery> deliveries) {
                nonAssignedDeliveries.setValue(deliveries);
            }
        });
    }

    // Méthode à appeler pour rafraîchir la liste des livraisons non affectées.
    public void refreshNonAssignedDeliveries() {
        // Relancer la récupération des données depuis le repository
        loadNonAssignedDeliveries();
    }
    public LiveData<List<Delivery>> getNonAssignedDeliveries() {
        return repository.getNonAssignedDeliveries();
    }
}
