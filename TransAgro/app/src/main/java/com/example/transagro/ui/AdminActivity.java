package com.example.transagro.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.example.transagro.R;
import com.example.transagro.model.Delivery;
import com.example.transagro.model.Driver;
import com.example.transagro.repository.AdminRepository;
import com.example.transagro.repository.DriverRepository;
import com.example.transagro.viewmodel.DeliveryViewModel;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private ListView lvDeliveries;
    private DeliveryViewModel deliveryViewModel;
    private List<Delivery> nonAssignedDeliveries = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        lvDeliveries = findViewById(R.id.lv_deliveries);

        // Charger les livraisons non affectées
        deliveryViewModel = new DeliveryViewModel(getApplication());
        deliveryViewModel.getNonAssignedDeliveries().observe(this, new Observer<List<Delivery>>() {
            @Override
            public void onChanged(List<Delivery> deliveries) {
                if (deliveries != null) {
                    nonAssignedDeliveries = deliveries;
                    List<String> items = new ArrayList<>();
                    for (Delivery d : deliveries) {
                        items.add("ID:" + d.getId() + " - " + d.getAddress());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(AdminActivity.this,
                            android.R.layout.simple_list_item_1, items);
                    lvDeliveries.setAdapter(adapter);
                } else {
                    Toast.makeText(AdminActivity.this, "Pas de livraisons non affectées", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Lorsqu'on clique sur une livraison, ouvrir le modal pour assigner un chauffeur
        lvDeliveries.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Delivery selectedDelivery = nonAssignedDeliveries.get(position);
                showAssignDialog(selectedDelivery);
            }
        });
    }

    // Création du menu logout dans l'action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Affichage du dialog pour assigner un chauffeur à la livraison sélectionnée
    private void showAssignDialog(final Delivery delivery) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Assigner la livraison ID: " + delivery.getId());

        // Créer un Spinner pour afficher la liste des chauffeurs de la ville concernée
        final Spinner spinnerDrivers = new Spinner(this);

        // Récupérer la liste des chauffeurs via l'API pour la ville de la livraison
        DriverRepository.getInstance().getDriversByCity(delivery.getVille()).observe(this, new Observer<List<Driver>>() {
            @Override
            public void onChanged(List<Driver> drivers) {
                if (drivers != null && !drivers.isEmpty()) {
                    List<String> driverNames = new ArrayList<>();
                    for (Driver d : drivers) {
                        driverNames.add("ID:" + d.getId() + " - " + d.getName());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(AdminActivity.this,
                            android.R.layout.simple_spinner_dropdown_item, driverNames);
                    spinnerDrivers.setAdapter(adapter);
                } else {
                    Toast.makeText(AdminActivity.this, "Aucun chauffeur trouvé dans la ville " + delivery.getVille(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setView(spinnerDrivers);
        builder.setPositiveButton("Assigner", (dialog, which) -> {
            if (spinnerDrivers.getSelectedItem() == null) {
                Toast.makeText(AdminActivity.this, "Veuillez choisir un chauffeur", Toast.LENGTH_SHORT).show();
                return;
            }
            // Récupérer l'ID du chauffeur sélectionné à partir du Spinner
            String selected = spinnerDrivers.getSelectedItem().toString();
            String idString = selected.split("-")[0].trim(); // Ex. "ID: 2"
            int selectedDriverId = Integer.parseInt(idString.replace("ID:", "").trim());

            // Log pour vérifier que l'appel est effectué
            Log.d("AdminActivity", "AssignDelivery appelé avec driverId: " + selectedDriverId + " et deliveryId: " + delivery.getId());

            // Appeler l'assignation via AdminRepository
            AdminRepository.getInstance().assignDelivery(selectedDriverId, delivery.getId(), new AdminRepository.AdminCallback() {
                @Override
                public void onSuccess(String message) {
                    runOnUiThread(() -> {
                        Toast.makeText(AdminActivity.this, message, Toast.LENGTH_SHORT).show();
                        // Rafraîchit la liste des livraisons non affectées
                        deliveryViewModel.refreshNonAssignedDeliveries();
                    });
                }
                @Override
                public void onFailure(String error) {
                    runOnUiThread(() -> Toast.makeText(AdminActivity.this, error, Toast.LENGTH_SHORT).show());
                }
            });

        });

        builder.setNegativeButton("Annuler", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}
