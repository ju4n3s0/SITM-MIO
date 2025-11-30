package com.sitm.mio.proxyserver.dto;

/**
 * Data Transfer Object representing a request for citizen information.
 * Contains origin and destination stop IDs for travel time queries.
 * 
 * Note: This is a copy of the DTO from the Citizen module.
 * Both modules run independently on different machines.
 */
public class GetCitizenInformationRequest {

    private final long originId;
    private final long destinationId;

    public GetCitizenInformationRequest(long originId, long destinationId) {
        this.originId = originId;
        this.destinationId = destinationId;
    }

    public long getOriginId() {
        return originId;
    }

    public long getDestinationId() {
        return destinationId;
    }

    @Override
    public String toString() {
        return "GetCitizenInformationRequest{originId=" + originId + ", destinationId=" + destinationId + "}";
    }
}
