package com.ssau.geolocation;

import android.content.Context;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;

/**
 * Created by Илья on 23.12.2016.
 */

public class MarkerAdapter extends RecyclerView.Adapter<MarkerAdapter.ViewHolder> {
    private ArrayList<MarkerItem> markers;
    Context context;

    public MarkerAdapter(Context context, ArrayList<MarkerItem> markers) {
        this.markers = markers;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.i_marker, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.deleteMarkerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationStore.getInstance().getNotLinkedMarkers().remove(position);
                notifyItemRemoved(position);
            }
        });
        holder.location.setText(context.getString(R.string.marker_location, LocationStore.getInstance().getNotLinkedMarkers().get(position).marker.getPosition().latitude, LocationStore.getInstance().getNotLinkedMarkers().get(position).marker.getPosition().longitude));
        holder.markerIcon.setImageResource(LocationStore.getInstance().getNotLinkedMarkers().get(position).icon);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeoItemActivity.startMe(context, position, GeoItemActivity.GeoType.MARKER);
            }
        });
    }


    @Override
    public int getItemCount() {
        return LocationStore.getInstance().getNotLinkedMarkers().size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView markerIcon;
        AppCompatTextView location;
        AppCompatImageButton deleteMarkerButton;
        View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            markerIcon = (ImageView) itemView.findViewById(R.id.marker_icon);
            location = (AppCompatTextView) itemView.findViewById(R.id.marker_location);
            deleteMarkerButton = (AppCompatImageButton) itemView.findViewById(R.id.delete_marker_button);
        }
    }
}
