package com.ssau.geolocation;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.ssau.geolocation.util.MapUtil;


public class ConnectAsyncTask extends AsyncTask<Void, Void, String> {
    private ProgressDialog progressDialog;
    public static final String ROAD_NOT_FOUND_ACTION = "roadNotFound";
    String url;
    Context context;
    GoogleMap map;
    int routeIndex;

    public ConnectAsyncTask(Context context, String urlPass, GoogleMap map, int routIndex) {
        url = urlPass;
        this.context = context;
        this.map = map;
        this.routeIndex = routIndex;
    }

    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
       // progressDialog = new ProgressDialog(context);
        //progressDialog.setMessage("Запрос пути...");
        //progressDialog.setIndeterminate(true);
        //progressDialog.show();
    }

    @Override
    protected String doInBackground(Void... params) {
        JSONParser jParser = new JSONParser();
        String json = jParser.getJSONFromUrl(url);
        //progressDialog.hide();
        return json;
    }


}
