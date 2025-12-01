package com.sitm.mio.proxyserver.dto;

/**
 * Data Transfer Object representing information provided to a citizen.
 * Contains a message with travel time or route information.
 * 
 * Note: This is a copy of the DTO from the Citizen module.
 * Both modules run independently on different machines.
 */
public class CitizenInformation {

    private final String message;

    public CitizenInformation(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "CitizenInformation{message='" + message + "'}";
    }
}
