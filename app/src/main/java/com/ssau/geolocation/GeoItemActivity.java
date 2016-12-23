package com.ssau.geolocation;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.GridView;

import java.util.ArrayList;

/**
 * Created by Илья on 23.12.2016.
 */

public class GeoItemActivity extends AppCompatActivity {

    public enum GeoType {
        TRAVEL, MARKER
    }

    public static final String GEO_TYPE_EXTRA = "geoType";
    public static final String ID_EXTRA = "id";
    private int id;
    AppCompatButton addPhoto;
    GridView photoGridView;
    ArrayList<Uri> bitmaps;
    private GeoType geoType;

    public static void startMe(Context context, int id, GeoType geoType) {
        Intent intent = new Intent(context, GeoItemActivity.class);
        intent.putExtra(ID_EXTRA, id);
        intent.putExtra(GEO_TYPE_EXTRA, geoType);
        context.startActivity(intent);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        id = intent.getIntExtra(ID_EXTRA, 0);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_geo_item);
        id = getIntent().getIntExtra(ID_EXTRA, 0);
        geoType = (GeoType) getIntent().getSerializableExtra(GEO_TYPE_EXTRA);
        bitmaps = new ArrayList<>();
        photoGridView = (GridView) findViewById(R.id.photos_grid_view);
        addPhoto = (AppCompatButton) findViewById(R.id.add_photo_button);
        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                } else {
                    intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                }
                startActivityForResult(intent, 120);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        switch (geoType){
            case TRAVEL:
                 bitmaps = LocationStore.getInstance().getTravels().get(id).photos;
                break;
            case MARKER:
                bitmaps = LocationStore.getInstance().getNotLinkedMarkers().get(id).photos;
                break;
        }
        photoGridView.setAdapter(new ImageAdapter(this, bitmaps));
        photoGridView.invalidate();

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (geoType){
                case TRAVEL:
                    LocationStore.getInstance().getTravels().get(id).photos.add(data.getData());
                    bitmaps = LocationStore.getInstance().getTravels().get(id).photos;
                    break;
                case MARKER:
                    LocationStore.getInstance().getNotLinkedMarkers().get(id).photos.add(data.getData());
                    bitmaps = LocationStore.getInstance().getNotLinkedMarkers().get(id).photos;
                    break;
            }
            photoGridView.setAdapter(new ImageAdapter(this, bitmaps));
            super.onActivityResult(requestCode, resultCode, data);
        }

    }
}
