package com.ote.otedeliveries.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Login implements Serializable {
    @SerializedName("username")
    private String username;

    @SerializedName("loggedin")
    private boolean loggedin;

    @SerializedName("loggedin_before")
    private boolean loggedinBefore;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isLoggedin() {
        return loggedin;
    }

    public void setLoggedin(boolean loggedin) {
        this.loggedin = loggedin;
    }

    public boolean isLoggedinBefore() {
        return loggedinBefore;
    }

    public void setLoggedinBefore(boolean loggedinBefore) {
        this.loggedinBefore = loggedinBefore;
    }
}
