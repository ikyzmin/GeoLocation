package com.ssau.geolocation;

import android.*;
import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.ssau.geolocation.service.Constants;
import com.ssau.geolocation.service.FetchAddressIntentService;
import com.ssau.geolocation.util.MapUtil;

import java.util.ArrayList;

import static com.ssau.geolocation.R.id.map;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private MapFragment mapFragment;
    private LocationManager manager;
    private GoogleMap googleMap;
    private GoogleApiClient googleApiClient;
    private Location lastKnownLocation;
    private FloatingActionButton addRouteButton;
    private SlidingUpPanelLayout slidingUpPanelLayout;
    private AppCompatTextView routeStartTextView;
    private AppCompatTextView routeEndTextView;
    private AppCompatEditText routeName;
    private AppCompatButton createRouteButton;
    private AddressResultReceiver mResultReceiver;

    private final ArrayList<Marker> newRoutePoints = new ArrayList<>();
    private final ArrayList<Travel> travels = new ArrayList<>();
    private LocationManager locationManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_map);
        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);
        SlidingUpPanelLayout layout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        layout.setAnchorPoint(0.3f);
        slidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        addRouteButton = (FloatingActionButton) findViewById(R.id.add_route_button);
        addRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
            }
        });
        routeStartTextView = (AppCompatTextView) findViewById(R.id.route_start);
        routeEndTextView = (AppCompatTextView) findViewById(R.id.route_end);
        routeName = (AppCompatEditText) findViewById(R.id.route_name);
        createRouteButton = (AppCompatButton) findViewById(R.id.add_route);
        mResultReceiver = new AddressResultReceiver(new Handler(Looper.getMainLooper()));
        createRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newRoutePoints.size() == 2) {
                    travels.add(new Travel(newRoutePoints.get(0), newRoutePoints.get(1)));
                    ConnectAsyncTask connectAsyncTask = new ConnectAsyncTask(MapActivity.this, MapUtil.makeURL(newRoutePoints.get(0).getPosition().latitude, newRoutePoints.get(0).getPosition().longitude, newRoutePoints.get(1).getPosition().latitude, newRoutePoints.get(1).getPosition().longitude), googleMap, travels.size() - 1);
                    connectAsyncTask.execute();
                    newRoutePoints.clear();
                }
            }
        });
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,
                0, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        lastKnownLocation = location;
                    }

                    @Override
                    public void onStatusChanged(String s, int i, Bundle bundle) {

                    }

                    @Override
                    public void onProviderEnabled(String s) {

                    }

                    @Override
                    public void onProviderDisabled(String s) {

                    }
                });
    }

    protected void onStart() {
        super.onStart();
    }

    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkGPS() && googleMap != null) {
            googleMap.setMyLocationEnabled(true);
        }
    }

    protected void startIntentService(LatLng latLng) {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        Location location = new Location(manager.getBestProvider(criteria, true));
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, new Location(location));
        startService(intent);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (checkGPS()) {
            googleMap.setMyLocationEnabled(true);
        }
        double latitude = lastKnownLocation != null ? lastKnownLocation.getLatitude() : 0;
        double longitude = lastKnownLocation != null ? lastKnownLocation.getLongitude() : 0;
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 12.0f));
        googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if (lastKnownLocation != null) {
                    googleMap.addMarker(new MarkerOptions().position(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude())));
                    return true;
                }
                return false;
            }
        });
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED && newRoutePoints.size() != 2) {
                    Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng));
                    if (newRoutePoints.size() == 0) {
                        startIntentService(marker.getPosition());
                    } else {
                        startIntentService(marker.getPosition());
                    }
                    newRoutePoints.add(marker);
                }

            }
        });
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (newRoutePoints.contains(marker)) {
                    newRoutePoints.remove(marker);
                    marker.remove();
                }

                return false;
            }
        });
    }

    private boolean checkGPS() {
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void buildAlertMessageNoGps() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).setMessage(R.string.permission_request).setTitle(R.string.permission_title)
                .setPositiveButton(R.string.common_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent viewIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(viewIntent);
                    }
                }).setNegativeButton(R.string.common_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create();
        alertDialog.show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string
            // or an error message sent from the intent service.
            routeStartTextView.setText(resultData.getString(Constants.RESULT_DATA_KEY));
            routeEndTextView.setText(resultData.getString(Constants.RESULT_DATA_KEY));
            // Show a toast message if an address was found.

        }
    }
}
