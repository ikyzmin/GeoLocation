package com.ssau.geolocation;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by Илья on 22.12.2016.
 */

public class Travel {
    public Marker origin;
    public Marker dest;

    public Travel(Marker org, Marker dst) {
        origin = org;
        dest = dst;
    }
}
