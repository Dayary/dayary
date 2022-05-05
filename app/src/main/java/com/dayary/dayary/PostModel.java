package com.dayary.dayary;

import java.io.Serializable;

public class PostModel implements Serializable {
    public String userId;
    public String photo;
    public String photoName;
    public String photoX;
    public String photoY;
    public String text;

    public PostModel(String userId, String photo, String photoName, String photoX, String photoY, String text) {
        this.userId = userId;
        this.photo = photo;
        this.photoName = photoName;
        this.photoX = photoX;
        this.photoY = photoY;
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

    public String getPhotoX() {
        return photoX;
    }

    public void setPhotoX(String photoX) {
        this.photoX = photoX;
    }

    public String getPhotoY() {
        return photoY;
    }

    public void setPhotoY(String photoY) {
        this.photoY = photoY;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public PostModel() {
    }
}