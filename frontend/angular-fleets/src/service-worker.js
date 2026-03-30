self.addEventListener('push', event => {
  let title = 'FleetFinder';
  let body = 'New notification';
  let payload = {};

  if(event.data) {

    try {
      payload = event.data.json();
      title = payload.title;
      body = payload.body;
      console.log("Push payload", payload);
    }
    catch {
      body = event.data.text();
    }
  } else {
    console.log('event.data is null.');
  }



  event.waitUntil(
    self.registration.showNotification(title, {
      body: body,
      icon: payload.icon || '/assets/icons/icon-192x192.png',
      tag: payload.tag,
      data: payload.data,
      actions: payload.actions,
      requireInteraction: payload.requireInteraction
    })
  )
});

self.addEventListener('notificationclick', event => {
  event.notification.close();
  if (event.action === 'view' || !event.action || event.action === '') {
    const url = event.notification.data?.url || self.location.origin
    event.waitUntil(clients.openWindow(url));
  }
});

