package com.E2145049;

import android.content.Context;

import com.example.e2145049.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchData {

    private static final String OPEN_WEATHER_MAP_API = "http://api.openweathermap.org/data/2.5/weather?lat=%f&lon=%f&appid=%s&units=metric";

    public static JSONObject getJSON(Context context, double latitude, double longitude) {
        try {
            String apiKey = context.getString(R.string.open_weather_maps_app_id);
            URL url = new URL(String.format(OPEN_WEATHER_MAP_API, latitude, longitude, apiKey));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder json = new StringBuilder(1024);
            String tmp;
            while ((tmp = reader.readLine()) != null) {
                json.append(tmp).append("\n");
            }
            reader.close();

            JSONObject data = new JSONObject(json.toString());

            // Check if the request was successful
            if (data.getInt("cod") != 200) {
                return null;
            }

            return data;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
