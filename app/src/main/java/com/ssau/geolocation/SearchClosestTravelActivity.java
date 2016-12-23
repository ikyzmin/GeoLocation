package com.ssau.geolocation;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;

/**
 * Created by Илья on 23.12.2016.
 */

public class SearchClosestTravelActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {

    AppCompatButton searchButton;
    RecyclerView recyclerView;
    private LocationManager manager;
    private Location lastKnownLocation;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_search_activity);
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        searchButton = (AppCompatButton) findViewById(R.id.search_button);
        recyclerView = (RecyclerView) findViewById(R.id.travels_list);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(SearchClosestTravelActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SearchClosestTravelActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
              //  manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,
                  //      0, new LocationListener() {
                  //          @Override
                  //          public void onLocationChanged(Location location) {

                                float minDistance = Float.MAX_VALUE;
                                float[] result = new float[3];
                                int index = 0;
                                for (Travel travel : LocationStore.getInstance().getTravels()) {
                                    Location.distanceBetween(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), travel.dest.getPosition().latitude, travel.dest.getPosition().longitude, result);
                                    if (result[1] == 0) {
                                        minDistance = minDistance > result[0] ? result[0] : minDistance;
                                        index++;
                                    } else {
                                        if (result[2] == 0) {
                                            minDistance = minDistance > result[1] ? result[1] : minDistance;
                                            index++;
                                        } else {
                                            minDistance = minDistance > result[2] ? result[2] : minDistance;
                                            index++;
                                        }
                                    }
                                }
                                ArrayList<Travel> foundedTravel = new ArrayList<>();
                                foundedTravel.add(LocationStore.getInstance().getTravels().get(index-1));
                                ArrayList<Polyline> line = new ArrayList<>();
                                line.add(LocationStore.getInstance().getLines().get(index-1));
                                recyclerView.setAdapter(new TravelsAdapter(SearchClosestTravelActivity.this, foundedTravel, line));

                          //  }

                          //  @Override
                         //   public void onStatusChanged(String s, int i, Bundle bundle) {

                          //  }

                          //  @Override
                          //  public void onProviderEnabled(String s) {

                         //   }

                           // @Override
                          //  public void onProviderDisabled(String s) {
//
                           // }
                       // });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
