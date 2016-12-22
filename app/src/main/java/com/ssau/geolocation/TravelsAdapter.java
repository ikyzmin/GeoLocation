package com.ssau.geolocation;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;

/**
 * Created by Илья on 22.12.2016.
 */

public class TravelsAdapter extends RecyclerView.Adapter<TravelsAdapter.ViewHolder> {

    private ArrayList<Travel> travels;
    ArrayList<Polyline> lines;
    Context context;

    public TravelsAdapter(Context context, ArrayList<Travel> travels, ArrayList<Polyline> lines) {
        this.travels = travels;
        this.lines = lines;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.i_travel, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        //holder.travelRouteTextView.setText(context.getString(R.string.travel_route,travels.get(position).origin));
        holder.travelNameTextView.setText(travels.get(position).name);
        Drawable routeDrawable = ContextCompat.getDrawable(context, R.drawable.square);
        routeDrawable.setColorFilter(new PorterDuffColorFilter(lines.get(position).getColor(), PorterDuff.Mode.SRC_IN));
        holder.travelColorImageView.setImageDrawable(routeDrawable);
        holder.deleteTravelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationStore.getInstance().getTravels().remove(position);
                LocationStore.getInstance().getLines().remove(position);
                notifyItemRemoved(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return travels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView travelColorImageView;
        AppCompatTextView travelNameTextView;
        AppCompatTextView travelRouteTextView;
        AppCompatImageButton deleteTravelButton;

        public ViewHolder(View itemView) {
            super(itemView);
            travelColorImageView = (ImageView) itemView.findViewById(R.id.travel_color);
            travelNameTextView = (AppCompatTextView) itemView.findViewById(R.id.travel_name);
            travelRouteTextView = (AppCompatTextView) itemView.findViewById(R.id.travel_route);
            deleteTravelButton = (AppCompatImageButton) itemView.findViewById(R.id.delete_travel_button);
        }
    }
}
