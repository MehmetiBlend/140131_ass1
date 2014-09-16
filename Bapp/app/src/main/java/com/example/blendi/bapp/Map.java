package com.example.blendi.bapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.view.Menu;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

// https://developers.google.com/maps/documentation/android/start
// how to put a map on the screen

public class Map extends Activity {

    GoogleMap myMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);
        myMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.mymap)).getMap();
        myMap.setMyLocationEnabled(true);


    }
    @Override
     public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.map_menu, menu);
        return true;

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

       switch(item.getItemId()){
           case R.id.satellite:
               myMap.setMapType(myMap.MAP_TYPE_SATELLITE);
               break;
           case R.id.terrain:
               myMap.setMapType(myMap.MAP_TYPE_TERRAIN);
               break;
           case R.id.hybird:
               myMap.setMapType(myMap.MAP_TYPE_HYBRID);
               break;
           case R.id.normal:
               myMap.setMapType(myMap.MAP_TYPE_NORMAL);
               break;

       }
        return true;
    }
}