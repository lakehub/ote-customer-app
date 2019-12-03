package com.ote.otedeliveries.models;

public class Place {
    private String placeID, placeText;

    public Place(String placeID, String placeText) {
        this.placeID = placeID;
        this.placeText = placeText;
    }

    public Place() {
    }

    public String getPlaceID() {
        return placeID;
    }

    public void setPlaceID(String placeID) {
        this.placeID = placeID;
    }

    public String getPlaceText() {
        return placeText;
    }

    public void setPlaceText(String placeText) {
        this.placeText = placeText;
    }
}
