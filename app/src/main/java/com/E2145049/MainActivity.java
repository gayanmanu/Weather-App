package com.E2145049;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.e2145049.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView textViewLatitudeLongitude;
    private TextView textViewAddress;
    private TextView textViewTime;
    private TextView textViewWeather;


    private static final String OPEN_WEATHER_MAP_API = "ac19072ea808b807d9a77fbc88aa1271"; // Replace with your API key

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewLatitudeLongitude = findViewById(R.id.textLatitudeLongitude);
        textViewAddress = findViewById(R.id.textAddress);
        textViewTime = findViewById(R.id.textTime);
        textViewWeather = findViewById(R.id.textWeather);

        // Check location permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        // Get location and update UI
        FetchLocation locationHelper = new FetchLocation(this);
        Location location = locationHelper.getLastKnownLocation();
        if (location != null) {
            updateLocationUI(location);
            new WeatherTask().execute(location.getLatitude(), location.getLongitude());
        }
    }

    @SuppressLint("SetTextI18n")
    private void updateLocationUI(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        // Display latitude and longitude
        textViewLatitudeLongitude.setText("Latitude: " + latitude + "\nLongitude: " + longitude);

        // Reverse geocode to get address
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            assert addresses != null;
            if (!addresses.isEmpty()) {
                String address = addresses.get(0).getAddressLine(0);
                textViewAddress.setText("Address: \n" + address);
                textViewWeather.setText("Mostly Cloudy");

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Display current system time
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentTime = sdf.format(new Date());
        textViewTime.setText("Time: \n" + currentTime);
    }

    private class WeatherTask extends AsyncTask<Double, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Double... params) {
            double latitude = params[0];
            double longitude = params[1];
            JSONObject jsonObject = null;
            try {
                jsonObject = FetchData.getJSON(MainActivity.this, latitude, longitude);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            try {
                if (jsonObject != null) {
                    JSONObject main = jsonObject.getJSONObject("main");
                    double temp = main.getDouble("temp");
                    double humidity = main.getDouble("humidity");

                    // Get weather description
                    JSONArray weatherArray = jsonObject.getJSONArray("weather");
                    JSONObject weatherObject = weatherArray.getJSONObject(0);
                    String description = weatherObject.getString("description");

                    textViewWeather.setText("Temperature: " + temp + "°C\nHumidity: " + humidity + "%\nDescription: " + description);
                } else {
                    Toast.makeText(getApplicationContext(), "Error fetching weather data!", Toast.LENGTH_SHORT).show(); // Display error toast
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
