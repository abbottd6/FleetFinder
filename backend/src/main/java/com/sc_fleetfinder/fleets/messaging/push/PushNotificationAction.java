package com.sc_fleetfinder.fleets.messaging.push;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PushNotificationAction {

    private String action;
    private String title;
}
