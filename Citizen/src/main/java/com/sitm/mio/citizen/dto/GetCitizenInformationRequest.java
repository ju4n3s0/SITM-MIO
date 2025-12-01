package com.sitm.mio.citizen.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data Transfer Object representing a request for citizen information.
 * Contains origin and destination stop IDs for travel time queries.
 */
public class GetCitizenInformationRequest {

    private final long originId;
    private final long destinationId;

    /**
     * Constructor for Jackson deserialization.
     */
    @JsonCreator
    public GetCitizenInformationRequest(
            @JsonProperty("originId") long originId, 
            @JsonProperty("destinationId") long destinationId) {
        this.originId = originId;
        this.destinationId = destinationId;
    }

    public long getOriginId() {
        return originId;
    }

    public long getDestinationId() {
        return destinationId;
    }
}