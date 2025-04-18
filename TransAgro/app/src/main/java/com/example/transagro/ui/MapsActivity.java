package com.example.transagro.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.transagro.R;
import com.example.transagro.model.Delivery;
import com.example.transagro.viewmodel.DeliveryViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DeliveryViewModel viewModel;
    private FusedLocationProviderClient fusedLocationClient;
    private LatLng currentLocation;
    private int driverId;
    private Polyline currentPolyline;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Récupération de l'identifiant du driver depuis l'intent
        driverId = getIntent().getIntExtra("driverId", 0);
        Log.d("MapsActivity", "Driver Id reçu: " + driverId);

        // Obtention de la Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if(mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Initialisation du ViewModel et du FusedLocationProviderClient
        viewModel = new ViewModelProvider(this).get(DeliveryViewModel.class);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Vérifier et demander la permission de localisation
        checkAndRequestLocationPermission();
    }

    // Affichage du menu logout
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_logout, menu);
        return true;
    }

    // Gestion du clic sur l'item de déconnexion
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

    // OnMapReady est appelé lorsque la carte est initialisée
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        Log.d("MapsActivity", "onMapReady: Carte initialisée");

        // Active l'option "ma position" (point bleu) si la permission est accordée
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        // Si currentLocation est déjà disponible, repositionner la caméra
        if(currentLocation != null) {
            Log.d("MapsActivity", "Current Location (onMapReady): " + currentLocation.toString());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12));
        } else {
            // Sinon, essayer de le récupérer de nouveau
            getLastLocation();
        }

        // Observer les livraisons et afficher les marqueurs
        viewModel.getDeliveries(driverId).observe(this, deliveries -> {
            Log.d("MapsActivity", "Nombre de livraisons reçues: " + (deliveries != null ? deliveries.size() : 0));
            mMap.clear();
            if (deliveries != null) {
                for (Delivery d : deliveries) {
                    LatLng position = new LatLng(d.getLatitude(), d.getLongitude());
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(position)
                            .title(d.getAddress());
                    markerOptions.icon(d.isDelivered() ?
                            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN) :
                            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    Marker marker = mMap.addMarker(markerOptions);
                    marker.setTag(d);
                }
            }
        });

        // Déclenche le traçage de l'itinéraire lors d'un clic sur un marqueur
        mMap.setOnMarkerClickListener(marker -> {
            Delivery delivery = (Delivery) marker.getTag();
            if (delivery != null && currentLocation != null) {
                traceRoute(currentLocation, new LatLng(delivery.getLatitude(), delivery.getLongitude()));
            }
            return false;
        });

        // Marquer la livraison comme effectuée lors du clic sur la fenêtre d'info
        mMap.setOnInfoWindowClickListener(marker -> {
            Delivery delivery = (Delivery) marker.getTag();
            if (delivery != null && !delivery.isDelivered()) {
                viewModel.markDeliveryDelivered(delivery.getId());
                // Mettre à jour le marqueur en changeant son icône immédiatement
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                Toast.makeText(MapsActivity.this, "Livraison effectuée", Toast.LENGTH_SHORT).show();
                // Optionnel : déclencher une nouvelle récupération des livraisons assignées
                // viewModel.getDeliveries(driverId); // (si les LiveData se rafraîchissent automatiquement)
            }
        });
    }

    // Vérification et demande de la permission de localisation
    private void checkAndRequestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Si la permission est déjà accordée, obtenir la localisation
            getLastLocation();
        }
    }

    // Callback de la demande de permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("MapsActivity", "Permission de localisation accordée");
                getLastLocation();
                if(mMap != null) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    mMap.setMyLocationEnabled(true);
                }
            } else {
                Log.e("MapsActivity", "Permission de localisation refusée");
                Toast.makeText(this, "La permission de localisation est requise", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    // Méthode pour obtenir la localisation actuelle
    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    Log.d("MapsActivity", "Current Location: " + currentLocation.toString());
                    // Mise à jour de la caméra si la carte est déjà initialisée
                    if (mMap != null) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12));
                        // Affichage d'un marqueur indiquant la localisation actuelle
                        mMap.addMarker(new MarkerOptions()
                                .position(currentLocation)
                                .title("Vous êtes ici")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    }
                } else {
                    Log.w("MapsActivity", "getLastLocation retourne null. Vérifiez que le service de localisation est activé et que l’émulateur dispose d’une position configurée.");
                }
            });
        }
    }

    /**
     * Construit l'URL pour l'API Directions de Google.
     */
    private String getDirectionUrl(LatLng origin, LatLng destination) {
        String strOrigin = "origin=" + origin.latitude + "," + origin.longitude;
        String strDest = "destination=" + destination.latitude + "," + destination.longitude;
        String key = "VOTRE_CLEF_API"; // Remplacez par votre clé API Google Directions
        String parameters = strOrigin + "&" + strDest + "&key=" + key;
        String output = "json";
        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
    }

    /**
     * Lance le téléchargement et le tracé de l'itinéraire.
     */
    private void traceRoute(LatLng origin, LatLng destination) {
        if (currentPolyline != null) {
            currentPolyline.remove();
        }
        String url = getDirectionUrl(origin, destination);
        new FetchURL().execute(url);
    }

    /**
     * AsyncTask pour récupérer et parser l'itinéraire.
     */
    private class FetchURL extends AsyncTask<String, Void, List<LatLng>> {

        @Override
        protected List<LatLng> doInBackground(String... strings) {
            String data;
            try {
                data = downloadUrl(strings[0]);
                Log.d("FetchURL", "Data downloaded: " + data);
                return parseDirections(data);
            } catch (Exception e) {
                Log.e("FetchURL", "Erreur lors du téléchargement ou du parsing: " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<LatLng> points) {
            if (points != null && points.size() > 0) {
                PolylineOptions lineOptions = new PolylineOptions();
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(0xFF0000FF);  // Bleu
                currentPolyline = mMap.addPolyline(lineOptions);
                Log.d("FetchURL", "Polyline tracée avec " + points.size() + " points.");
            } else {
                Toast.makeText(MapsActivity.this, "Impossible de tracer l'itinéraire", Toast.LENGTH_SHORT).show();
                Log.e("FetchURL", "Aucun point reçu pour tracer l'itinéraire.");
            }
        }

        // Méthode downloadUrl inchangée
        private String downloadUrl(String strUrl) throws IOException {
            String data = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(strUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                iStream = urlConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                data = sb.toString();
                br.close();
            } catch (Exception e) {
                Log.e("DownloadUrl", "Erreur de téléchargement: " + e.getMessage());
                e.printStackTrace();
            } finally {
                if (iStream != null) iStream.close();
                if (urlConnection != null) urlConnection.disconnect();
            }
            return data;
        }

        private List<LatLng> parseDirections(String jsonData) {
            List<LatLng> route = new ArrayList<>();
            try {
                JSONObject jsonObject = new JSONObject(jsonData);
                String status = jsonObject.optString("status");
                if (!"OK".equals(status)) {
                    Log.e("ParseDirections", "API Directions returned status: " + status);
                    return route;
                }
                JSONArray routes = jsonObject.getJSONArray("routes");
                if (routes.length() > 0) {
                    JSONObject routeObject = routes.getJSONObject(0);
                    JSONObject overviewPolyline = routeObject.getJSONObject("overview_polyline");
                    String encodedString = overviewPolyline.getString("points");
                    route = decodePoly(encodedString);
                }
            } catch (JSONException e) {
                Log.e("ParseDirections", "Erreur de parsing: " + e.getMessage());
                e.printStackTrace();
            }
            return route;
        }

        private List<LatLng> decodePoly(String encoded) {
            List<LatLng> poly = new ArrayList<>();
            int index = 0, len = encoded.length();
            int lat = 0, lng = 0;
            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;
                shift = 0;
                result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;
                poly.add(new LatLng(lat / 1E5, lng / 1E5));
            }
            return poly;
        }
    }
}
