package com.sc_fleetfinder.fleets.messaging.push;

import com.sc_fleetfinder.fleets.entities.PushSubscription;
import com.sc_fleetfinder.fleets.utils.ExternalNotifcationResult;
import lombok.extern.slf4j.Slf4j;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import org.apache.http.HttpResponse;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class PushNotificationService {

    @Value("${vapid.public.key}")
    private String publicKey;

    @Value("${vapid.private.key}")
    private String privateKey;

    public Map<ExternalNotifcationResult, HttpStatus> sendPushNotification(PushSubscription pushSub, String payload) throws Exception{

        String endpoint = pushSub.getDeviceUrl();
        String p256dh = pushSub.getPublicKey();
        String auth = pushSub.getBrowserSecret();

        PushService pushService = new PushService()
                .setPublicKey(publicKey)
                .setPrivateKey(privateKey);

        // this is NOT the notification entity from fleetfinder,
        // this is from 'martijndwars.webpush.Notification'
        Notification pushNote = new Notification(endpoint, p256dh, auth, payload);

        HttpResponse response = pushService.send(pushNote);

        Map<ExternalNotifcationResult, HttpStatus> result = new HashMap<>();

        if(response.getStatusLine().getStatusCode() == 201) {
            result.put(ExternalNotifcationResult.SUCCESS, HttpStatus.CREATED);
        } else {
            HttpStatus status = HttpStatus.valueOf(response.getStatusLine().getStatusCode());
            result.put(ExternalNotifcationResult.FAILURE, status);
        }

        log.warn("PUSH SUBSCRIPTION SEND RESULT: {} \n PUSH SUBSCRIPTION RESPONSE LINE: {}", result, response);
        return result;
    }
}
