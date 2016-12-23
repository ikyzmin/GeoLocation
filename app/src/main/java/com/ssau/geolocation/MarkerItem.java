package com.ssau.geolocation;

import android.net.Uri;

import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

public class MarkerItem {
    public Marker marker;
    public int icon;
    public final ArrayList<Uri> photos = new ArrayList<>();
}
