package com.ssau.geolocation;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class PhotoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private MapActivity activity;

    public PhotoWindowAdapter(MapActivity activity) {
        this.activity = activity;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View v = LayoutInflater.from(activity).inflate(R.layout.i_marker_content, null,false);
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.photos_recycler);
        recyclerView.setAdapter(new PhotoListAdapter(marker, activity));
        return v;
    }
}
