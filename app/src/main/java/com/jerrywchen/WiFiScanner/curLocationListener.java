package com.jerrywchen.WiFiScanner;

import android.location.LocationListener;
import android.location.*;

import android.os.Bundle;
/**
 * Created by Varun on 5/1/2017.
 */

public class curLocationListener implements LocationListener {

    /**Overkill for debuggin**/
    private Location myLocation;
    public double lattitude;
    private double longitude;

    public void onLocationChanged(Location location){
        myLocation = location;
        lattitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    public void onProviderDisabled(String arg0) {
        // TODO Auto-generated method stub

    }

    public void onProviderEnabled(String arg0) {
        // TODO Auto-generated method stub

    }

    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        // TODO Auto-generated method stub


    }

    public Location getMyLocation(){
        return myLocation;
    }

    public double getLattitude(){
        return lattitude;
    }

    public double getLongitude(){
        return longitude;
    }

}
