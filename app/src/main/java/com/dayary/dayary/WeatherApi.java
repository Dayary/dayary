package com.dayary.dayary;

import org.json.JSONArray;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApi {
    public static final String API_URL = "https://api.openweathermap.org/";
    @GET("data/2.5/weather")
    Call<Object> getWeather(@Query("q") String q, @Query("appid") String appid);
}
