package com.example.restfulapiweather;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class WeatherDataService {

    public static final String SEARCH_QUERY = "https://www.metaweather.com/api/location/search/?query=";
    public static final String QUERY_FOR_CITY_WEATHER_BY_ID = "https://www.metaweather.com/api/location/";

    Context context;
    String cityID;

    public WeatherDataService(Context context) {
        this.context = context;
    }

    public interface VolleyResponseListener {
        void onError(String message);

        void onResponse(String cityId);
    }



    public void getCityId(String cityName, VolleyResponseListener volleyResponseListener) {
        String url = SEARCH_QUERY + cityName;


        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                cityID = "";
                try {
                    JSONObject cityInfo = response.getJSONObject(0);
                    cityID = cityInfo.getString("woeid");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                this worked but it didn't return the id number to Main Activity
              //  Toast.makeText(context, "City ID = " + cityID, Toast.LENGTH_SHORT).show();
                volleyResponseListener.onResponse(cityID);
//                            Prints the entire toString
//                        Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               // Toast.makeText(context, "Something is wrong", Toast.LENGTH_SHORT).show();
                volleyResponseListener.onError("Something is wrong");

            }
        });

        MySingleton.getInstance(context).addToRequestQueue(request);
        // returned a NULL. problem!
//        return cityID;
    }

    public void getCityForecastById(String cityID) {
        List<WeatherReportModel> report = new ArrayList<>();
        String url = QUERY_FOR_CITY_WEATHER_BY_ID + cityID;


        // get the json object
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(context, response.toString(), Toast.LENGTH_SHORT).show();
            }

        } , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
            //get the property called "consolodated_weather which is an array

        MySingleton.getInstance(context).addToRequestQueue(request);
    }
        // get each item in the array and assign it to a new Weather Report object

//
//    public List<WeatherReportModel> getCityForecastByName(String cityName) {
//
//    }
}
