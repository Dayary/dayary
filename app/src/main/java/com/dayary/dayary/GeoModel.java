package com.dayary.dayary;

import android.graphics.Bitmap;

public class GeoModel {
    private double lat;
    private double lng;
    private Bitmap bitmap;

    public GeoModel() {
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public GeoModel(double lat, double lng, Bitmap bitmap) {
        this.lat = lat;
        this.lng = lng;
        this.bitmap = bitmap;
    }
}
