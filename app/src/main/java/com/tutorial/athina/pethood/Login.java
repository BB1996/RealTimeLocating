package com.tutorial.athina.pethood;

public class Login {

    private String email, password, online;
    private double lat, lng;

    public Login() {

    }

    public Login(String email, String password, String online, double lat, double lng) {
        this.email = email;
        this.password = password;
        this.online = online;
        this.lat = lat;
        this.lng = lng;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getOnline() {
        return online;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }
}
