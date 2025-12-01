package com.sitm.mio.datacenter.model;


//DTO with operator credentials coming from OperationControl module.

public class OperatorCredentials {

    private final String username;
    private final String password;

    public OperatorCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return username; }

    public String getPassword() { return password; }

    @Override
    public String toString() {
        return "OperatorCredentials{username='" + username + "'}";
    }
}