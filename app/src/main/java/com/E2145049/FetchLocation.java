package com.E2145049;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

public class FetchLocation {

    private Context context;

    public FetchLocation(Context context) {
        this.context = context;
    }

    public Location getLastKnownLocation() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                return locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        }
        return null;
    }
}
