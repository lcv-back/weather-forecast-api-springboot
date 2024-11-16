package com.skyapi.weatherforecast.location;


public class LocationNotFoundException extends Exception {

    public LocationNotFoundException(String message) {
        super(message);
    }

    public LocationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
