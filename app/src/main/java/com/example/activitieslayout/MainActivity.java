package com.example.activitieslayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

//    initiallize all the variables
    private RelativeLayout RLhome;
    private ProgressBar idPBLoading;
    private TextView city_name,temperature,weather_condition;
    private TextInputEditText input_city;
    private ImageView bg_im,search_icon,weather_icon;
    private RecyclerView RVweagther;
    private ArrayList<WeatherRVmodal> weatherRVmodalArrayList;
    private WeatherRVAdapter weatherRVAdapter;
    private LocationManager locationManager;
    private final int PERMISSION_CODE=1;
    private String cityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);

        //access them by id's
        RLhome = findViewById(R.id.RLhome);
        idPBLoading = findViewById(R.id.idPBLoading);
        city_name = findViewById(R.id.city_name);
        temperature = findViewById(R.id.temperature);
        weather_condition = findViewById(R.id.weather_condition);
        input_city = findViewById(R.id.input_city);
        bg_im = findViewById(R.id.bg_img);
        search_icon = findViewById(R.id.search_icon);
        weather_icon=findViewById(R.id.weather_icon);
        RVweagther = findViewById(R.id.RVweather);
        weatherRVmodalArrayList = new ArrayList<>();
        weatherRVAdapter=new WeatherRVAdapter(this.weatherRVmodalArrayList);
        RVweagther.setAdapter(weatherRVAdapter);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);



//        check if permission granted or not
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
//                   if permission not granted request for permission
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSION_CODE);
        }

//        if permission is granted get last know location
        Location location=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        cityName = getCityName(location.getLongitude(), location.getLatitude());

        getWeatherInfo(cityName);

//        onclick on search icon
        search_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = input_city.getText().toString();
//                check if city name empty or not
                if(city.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please Enter a Valid City Name...",Toast.LENGTH_SHORT).show();
                }else {
                    city_name.setText(cityName);
                    getWeatherInfo(city);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==PERMISSION_CODE){
            if(grantResults.length>0 && grantResults.length==PERMISSION_CODE){
                Toast.makeText(this,"Permissions granted..",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Please provide permission.",Toast.LENGTH_SHORT).show();
            }

        }
    }

    //    method for longitude and latitude and will acess city name
    private String getCityName(double longitude ,double latitude) {
//        if city not found within specified long,lat return not found
        String cityName="Not Found";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(latitude,longitude,10);
            for(Address adr: addresses){
                if(adr!=null){
                    String city = adr.getLocality();
                    if(city!=null && !city.equals("")){
                        cityName = city;
                    }else {
                        Log.d("TAG","CITY MOT FOUND");
                        Toast.makeText(this,"User City not found...", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        }catch (IOException e) {
            e.printStackTrace();
        }
      return cityName;
    }

//    method for getting weather info and for user location input
    private void getWeatherInfo(String cityName) {
        String url="http://api.weatherapi.com/v1/current.json?key=fbefc049f6b4417bab4214019211308&q="+cityName+"&aqi=no";
        city_name.setText(cityName);
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);


//        Since parent is a JSON response so we will call JSON object request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
//                setting visiblity of progress bar gone and home page visible
                idPBLoading.setVisibility(View.GONE);
                RLhome.setVisibility(View.VISIBLE);
                weatherRVmodalArrayList.clear();

                try {
                    String Displaytemperature = response.getJSONObject("current").getString("temp_c");
                    temperature.setText(Displaytemperature+"°c");
                    int isDay = response.getJSONObject("current").getInt("is_day");
                    String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionIcon = response.getJSONObject("current").getJSONObject("icon").getString("text");
                    Context w_icon = weather_icon.getContext();
                    Picasso.with(w_icon).load("http:".concat(conditionIcon)).into(weather_icon);
                    weather_condition.setText(condition);
                    Context backgroung_image= bg_im.getContext();
                    if(isDay==1){
                        Picasso.with(backgroung_image).load("https://images.unsplash.com/photo-1566228015668-4c45dbc4e2f5?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=334&q=80").into(bg_im);
                    }else{
                        Picasso.with(backgroung_image).load("https://images.unsplash.com/photo-1454177697940-c43d9f9a7307?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=334&q=80").into(bg_im);
                    }




                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Please enter a valid city name.",Toast.LENGTH_SHORT).show();
            }
        }); //As this is a get request

        requestQueue.add(jsonObjectRequest);


    }
}