package com.talentomobile.apptalento.model;

/**
 * Created by Tomás on 28/02/2018.
 */

public class City {
    private String name;
    private String country;
    private String bbox;
    private Double longitude;
    private Double latitude;

    public City() {}

    @Override
    public String toString() {
        return this.name+","+this.country+","+this.longitude.toString()+","+this.latitude.toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String region) {
        this.country = region;
    }

    public String getBbox() {
        return bbox;
    }

    public void setBbox(String bbox) {
        this.bbox = bbox;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
}
