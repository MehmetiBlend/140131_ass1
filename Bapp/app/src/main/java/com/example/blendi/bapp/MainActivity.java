package com.example.blendi.bapp;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainActivity extends Activity implements View.OnClickListener{

    Button weather;
    Button map;
    Button camera;
    Intent i;
    MediaPlayer song;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Initialize();
        song = MediaPlayer.create(this,R.raw.shotgun);
        song.start();
    }

    private void Initialize(){

        weather = (Button)findViewById(R.id.btnWeather);
        map = (Button)findViewById(R.id.btnLocation);
        camera = (Button)findViewById(R.id.btnCamera);
        weather.setOnClickListener(this);
        map.setOnClickListener(this);
        camera.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        song.release();
        switch (view.getId()){

            case R.id.btnWeather:
                i = new Intent("android.intent.action.weather");
                startActivity(i);
                break;
            case R.id.btnCamera:
                i = new Intent("android.intent.action.camera");
                startActivity(i);
                break;
            case R.id.btnLocation:
                i = new Intent("android.intent.action.map");
                startActivity(i);
                break;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        song.release();
    }
}
