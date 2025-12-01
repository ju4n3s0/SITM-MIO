package com.sitm.mio.datacenter.model;

import java.util.List;

public class OperatorAuthResult {

    private final long operatorId;
    private final String username;
    private final List<String> assignedZones;
    private final String token;

    public OperatorAuthResult(long operatorId, String username, 
            List<String> assignedZones, String token) {
        this.operatorId = operatorId;
        this.username = username;
        this.assignedZones = assignedZones;
        this.token = token;
    }

    public long getOperatorId() {
        return operatorId;
    }

    public String getUsername() {
        return username;
    }

    public List<String> getAssignedZones() {
        return assignedZones;
    }

    public String getToken() {
        return token;
    }

    @Override
    public String toString() {
        return "OperatorAuthResult{" +
                "operatorId=" + operatorId +
                ", username='" + username + '\'' +
                ", assignedZones=" + assignedZones +
                ", token='" + token + '\'' +
                '}';
    }
}