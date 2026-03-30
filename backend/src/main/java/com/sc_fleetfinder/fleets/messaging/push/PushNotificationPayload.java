package com.sc_fleetfinder.fleets.messaging.push;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PushNotificationPayload {

    private String title;
    private String body;
    private String icon;
    private String tag;
    private List<PushNotificationAction> actions = new ArrayList<>();
    private boolean requireInteraction;
    private PushNotificationDataField data;

}
