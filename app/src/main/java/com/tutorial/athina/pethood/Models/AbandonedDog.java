package com.tutorial.athina.pethood.Models;

public class AbandonedDog {

    Double lat,lng;

    public AbandonedDog() {
    }

    public AbandonedDog(Double lat, Double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }
}
