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

    public interface foreCastByIDResponse {
        void onError(String message);

        void onResponse(WeatherReportModel weatherReportModel);
    }

    public void getCityForecastById(String cityID, foreCastByIDResponse foreCastByIDResponse) {
        List<WeatherReportModel> report = new ArrayList<>();
        String url = QUERY_FOR_CITY_WEATHER_BY_ID + cityID;


        // get the json object
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
               // Toast.makeText(context, response.toString(), Toast.LENGTH_SHORT).show();

                try {
                    JSONArray consolidated_weather_list = response.getJSONArray("consolidated_weather");
                    //get the first item in the array
                    WeatherReportModel first_day = new WeatherReportModel();




                    JSONObject firstDayFromApi = (JSONObject) consolidated_weather_list.get(0);
                    first_day.setId(firstDayFromApi.getInt("id"));
                    first_day.setWeather_state_name(firstDayFromApi.getString("weather_state_name"));
                    first_day.setWeather_state_abbr(firstDayFromApi.getString("weather_state_abbr"));
                    first_day.setWind_direction_compass(firstDayFromApi.getString("wind_direction_compass"));
                    first_day.setCreated(firstDayFromApi.getString("created"));
                    first_day.setApplicable_date(firstDayFromApi.getString("applicable_date"));
                    first_day.setMin_temp(firstDayFromApi.getLong("min_temp"));
                    first_day.setMax_temp(firstDayFromApi.getLong("max_temp"));
                    first_day.setThe_temp(firstDayFromApi.getLong("the_temp"));
                    first_day.setWind_speed(firstDayFromApi.getLong("wind_speed"));
                    first_day.setWind_direction(firstDayFromApi.getLong("wind_direction"));
                    first_day.setAir_pressure(firstDayFromApi.getInt("air_pressure"));
                    first_day.setHumidity(firstDayFromApi.getInt("humidity"));
                    first_day.setVisibility(firstDayFromApi.getLong("visibility"));
                    first_day.setPredictability(firstDayFromApi.getLong("predictability"));

                 foreCastByIDResponse.onResponse(first_day);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
            //get the property called "consolidated_weather which is an array

        MySingleton.getInstance(context).addToRequestQueue(request);
    }
        // get each item in the array and assign it to a new Weather Report object

//
//    public List<WeatherReportModel> getCityForecastByName(String cityName) {
//
//    }
}
