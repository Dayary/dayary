package com.dayary.dayary;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class PostModel implements Serializable {
    public String userId;
    public String photo;
    public String photoName;
    public String photoLatitude;
    public String photoLongitude;
    public String text;

    public PostModel() {
    }

    public PostModel(String userId, String photo, String photoName, String photoLatitude, String photoLongitude, String text) {
        this.userId = userId;
        this.photo = photo;
        this.photoName = photoName;
        this.photoLatitude = photoLatitude;
        this.photoLongitude = photoLongitude;
        this.text = text;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public String getPhotoLatitude() {
        return photoLatitude;
    }

    public void setPhotoLatitude(String photoLatitude) {
        this.photoLatitude = photoLatitude;
    }

    public String getPhotoLongitude() {
        return photoLongitude;
    }

    public void setPhotoLongitude(String photoLongitude) {
        this.photoLongitude = photoLongitude;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("photo", photo);
        result.put("photoLatitude", photoLatitude);
        result.put("photoLongitude", photoLongitude);
        result.put("photoName", photoName);
        result.put("text", text);
        result.put("userId", userId);
        return result;
    }
}