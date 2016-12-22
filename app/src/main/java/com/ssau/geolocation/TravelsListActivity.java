package com.ssau.geolocation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

public class TravelsListActivity extends AppCompatActivity {


    private RecyclerView travelsRecycler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_travels_list);
        travelsRecycler = (RecyclerView) findViewById(R.id.travels_list);
        travelsRecycler.setAdapter(new TravelsAdapter(this, LocationStore.getInstance().getTravels(), LocationStore.getInstance().getLines()));
    }
}
