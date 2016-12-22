package com.ssau.geolocation;

import android.content.Context;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;

/**
 * Created by Илья on 23.12.2016.
 */

public class MarkerAdapter extends RecyclerView.Adapter<MarkerAdapter.ViewHolder> {
    private ArrayList<Marker> markers;
    Context context;

    public MarkerAdapter(Context context, ArrayList<Marker> markers) {
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
        holder.location.setText(context.getString(R.string.marker_location, LocationStore.getInstance().getNotLinkedMarkers().get(position).getPosition().latitude, LocationStore.getInstance().getNotLinkedMarkers().get(position).getPosition().longitude));
    }


    @Override
    public int getItemCount() {
        return LocationStore.getInstance().getNotLinkedMarkers().size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView location;
        AppCompatImageButton deleteMarkerButton;

        public ViewHolder(View itemView) {
            super(itemView);
            location = (AppCompatTextView) itemView.findViewById(R.id.marker_location);
            deleteMarkerButton = (AppCompatImageButton) itemView.findViewById(R.id.delete_marker_button);
        }
    }
}
