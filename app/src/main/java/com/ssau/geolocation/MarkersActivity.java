package com.ssau.geolocation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;


public class MarkersActivity extends AppCompatActivity {

    private RecyclerView markersRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_markers_list);
        markersRecyclerView = (RecyclerView) findViewById(R.id.marker_list);
        markersRecyclerView.setAdapter(new MarkerAdapter(this, LocationStore.getInstance().getNotLinkedMarkers()));

    }
}
