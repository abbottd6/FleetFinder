package com.sc_fleetfinder.fleets.messaging.push;

import com.sc_fleetfinder.fleets.entities.PushSubscription;
import lombok.extern.slf4j.Slf4j;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PushNotificationService {

    @Value("${vapid.public.key}")
    private String publicKey;

    @Value("${vapid.private.key}")
    private String privateKey;

    public void sendPushNotification(PushSubscription pushSub, String payload) throws Exception{

        String endpoint = pushSub.getDeviceUrl();
        String p256dh = pushSub.getPublicKey();
        String auth = pushSub.getBrowserSecret();

        PushService pushService = new PushService()
                .setPublicKey(publicKey)
                .setPrivateKey(privateKey);

        // this is NOT the notification entity from my app,
        // this is from 'martijndwars.webpush.Notification'
        Notification pushNote = new Notification(endpoint, p256dh, auth, payload);

        HttpResponse response = pushService.send(pushNote);
        log.warn("push service response: {}", response.getStatusLine());
    }
}
