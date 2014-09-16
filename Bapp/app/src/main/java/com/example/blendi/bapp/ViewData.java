package com.example.blendi.bapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by blendi on 9/13/2014.
 */
public class ViewData extends Activity implements View.OnClickListener {
    TextView data;
    Button delete;
    SQLite objSQL;
    //String city_country;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_data);
        Initialize();
        objSQL = new SQLite(this);

        try {
            //open connection with database
            objSQL.Open();
            //get all data from database via getAllData method in SQLite class
            data.setText(objSQL.getAllData());

        } catch (SQLException e) {
            Dialog d = new Dialog(this);
            d.setTitle("Error!");
            TextView tv = new TextView(this);
            tv.setText("Failed to get data from Database");
        } finally {

            objSQL.Close();
        }

    }


    private void Initialize() {

        data = (TextView) findViewById(R.id.txtData);
        delete = (Button) findViewById(R.id.btnDelete);
        delete.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        // on "Delete history" button click
        try {
            objSQL.Open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        objSQL.Delete();
        objSQL.Close();
        data.setText("Database is empty!");
    }
}
