package com.example.donald.crimereporting;

/**
 * Created by donald on 9/22/17.
 */

public class Users {
    private String username;
    private String phone;
    private String state;
    private String crimeTypes;
    private String crimeDesc;
    private String crimePlace;
    private String date;
    private String time;
    private String email;

    public Users() {

    }

    public Users(String username, String phone, String state,
                 String crimeTypes, String crimeDesc, String crimePlace,
                 String date, String time, String email) {
        this.username = username;
        this.phone = phone;
        this.state = state;
        this.crimeTypes = crimeTypes;
        this.crimeDesc = crimeDesc;
        this.crimePlace = crimePlace;
        this.date = date;
        this.time = time;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCrimeTypes() {
        return crimeTypes;
    }

    public void setCrimeTypes(String crimeTypes) {
        this.crimeTypes = crimeTypes;
    }

    public String getCrimeDesc() {
        return crimeDesc;
    }

    public void setCrimeDesc(String crimeDesc) {
        this.crimeDesc = crimeDesc;
    }

    public String getCrimePlace() {
        return crimePlace;
    }

    public void setCrimePlace(String crimePlace) {
        this.crimePlace = crimePlace;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
