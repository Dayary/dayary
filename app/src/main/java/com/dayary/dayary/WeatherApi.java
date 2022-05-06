package com.dayary.dayary;

import android.app.Application;
import org.json.JSONArray;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApi {
    @GET("weather")
    Call<WeatherModel> getWeather(@Query("q") String cityname, @Query("appid") String appKey);
}
