self.addEventListener('push', event => {
  let title = 'FleetFinder';
  let body = 'New notification'

  if(event.data) {
    try {
      const data = event.data.json();
      title = data.title;
      body = data.body;
    }
    catch {
      body = event.data.text();
    }
  }

  const data = event.data?.json();
  self.registration.showNotification(title, {
    body: body,
    icon: '/assets/icons/icon-192x192.png'
  });
});
