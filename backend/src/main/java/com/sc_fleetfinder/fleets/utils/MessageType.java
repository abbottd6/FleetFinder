package com.sc_fleetfinder.fleets.utils;

public enum MessageType {
    TEXT,
    SYSTEM;

    public static MessageType fromString(String value) {
        if(value == null) {
            throw new IllegalArgumentException("messageType requires non-null for new message");
        }
        try {
            return MessageType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid messageType: " + value);
        }
    }
}
