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

        void onResponse(List<WeatherReportModel> weatherReportModels);
    }

    public void getCityForecastById(String cityID, foreCastByIDResponse foreCastByIDResponse) {
        List<WeatherReportModel> weatherReportModels = new ArrayList<>();
        String url = QUERY_FOR_CITY_WEATHER_BY_ID + cityID;


        // get the json object
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
               // Toast.makeText(context, response.toString(), Toast.LENGTH_SHORT).show();

                try {
                    JSONArray consolidated_weather_list = response.getJSONArray("consolidated_weather");
                    //get the first item in the array

                    for(int i = 0; i< consolidated_weather_list.length(); i++) {

                        WeatherReportModel one_day_weather = new WeatherReportModel();
                        JSONObject firstDayFromApi = (JSONObject) consolidated_weather_list.get(i);
                        one_day_weather.setId(firstDayFromApi.getInt("id"));
                        one_day_weather.setWeather_state_name(firstDayFromApi.getString("weather_state_name"));
                        one_day_weather.setWeather_state_abbr(firstDayFromApi.getString("weather_state_abbr"));
                        one_day_weather.setWind_direction_compass(firstDayFromApi.getString("wind_direction_compass"));
                        one_day_weather.setCreated(firstDayFromApi.getString("created"));
                        one_day_weather.setApplicable_date(firstDayFromApi.getString("applicable_date"));
                        one_day_weather.setMin_temp(firstDayFromApi.getLong("min_temp"));
                        one_day_weather.setMax_temp(firstDayFromApi.getLong("max_temp"));
                        one_day_weather.setThe_temp(firstDayFromApi.getLong("the_temp"));
                        one_day_weather.setWind_speed(firstDayFromApi.getLong("wind_speed"));
                        one_day_weather.setWind_direction(firstDayFromApi.getLong("wind_direction"));
                        one_day_weather.setAir_pressure(firstDayFromApi.getInt("air_pressure"));
                        one_day_weather.setHumidity(firstDayFromApi.getInt("humidity"));
                        one_day_weather.setVisibility(firstDayFromApi.getLong("visibility"));
                        one_day_weather.setPredictability(firstDayFromApi.getLong("predictability"));
                        weatherReportModels.add(one_day_weather);
                    }

                 foreCastByIDResponse.onResponse(weatherReportModels);

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

        public interface getCityForecastByNameCallback {
            void onError(String message);
            void onResponse(List<WeatherReportModel> weatherReportModels);
        }
    public void getCityForecastByName(String cityName, getCityForecastByNameCallback getCityForecastByNameCallback) {
        // fetch the city id given the city name
        getCityId(cityName, new VolleyResponseListener() {
            @Override
            public void onError(String message) {

            }

            @Override
            public void onResponse(String cityId) {
                // now we have two city Id
                getCityForecastById(cityId, new foreCastByIDResponse() {
                    @Override
                    public void onError(String message) {

                    }

                    @Override
                    public void onResponse(List<WeatherReportModel> weatherReportModels) {
                        // we have the weather report.
                        getCityForecastByNameCallback.onResponse(weatherReportModels);
                    }
                });
            }
        });
        // fetch the city forecast given the city id

    }
}
