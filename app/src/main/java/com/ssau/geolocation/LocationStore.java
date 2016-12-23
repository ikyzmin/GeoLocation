package com.ssau.geolocation;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;


public class LocationStore {

    private static LocationStore instance;

    private LocationStore() {
    }

    public static LocationStore getInstance() {
        if (null == instance) {
            instance = new LocationStore();
        }
        return instance;
    }

    private  ArrayList<Travel> travels = new ArrayList<>();
    private  ArrayList<MarkerItem> notLinkedMarkers = new ArrayList<>();
    private  ArrayList<Polyline> lines = new ArrayList<>();

    public ArrayList<Travel> getTravels() {
        return travels;
    }

    public void addTravel(Travel travel){
        travels.add(travel);
    }

    public void addNotLinkedMarker(MarkerItem marker){
        notLinkedMarkers.add(marker);
    }

    public void addLine(Polyline line){
        lines.add(line);
    }

    public void setTravels(ArrayList<Travel> travels) {
       travels = travels;
    }

    public ArrayList<MarkerItem> getNotLinkedMarkers() {
        return notLinkedMarkers;
    }

    public void setNotLinkedMarkers(ArrayList<MarkerItem> notLInkedMarkers) {
        notLinkedMarkers = notLInkedMarkers;
    }

    public  ArrayList<Polyline> getLines() {
        return lines;
    }

    public void setLines(ArrayList<Polyline> lines) {
        lines = lines;
    }
}
