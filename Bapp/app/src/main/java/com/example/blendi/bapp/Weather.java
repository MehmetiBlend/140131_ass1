package com.example.blendi.bapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.text.DecimalFormat;

/**
 * Created by blendi on 9/10/2014.
 */
public class Weather extends Activity implements View.OnClickListener {

    TextView temperature, txtcity, description;
    EditText edcity, edcountry;
    String city = "Gjøvik";
    String country = "Norway";
    Button search, history;
    HttpClient client;
    JSONObject json;
    SQLite objSQL;
    //get weather info from link
    //http://api.openweathermap.org/data/2.5/weather?q=city,country
    final static String URL = "http://api.openweathermap.org/data/2.5/weather?q=";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_activity);
        Initialize();
        client = new DefaultHttpClient();
        // set content of text view from city and country from edit text fields
        txtcity.setText(city + ", " + country);
        // Read temperature form main
        new ReadTemp().execute("temp");
        // Read description from weather
        new ReadDesc().execute("description");
        //store data in database
        SetData();
    }

    private void Initialize() {

        temperature = (TextView) findViewById(R.id.txtTemp);
        description = (TextView) findViewById(R.id.txtDesc);
        txtcity = (TextView) findViewById(R.id.txtcity);
        search = (Button) findViewById(R.id.btnSearch);
        edcity = (EditText) findViewById(R.id.edCity);
        edcountry = (EditText) findViewById(R.id.edCountry);
        history = (Button) findViewById(R.id.btnHistory);
        history.setOnClickListener(this);


        //http://stackoverflow.com/questions/11134144/android-edittext-onchange-listener
        // set change location button to enabled after city is written.
        edcity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                search.setEnabled(true);
                search.setOnClickListener(Weather.this);
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            //if button change location is clicked
            case R.id.btnSearch:
                //set content of text view with city, country
                txtcity.setText(edcity.getText().toString() + ", " + edcountry.getText().toString());
                //replace whitespaces with '&' character for cities that has two or more words e.g. Las Vegas , New York.. etc.
                if (edcity.getText().toString().contains(" ")) {
                    city = edcity.getText().toString().replaceAll(" ", "&");
                } else {
                    city = edcity.getText().toString();
                }
                //replace whitespaces with '&' character for countries that has two or more words e.g. United Kingdom , United States.. etc
                if (edcountry.getText().toString().contains(" ")) {
                    country = edcountry.getText().toString().replaceAll(" ", "&");
                } else {
                    country = edcountry.getText().toString();
                }
                //read temperature
                new ReadTemp().execute("temp");
                edcity.getText().clear();
                edcountry.getText().clear();
                //read sky description
                new ReadDesc().execute("description");
                //store data
                SetData();
                break;
            case R.id.btnHistory:
                Intent i = new Intent("android.intent.action.viewData");
                startActivity(i);
                break;
        }
    }

    //JSONObject method gets data from internet depend of cityCounty and an extra parameter x
    //if x == 1 get data for temperature else get data for weather(sky description)
    public JSONObject lastInfo(String cityCountry, int x) throws ClientProtocolException, JSONException, IOException {

        //set up url
        StringBuilder url = new StringBuilder(URL);
        url.append(cityCountry);// cityCountry = city,county
        HttpGet get = new HttpGet(url.toString());//ask for data
        HttpResponse response = client.execute(get);// get response
        int status = response.getStatusLine().getStatusCode();
        if (status == 200) // Status Code OK
        {
            HttpEntity entity = response.getEntity();
            String data = EntityUtils.toString(entity);//get data from entity as a string
            json = new JSONObject(data);//initialize jsonObject;
            JSONArray weather = json.getJSONArray("weather");//go to weather array
            JSONObject lastData = weather.getJSONObject(0);//take the first object for string I'll pas to you
            JSONObject TEMP = json.getJSONObject("main");//go to main object
            if (x == 1) {//if x==1 get return temperature object
                return TEMP;
            } else return lastData;//else return weather object
        } else {
            Toast.makeText(this, "error", Toast.LENGTH_LONG).show();
            return null;
        }
    }

    //Read temperature from main JSONObject
    public class ReadTemp extends AsyncTask<String, Integer, String> {

        //Get data
        @Override
        protected String doInBackground(String... strings) {

            String location = city + "," + country;
            try {
                json = lastInfo(location, 1);//call JSONObject lastInfo method for location and get main object
                return json.getString(strings[0]);//get value of string(temperature) in main object

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
        //Set data to variables
        @Override
        protected void onPostExecute(String s) {

            if (s != null) {
                Double temp = Double.parseDouble(s);//convert temperature value from string format to double
                temp -= 273.15;//convert from Kelvin to Celsius
                temperature.setText(new DecimalFormat("#0.00").format(temp) + " °C");//set decimal format with 2 numbers after decimal point

            } else {
                txtcity.setText("N/A,N/A");//Not Available
                temperature.setText("N/A");
            }

        }
    }

    //Read weather description
    public class ReadDesc extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {


            String location = city + "," + country;
            try {
                json = lastInfo(location, 2);//call JSONObject lastInfo method for location and get weather object
                return json.getString(strings[0]);//get value of string(description) in weather object

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null) {
                description.setText(s);
            } else
                description.setText("Location not found!");

        }
    }

    //Store data to database
    public void SetData() {
        //Delay for 2.2 sec to put data in SQLite Database because, saving data in database is faster than getting them from Internet
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if ((txtcity.getText().toString() != "N/A,N/A") && (temperature.getText().toString() != "N/A")
                        && (description.getText().toString() != "Location not found!")) {
                    try {
                        objSQL = new SQLite(Weather.this);
                        objSQL.Open();
                        //PutData(String name, String temp, String desc)
                        objSQL.PutData(txtcity.getText().toString(), temperature.getText().toString(), description.getText().toString());
                        Toast.makeText(Weather.this, "Registred in History!", Toast.LENGTH_SHORT).show();
                    } catch (SQLException e) {
                        Dialog d = new Dialog(Weather.this);
                        d.setTitle("Error!");
                        TextView tv = new TextView(Weather.this);
                        tv.setText("Failed to add data in Database");
                    } finally {
                        objSQL.Close();
                    }
                } else {
                    Toast.makeText(Weather.this, "Not registred in history!", Toast.LENGTH_SHORT).show();
                }
            }
        }, 2200);//2200 milliseconds delay

    }

}

