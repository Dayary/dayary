package com.dayary.dayary;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherModel {
    @SerializedName("weather")
    private List<Weather> weather;

    public List<Weather> getWeather() {
        return weather;
    }

}
