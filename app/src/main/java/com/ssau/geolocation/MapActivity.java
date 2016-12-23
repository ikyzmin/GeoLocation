package com.ssau.geolocation;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.ssau.geolocation.service.Constants;
import com.ssau.geolocation.service.FetchAddressIntentService;
import com.ssau.geolocation.util.MapUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import static com.ssau.geolocation.R.id.map;
import static com.ssau.geolocation.R.id.marker_icon;

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
    private AppCompatImageButton markerIconButton;
    private DrawerLayout drawerLayout;
    private RecyclerView menuRecyclerView;
    private AppCompatButton createRouteButton;
    private ActionBarDrawerToggle menuToggle;
    private AddressResultReceiver mResultReceiver;
    private int selectedIndex = 0;
    private final int[] markersIcon = new int[]{R.mipmap.arrow_marker,R.mipmap.android_marker, R.mipmap.marker_nota};
    private Toolbar toolbar;
    private final ArrayList<Marker> notLinkedMarkers = new ArrayList<>();
    private final ArrayList<Polyline> lines = new ArrayList<>();
    private String location;
    private final ArrayList<Marker> newRoutePoints = new ArrayList<>();
    private final ArrayList<Travel> travels = new ArrayList<>();
    private LocationManager locationManager;

    private BroadcastReceiver roadNotFoundReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            travels.get(travels.size() - 1).dest.remove();
            travels.get(travels.size() - 1).origin.remove();
            travels.remove(travels.size() - 1);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_map);
        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);
        SlidingUpPanelLayout layout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        layout.setAnchorPoint(0.4f);
        menuRecyclerView = (RecyclerView) findViewById(R.id.menu_content);
        menuRecyclerView.setAdapter(new MenuAdapter(Arrays.asList(com.ssau.geolocation.menu.MenuItem.values()), this));

        slidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        addRouteButton = (FloatingActionButton) findViewById(R.id.add_route_button);
        addRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
            }
        });
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        markerIconButton = (AppCompatImageButton) findViewById(R.id.marker_icon);
        markerIconButton.setImageResource(markersIcon[selectedIndex%markersIcon.length]);
        markerIconButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedIndex++;
                selectedIndex %= markersIcon.length;
                markerIconButton.setImageResource(markersIcon[selectedIndex % markersIcon.length]);

            }
        });
        drawerLayout = (DrawerLayout) findViewById(R.id.menu);
        setSupportActionBar(toolbar);
        menuToggle = new ActionBarDrawerToggle(this, drawerLayout,
                toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        menuToggle.syncState();
        routeStartTextView = (AppCompatTextView) findViewById(R.id.route_start);
        routeEndTextView = (AppCompatTextView) findViewById(R.id.route_end);
        routeName = (AppCompatEditText) findViewById(R.id.route_name);
        createRouteButton = (AppCompatButton) findViewById(R.id.add_route);
        routeName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                createRouteButton.setEnabled(s.length() != 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mResultReceiver = new AddressResultReceiver(new Handler(Looper.getMainLooper()));
        createRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newRoutePoints.size() == 2) {
                    Travel travel = new Travel(routeName.getText().toString(), newRoutePoints.get(0), newRoutePoints.get(1));
                    ConnectAsyncTask connectAsyncTask = new ConnectAsyncTask(MapActivity.this, MapUtil.makeURL(newRoutePoints.get(0).getPosition().latitude, newRoutePoints.get(0).getPosition().longitude, newRoutePoints.get(1).getPosition().latitude, newRoutePoints.get(1).getPosition().longitude), googleMap, travels.size() - 1);
                    String result = null;
                    try {
                        result = connectAsyncTask.execute().get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                    Polyline polyline = MapUtil.drawPath(googleMap, result, LocationStore.getInstance().getTravels().size());
                    if (polyline == null) {
                        Toast.makeText(MapActivity.this, "Дорога не найдена", Toast.LENGTH_LONG).show();
                        travel.dest.remove();
                        travel.origin.remove();
                        updateMap();
                    } else {
                        LocationStore.getInstance().addLine(polyline);
                        LocationStore.getInstance().addTravel(travel);
                    }
                    newRoutePoints.clear();
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                    routeName.setText("");
                    routeStartTextView.setText("");
                    routeEndTextView.setText("");
                }
            }
        });
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
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

    private void updateMap() {
        if (googleMap != null) {
            googleMap.clear();
            for (MarkerItem marker : LocationStore.getInstance().getNotLinkedMarkers()) {
                googleMap.addMarker(new MarkerOptions().position(marker.marker.getPosition()).icon(BitmapDescriptorFactory.fromResource(marker.icon)));
            }
            for (Travel travel : LocationStore.getInstance().getTravels()) {
                googleMap.addMarker(new MarkerOptions().position(travel.origin.getPosition()));
                googleMap.addMarker(new MarkerOptions().position(travel.dest.getPosition()));
            }

            for (Polyline polyline : LocationStore.getInstance().getLines()) {
                googleMap.addPolyline(new PolylineOptions()
                        .addAll(polyline.getPoints())
                        .width(12)
                        .color(polyline.getColor())//Google maps blue color
                        .geodesic(true)
                );
            }
        }
    }

    protected void onStart() {
        super.onStart();
    }

    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.search) {
            addMarkerByString();
        }
        if (menuToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkGPS() && googleMap != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            googleMap.setMyLocationEnabled(true);
        }
        registerReceiver(roadNotFoundReceiver, new IntentFilter(ConnectAsyncTask.ROAD_NOT_FOUND_ACTION));
        updateMap();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(roadNotFoundReceiver);
    }

    protected void startIntentService(LatLng latLng, int requestCode) {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        Location location = new Location(manager.getBestProvider(criteria, true));
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, new Location(location));
        intent.putExtra(Constants.REQUEST_CODE_EXTRA, requestCode);
        startService(intent);
    }

    protected void startIntentService(String location, int requestCode) {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        intent.putExtra(Constants.REQUSTED_STRING_LOCATION, location);
        intent.putExtra(Constants.REQUEST_CODE_EXTRA, requestCode);
        startService(intent);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
        updateMap();
        if (checkGPS()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            }
            googleMap.setMyLocationEnabled(true);
        }
        double latitude = lastKnownLocation != null ? lastKnownLocation.getLatitude() : 0;
        double longitude = lastKnownLocation != null ? lastKnownLocation.getLongitude() : 0;
        googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if (lastKnownLocation != null) {
                    MarkerItem markerItem = new MarkerItem();
                    Marker marker = googleMap.addMarker(new MarkerOptions().position(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude())));
                    markerItem.marker = marker;
                    LocationStore.getInstance().addNotLinkedMarker(markerItem);
                    return true;
                }
                return false;
            }
        });
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED && newRoutePoints.size() != 2) {
                    Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(markersIcon[selectedIndex])));
                    if (newRoutePoints.size() == 0) {
                        startIntentService(marker.getPosition(), Constants.REQUEST_START);
                    } else {
                        startIntentService(marker.getPosition(), Constants.REQUEST_END);
                    }
                    newRoutePoints.add(marker);
                } else {
                    if (slidingUpPanelLayout.getPanelState() != SlidingUpPanelLayout.PanelState.ANCHORED) {
                        MarkerItem markerItem = new MarkerItem();
                        markerItem.marker = googleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(markersIcon[selectedIndex])));
                        markerItem.icon = markersIcon[selectedIndex];
                        LocationStore.getInstance().addNotLinkedMarker(markerItem);
                    }
                }
            }
        });
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (newRoutePoints.contains(marker)) {
                    newRoutePoints.remove(marker);
                    marker.remove();
                    updateMap();
                }
                //if (LocationStore.getInstance().getNotLinkedMarkers().contains(marker)) {
                //  LocationStore.getInstance().getNotLinkedMarkers().remove(marker);
                //marker.remove();
                //updateMap();
                //}

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

            if (resultCode == Constants.REQUEST_START) {
                routeStartTextView.setText(getString(R.string.route_start, resultData.getString(Constants.RESULT_DATA_KEY)));
            }
            if (resultCode == Constants.REQUEST_END) {
                routeEndTextView.setText(getString(R.string.route_end, resultData.getString(Constants.RESULT_DATA_KEY)));
            }

            if (resultCode == Constants.REQUEST_LOCATION) {
                Marker marker = googleMap.addMarker(new MarkerOptions().position((LatLng) resultData.getParcelable(Constants.LOCATION_DATA_EXTRA)).icon(BitmapDescriptorFactory.fromResource(markersIcon[selectedIndex])));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 7.0f));
                MarkerItem markerItem = new MarkerItem();
                markerItem.marker = marker;
                markerItem.icon = markersIcon[selectedIndex];
                LocationStore.getInstance().addNotLinkedMarker(markerItem);
            }

        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            //paths.add(data.getData());
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void choosePhoto(Intent intent, Marker marker) {
    }

    private void addMarkerByString() {
        final EditText input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Введите адрес");
        alert.setView(input); // uncomment this line
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                location = input.getText().toString();
                startIntentService(location, Constants.REQUEST_LOCATION);
            }
        });
        alert.show();
    }
}
