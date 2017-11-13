package com.example.bassi.whome;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.PolyUtil;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationListener locationListener;
    private final static int My_PERMISSIONS_FINE_LOCATION = 101;
    private RequestButtons buttonManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(userLocation).title("Your location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));

                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                try {

                    List<Address> listAddresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                    if(listAddresses != null && listAddresses.size() > 0){

                        Log.i("PlaceInfo", listAddresses.get(0).toString());

                    }

                }
                catch (IOException e) {
                    e.printStackTrace();
                }

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
        buttonManager = new RequestButtons();
        RequestButtons fragment = new RequestButtons();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        System.out.println("WOWOWOWOWOWOWOWOWOW");
        getPermissions(googleMap);

        LatLng userLocation = initLocation(googleMap);
        //THIS CHECKS THE TIME!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        if (!checkTime()) {
            //display message that says walkhome isn't running at this time
            System.out.println("timeout");
        } else {
            // THIS CREATES THE BOUNDS FROM A SET OF LATITUDE AND LONGITUDE COORDS
            PolygonOptions bounds = createMapBounds(googleMap);
            //makes the shape visible
            bounds.strokeColor(Color.BLACK);
            //add the polygon to the map
            googleMap.addPolygon(bounds);
            //checks if the user's location is within the walkhome range
            if (PolyUtil.containsLocation(userLocation, bounds.getPoints(), false)) {



            }
        }


    }
    //the only methods you need to copy are the first two under this one
    private boolean checkTime() {
        Calendar current = Calendar.getInstance();
        int day = current.get(current.DAY_OF_WEEK);
        int hour = current.get(current.HOUR_OF_DAY);
        System.out.println("d "+day);
        System.out.println(hour);
        if (day == 1) {
            //sunday
            if (hour < 20 && hour > 1) {
                return false;
            }
        } else if (day == 2 || day == 3) {
            //monday and tuesday
            if (hour < 20 && hour > 2) {
                return false;
            }
        } else if (day == 4 || day == 5 || day == 6 || day == 7) {
            //wednesday to saturday
            if (hour < 20 && hour > 3) {
                return false;
            }
        } else {
            return true;
        }
        return true;
    }

    public PolygonOptions createMapBounds(GoogleMap googleMap) {
        PolygonOptions bounds = new PolygonOptions()
                .add(new LatLng(44.220396, -76.491977),
                        new LatLng(44.224682, -76.479700),
                        new LatLng(44.231664, -76.475201),
                        new LatLng(44.231664, -76.475201),
                        new LatLng(44.241202, -76.511102),
                        new LatLng(44.240126, -76.523154),
                        new LatLng(44.232909, -76.522725),
                        new LatLng(44.232079, -76.520970),
                        new LatLng(44.232311,-76.518228),
                        new LatLng(44.230778, -76.517702),
                        new LatLng(44.223212, -76.516894),
                        new LatLng(44.221833, -76.513745),
                        new LatLng(44.221833, -76.513745));

        return bounds;


    }


    public void getPermissions(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, My_PERMISSIONS_FINE_LOCATION);
            }
        }
    }

    public LatLng initLocation(GoogleMap googleMap) {
        mMap = googleMap;
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = service.getBestProvider(criteria, false);
        Location location;
        location = getLastKnownLocation();
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            service.requestLocationUpdates(provider, 500, 5, locationListener);
        }
        return userLocation;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case My_PERMISSIONS_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "This app requires permissions enabled to run", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;

        }
    }
    private Location getLastKnownLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            for (String provider : providers) {
                Location l = locationManager.getLastKnownLocation(provider);

                if (l == null) {
                    continue;
                }
                if (bestLocation == null
                        || l.getAccuracy() < bestLocation.getAccuracy()) {
                    System.out.println("found best last known location: %s");
                    bestLocation = l;
                }
            }
        }
        System.out.println("best location" + bestLocation);
        if (bestLocation == null) {
            return null;
        }
        return bestLocation;
    }
}

