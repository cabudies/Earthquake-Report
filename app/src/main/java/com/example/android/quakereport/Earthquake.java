package com.example.android.quakereport;

/**
 * Created by gurjas on 07-06-2017.
 */

public class Earthquake {

    private double earthquakeMagnitude;
    private String earthquakeOriginPlace;
    private long earthquakeOccurringDate;
    private String earthQuakeCheckURL;

    public Earthquake(double magnitude, String origin, long date, String receivedURL)
    {
        earthquakeMagnitude = magnitude;
        earthquakeOriginPlace = origin;
        earthquakeOccurringDate = date;
        earthQuakeCheckURL = receivedURL;
    }

    public double getEarthquakeMagnitude() {
        return earthquakeMagnitude;
    }

    public long getEarthquakeOccurringDate() {
        return earthquakeOccurringDate;
    }

    public String getEarthquakeOriginPlace() {
        return earthquakeOriginPlace;
    }

    public String getEarthQuakeCheckURL() {
        return earthQuakeCheckURL;
    }
}
