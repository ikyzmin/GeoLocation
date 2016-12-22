package com.ssau.geolocation;

import com.google.android.gms.maps.model.Marker;

public class Travel {
    public Marker origin;
    public Marker dest;
    public String name;

    public Travel(String name, Marker org, Marker dst) {
        origin = org;
        dest = dst;
        this.name = name;
    }
}
