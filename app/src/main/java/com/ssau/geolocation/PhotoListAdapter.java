package com.ssau.geolocation;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

/**
 * Created by Илья on 23.12.2016.
 */

public class PhotoListAdapter extends RecyclerView.Adapter {

    public static final int PHOTO = 0;
    public static final int ADD_PHOTO_BUTTON = 1;
    Marker marker;
    MapActivity activity;
    final ArrayList<Uri> photos = new ArrayList<>();

    public PhotoListAdapter(Marker marker, MapActivity activity) {
        this.marker = marker;
        this.activity = activity;
    }

    public void addPhotoUri(Uri uri) {
        photos.add(uri);
        notifyItemInserted(photos.size() - 1);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case PHOTO:
                return new PhotoViewHolder(LayoutInflater.from(activity).inflate(R.layout.i_photo, parent));
            case ADD_PHOTO_BUTTON:
                return new AddPhotoViewHolder(LayoutInflater.from(activity).inflate(R.layout.i_add_photo_controller, parent));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case PHOTO:
                PhotoViewHolder photoViewHolder = (PhotoViewHolder) holder;
                photoViewHolder.photo.setImageURI(photos.get(position));
                break;
            case ADD_PHOTO_BUTTON:
                AddPhotoViewHolder addPhotoViewHolder = (AddPhotoViewHolder) holder;
                addPhotoViewHolder.appCompatImageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        activity.choosePhoto(intent, marker);
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position != (getItemCount() - 1) ? PHOTO : ADD_PHOTO_BUTTON;
    }

    class PhotoViewHolder extends RecyclerView.ViewHolder {

        ImageView photo;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            photo = (ImageView) itemView.findViewById(R.id.photo);
        }
    }

    class AddPhotoViewHolder extends RecyclerView.ViewHolder {

        AppCompatImageButton appCompatImageButton;

        public AddPhotoViewHolder(View itemView) {
            super(itemView);
            appCompatImageButton = (AppCompatImageButton) itemView.findViewById(R.id.add_photo_button);
        }
    }
}
