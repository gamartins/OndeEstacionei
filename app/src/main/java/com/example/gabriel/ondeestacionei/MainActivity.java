package com.example.gabriel.ondeestacionei;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LocationProvider.LocationCallback, View.OnClickListener {
    private LocationProvider locationProvider;
    private TextView currentLatitudeTextView, currentLongitudeTextView;
    private TextView previousLatitudeTextView, previousLongitudeTextView;
    private Button handlePositionButton;
    private StorageProvider storageProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        storageProvider = new StorageProvider(getApplicationContext());
        locationProvider = new LocationProvider(this, this);

        currentLatitudeTextView = (TextView) findViewById(R.id.latitudeTextView);
        currentLongitudeTextView = (TextView) findViewById(R.id.longitudadeTextView);
        previousLatitudeTextView = (TextView) findViewById(R.id.previousLatitudeTextView);
        previousLongitudeTextView = (TextView) findViewById(R.id.previousLongitudeTextView);

        handlePositionButton = (Button) findViewById(R.id.handlePositionButton);
        handlePositionButton.setOnClickListener(this);
        if (storageProvider.isEmpty()) {
            handlePositionButton.setText(R.string.save);
        } else {
            handlePositionButton.setText(R.string.remove_position);
        }
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
        currentLongitudeTextView.setText(String.valueOf(location.getLongitude()));
        currentLatitudeTextView.setText(String.valueOf(location.getLatitude()));
    }

    @Override
    public void onClick(View view) {
        if (storageProvider.isEmpty()){
            Toast.makeText(this, "Save called", Toast.LENGTH_SHORT).show();

            Float latitude = Float.valueOf(currentLatitudeTextView.getText().toString());
            Float longitude = Float.valueOf(currentLongitudeTextView.getText().toString());

            storageProvider.setLatitude(latitude);
            storageProvider.setLongitude(longitude);

            previousLatitudeTextView.setText(storageProvider.getLatitude().toString());
            previousLongitudeTextView.setText(storageProvider.getLongitude().toString());

            handlePositionButton.setText(R.string.remove_position);

        } else {
            Toast.makeText(this, "Delete called", Toast.LENGTH_SHORT).show();
            storageProvider.eraseStorage();
            handlePositionButton.setText(R.string.save);

            previousLatitudeTextView.setText("");
            previousLongitudeTextView.setText("");
        }
    }
}
