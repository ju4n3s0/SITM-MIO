package com.sitm.mio.citizen.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data Transfer Object representing information provided to a citizen.
 * Contains a message with travel time or route information.
 */
public class CitizenInformation {

    private final String message;

    @JsonCreator
    public CitizenInformation(@JsonProperty("message") String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}