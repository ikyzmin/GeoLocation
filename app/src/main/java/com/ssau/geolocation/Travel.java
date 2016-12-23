package com.ssau.geolocation;

import android.net.Uri;

import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

public class Travel {
    public Marker origin;
    public Marker dest;
    public String name;
    public int icon;
    public ArrayList<Uri> photos = new ArrayList<>();

    public Travel(String name, Marker org, Marker dst,int icon) {
        origin = org;
        dest = dst;
        this.name = name;
        this.icon = icon;
    }
}
