package com.example.blendi.bapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by blendi on 9/10/2014.
 */
public class Camera extends Activity implements View.OnClickListener {


    Button setWall;
    ImageButton StartCam;
    ImageView imgView;
    Intent i;
    final static int cameraData = 0;
    Bitmap bmp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_activity);
        Initialize();
        // get imgae from raw folder
        InputStream is = getResources().openRawResource(R.raw.fff);
        bmp = BitmapFactory.decodeStream(is);
    }

    private void Initialize() {

        setWall = (Button) findViewById(R.id.btnSetWall);
        StartCam = (ImageButton) findViewById(R.id.ibtnStartCamera);
        imgView = (ImageView) findViewById(R.id.ivCamera);
        setWall.setOnClickListener(this);
        StartCam.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnSetWall:
                //use bitmap to set image to background
                try {
                    getApplicationContext().setWallpaper(bmp);
                    Toast t = Toast.makeText(this,"Wallpaper is set!",Toast.LENGTH_SHORT);
                    t.show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast t = Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT);
                    t.show();
                }
                break;

            case R.id.ibtnStartCamera:
                //start camera to take a picture
                i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //start activity to get a result
                startActivityForResult(i, cameraData);
            break;
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            //if data are OK put them in bitmap then in imageView
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                bmp = (Bitmap) extras.get("data");
                imgView.setImageBitmap(bmp);
            }

    }
}
