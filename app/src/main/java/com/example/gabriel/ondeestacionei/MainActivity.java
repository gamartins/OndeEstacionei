package com.example.gabriel.ondeestacionei;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LocationProvider.LocationCallback {
    private LocationProvider locationProvider;
    private TextView latitudeTextView, longitudeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latitudeTextView = (TextView) findViewById(R.id.latitudeTextView);
        longitudeTextView = (TextView) findViewById(R.id.longitudadeTextView);

        locationProvider = new LocationProvider(this, this);
    }

    @Override
    protected void onStart() {
        locationProvider.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        locationProvider.disconnect();
        super.onStop();
    }

    @Override
    public void handleNewLocation(Location location) {
        longitudeTextView.setText(String.valueOf(location.getLongitude()));
        latitudeTextView.setText(String.valueOf(location.getLatitude()));
    }
}
