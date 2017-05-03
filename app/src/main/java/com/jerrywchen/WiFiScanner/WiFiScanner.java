package com.jerrywchen.WiFiScanner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.zip.DataFormatException;

import android.location.Criteria;
import android.util.Log;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import android.net.wifi.WifiManager;
import android.net.wifi.WifiInfo;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.content.ContextCompat;

import com.jerrywchen.WiFiScanner.curLocationListener;

public class WiFiScanner extends AppCompatActivity {

    protected LocationManager lm;

    protected List<Double> longitudes = new ArrayList<Double>();
    protected List<Double> latitudes = new ArrayList<Double>();
    protected List<Integer> signalStrength = new ArrayList<Integer>();
    private int idx = 0;
    Button calcButton;

    private curLocationListener myLocationListener = new curLocationListener();
    private Triangulation triangulation; // = new Triangulation();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        calcButton = (Button) findViewById(R.id.button2);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            }
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            }
        }
    }




    public List<Address> getAddress(Location location){
        Geocoder geocoder = new Geocoder(getBaseContext(), Locale.US);
        List<Address> addresses = null;

        try{
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
         //   return  addresses;

        }
        catch(IOException e) {
            System.out.println("Cannot find location");
        }

        return addresses;
    }

    public void onButtonTapLog(View v) {
        Context context = this;
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        int numberOfLevels = 10;
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int level = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), numberOfLevels);
        int rssi = wifiInfo.getRssi();

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            }
        }


        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        Criteria criteria = new Criteria();
        String provider = lm.getBestProvider(criteria, true);
        lm.requestLocationUpdates(provider, 0, 0, myLocationListener);

        /***Updated Location***/

        double latitude = myLocationListener.getLattitude();
        double longitude = myLocationListener.getLongitude();

        /*
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        */
        signalStrength.add(rssi);
        longitudes.add(longitude);
        latitudes.add(latitude);
        idx++;
        if(idx >= 3)
            calcButton.setEnabled(true);

        Toast myToast = Toast.makeText(getApplicationContext(), "Saved location " + String.valueOf(idx), Toast.LENGTH_LONG);
        myToast.show();
    }

    public void onButtonTapCalculate(View v) {
        //Toast myToast= Toast.makeText(getApplicationContext(), "Calculating...", Toast.LENGTH_LONG);
        //myToast.show();

        if(latitudes.isEmpty() || longitudes.isEmpty()) {
            Toast invalidToast = Toast.makeText(getApplicationContext(), "Invalid",Toast.LENGTH_LONG);
            invalidToast.show();
            return;
        }
        Toast myToast = Toast.makeText(getApplicationContext(), "Calculating...", Toast.LENGTH_LONG);

        String latitude= String.valueOf(latitudes.get(0));
        String longitude = String.valueOf(longitudes.get(0));


        //Initiate triangulation
        triangulation = new Triangulation(latitudes,longitudes,signalStrength);

        //In order to find changes lets take the last known location
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            }
        }


        Location lastKnownLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        double lastKnownLattitude = lastKnownLocation.getLatitude();
        double lastKnownLongitude = lastKnownLocation.getLongitude();

        Toast.makeText(this,"Beginning Data Mining",Toast.LENGTH_LONG).show();

        //Change in RSSI per change in lattitude
        triangulation.testHorizontalTriangulation(lastKnownLattitude);

        //Change in RSSI per change in longitude
        triangulation.testVerticalTriangulation(lastKnownLongitude);

        //Total RSSI change


        //Finish Triangulation
        triangulation.finalizeTriangulation();


        WebView webview = (WebView) findViewById(R.id.webview);
        webview.setWebViewClient(new WebViewClient());
        String url = "https://maps.googleapis.com/maps/api/staticmap?center=" + latitude + "," + longitude + "&zoom=18&size=500x500&maptype=roadmap&markers=color:red%7Clabel:%7C" + latitude + "," + longitude + "&key=AIzaSyCVC9cfI0q5WR9LkWREaCAPOglTyC8FegE";
        webview.loadUrl(url);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

