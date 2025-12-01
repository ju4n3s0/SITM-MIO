package com.sitm.mio.citizen.dto;

/**
 * Data Transfer Object representing information provided to a citizen.
 * Contains a message with travel time or route information.
 */
public class CitizenInformation {

    private final String message;

    public CitizenInformation(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}