package com.example.gabriel.ondeestacionei;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static com.example.gabriel.ondeestacionei.LocationProvider.REQUEST_PERMISSION_ACCESS_FINE_LOCATION;

public class MainActivity extends FragmentActivity implements
        LocationProvider.LocationCallback, View.OnClickListener, OnMapReadyCallback {

    private LocationProvider locationProvider;
    private TextView currentLatitudeTextView, currentLongitudeTextView;
    private TextView previousLatitudeTextView, previousLongitudeTextView;
    private Button handlePositionButton;
    private StorageProvider storageProvider;
    private GoogleMap mMap;
    private LatLng posicaoSalva;
    private SupportMapFragment mapFragment;
    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_container);

        // Requisitando as permissões de localização
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSION_ACCESS_FINE_LOCATION);
        } else {
            locationProvider = new LocationProvider(this, this);
            mapFragment.getMapAsync(this);
        }

        storageProvider = new StorageProvider(getApplicationContext());

        currentLatitudeTextView = (TextView) findViewById(R.id.latitudeTextView);
        currentLongitudeTextView = (TextView) findViewById(R.id.longitudadeTextView);
        previousLatitudeTextView = (TextView) findViewById(R.id.previousLatitudeTextView);
        previousLongitudeTextView = (TextView) findViewById(R.id.previousLongitudeTextView);

        handlePositionButton = (Button) findViewById(R.id.handlePositionButton);
        handlePositionButton.setOnClickListener(this);
        if (storageProvider.isEmpty()) {
            handlePositionButton.setText(R.string.save);
        } else {
            posicaoSalva = new LatLng(storageProvider.getLatitude(), storageProvider.getLongitude());

            previousLongitudeTextView.setText(storageProvider.getLongitude().toString());
            previousLatitudeTextView.setText(storageProvider.getLatitude().toString());

            handlePositionButton.setText(R.string.remove_position);
        }
    }

    @Override
    protected void onStart() {
        if (locationProvider !=null) locationProvider.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        if (locationProvider !=null) locationProvider.disconnect();
        super.onStop();
    }

    @Override
    public void handleNewLocation(Location location) {
        currentLongitudeTextView.setText(String.valueOf(location.getLongitude()));
        currentLatitudeTextView.setText(String.valueOf(location.getLatitude()));
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if (mMap != null && this.location == null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        } else {
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        }

        this.location = location;
    }

    @Override
    public void onClick(View view) {
        if (storageProvider.isEmpty()) {
            Toast.makeText(this, "Save called", Toast.LENGTH_SHORT).show();

            Float latitude = Float.valueOf(currentLatitudeTextView.getText().toString());
            Float longitude = Float.valueOf(currentLongitudeTextView.getText().toString());

            storageProvider.setLatitude(latitude);
            storageProvider.setLongitude(longitude);

            previousLatitudeTextView.setText(storageProvider.getLatitude().toString());
            previousLongitudeTextView.setText(storageProvider.getLongitude().toString());

            // Criando um marcador com a posição atual
            posicaoSalva = new LatLng(storageProvider.getLatitude(), storageProvider.getLongitude());
            mMap.addMarker(new MarkerOptions().position(posicaoSalva).title("Posição salva"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posicaoSalva, 17));

            handlePositionButton.setText(R.string.remove_position);

        } else {
            Toast.makeText(this, "Delete called", Toast.LENGTH_SHORT).show();
            storageProvider.eraseStorage();
            handlePositionButton.setText(R.string.save);

            previousLatitudeTextView.setText("");
            previousLongitudeTextView.setText("");

            // Remove os marcadores do mapa.
            mMap.clear();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (posicaoSalva != null) {
            mMap.addMarker(new MarkerOptions().position(posicaoSalva).title("Posição salva"));
        }

        // Verificando permissões
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSION_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationProvider = new LocationProvider(this, this);
                    mapFragment.getMapAsync(this);
                }
            }

        }
    }
}
