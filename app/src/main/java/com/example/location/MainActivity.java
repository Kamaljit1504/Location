package com.example.location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import static android.location.LocationManager.GPS_PROVIDER;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    LocationManager locationManager;
    LocationListener locationListener;
    private static final int RequestCode = 1;

    Button start, stop;
    TextView location_textView;
    /*
    *the other way to acces the location of user wth the
    * FusedLocation provider
     */


   FusedLocationProviderClient fusedLocationProvderClient;
   LocationRequest locationRequest;
   LocationCallback locationCallback;
   Location lastKnownlocation;

    @Override
    protected void onStart() {
        super.onStart();
        if(!checkPermission())
        {
            requestPermission();
        }
        else
        {
            getLastLocation();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        location_textView = findViewById(R.id.txt_Location);
        start = findViewById(R.id.btn_update);
        stop = findViewById(R.id.stopUpdate);

        //initailize fusedLocationProvderClient;
        fusedLocationProvderClient = LocationServices.getFusedLocationProviderClient(this);
        // initialize the location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                Log.i(TAG, "onLocationChanged: " + location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {


            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, RequestCode);
//        } else {
//            locationManager.requestLocationUpdates(GPS_PROVIDER, 0, 0, locationListener);
//        }
    }

    // allow or deny permiission

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;

            }

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        }

        start.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                fusedLocationProvderClient.removeLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                start.setEnabled(start.isEnabled());
                stop.setEnabled(!stop.isEnabled());
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                fusedLocationProvderClient.removeLocationUpdates(locationCallback);
                start.setEnabled(!start.isEnabled());
                stop.setEnabled(stop.isEnabled());
            }
        });
    }

    private void buildLocationRequest()
    {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10);

    }


    private void buildLocationCallBack()
    {
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                for(Location location: locationResult.getLocations())
                {
                    location_textView.setText(String.valueOf(location.getLatitude()) + "/" + String.valueOf(location.getLongitude()));
                }
            }
        };
    }

    private boolean checkPermission()
    {
       int permissionState = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;

    }

    private void requestPermission()
    {
        /*
        we provide additional rational to theuser when user has denied the permission
         */
        boolean shoudProvideRatonale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (shoudProvideRatonale)
        {
            Log.i(TAG, "requestpermission:" + "Displaying the permission rationale");
            //provide a way so that user can grant permission
        }
        else
        {
            startLocationPermissionRequest();
          //  requestPermission();
        }
    }
    private void startLocationPermissionRequest()
    {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},RequestCode);
    }

    private void getLastLocation()
    {
        fusedLocationProvderClient.getLastLocation().addOnCompleteListener(this, new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful() && task.getResult() != null)
                {
                    lastKnownlocation = task.getResult();
                    setLocation(lastKnownlocation);
                }
            }
        });
    }
    private void setLocation(Location location)
    {
        location_textView.setText(String.valueOf(location.getLatitude()) + "/" + String.valueOf(location.getLongitude()));
    }
   /* private void getLocation()
    {
        locationCallback = new LocationCallback()
        {
            @Override
            public void onLocationResult(LocationResult locationResult)
            {
                for(Location location: locationResult.getLocations());
            }
        };
    }*/

   private void showSnakbar(final int mainStringId, final int actionStringId, View.onClickListener listener)
   {
      Snackbar.make(findViewById(android.R.id.content), getString(mainStringId), Snack.LENGTH_INDEFINITE.setAction(actionStringId, list))
   }



}