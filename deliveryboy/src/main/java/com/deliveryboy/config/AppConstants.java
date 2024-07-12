package com.deliveryboy.config;

public enum AppConstants {
    LOCATION_TOPIC_NAME("location-update-topic");

    public final String value;

    AppConstants(String value) {
        this.value = value;
    }
}
