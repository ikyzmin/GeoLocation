package com.ssau.geolocation.menu;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;

import com.ssau.geolocation.MarkersActivity;
import com.ssau.geolocation.R;
import com.ssau.geolocation.SearchClosestTravelActivity;
import com.ssau.geolocation.TravelsListActivity;

/**
 * Created by Илья on 22.12.2016.
 */

public enum MenuItem {
    TRAVELS(R.string.travels_list, TravelsListActivity.class),
    MARKERS(R.string.not_linked_markers_list, MarkersActivity.class),
    SEARCH(R.string.search_closest_travel, SearchClosestTravelActivity.class);

    @StringRes
    int titleId;

    Class<? extends AppCompatActivity> activity;

    MenuItem(@StringRes int titleId, Class<? extends AppCompatActivity> activity) {
        this.titleId = titleId;
        this.activity = activity;
    }

    public int getTitleId() {
        return titleId;
    }

    public void activate(Context context) {
        if (activity != null) {
            context.startActivity(new Intent(context, activity));
        }
    }
}
