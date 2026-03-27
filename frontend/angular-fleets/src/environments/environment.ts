import {LogLevel} from "angular-auth-oidc-client";

//TODO GENERATE PROD VAPID KEY PAIR

export const environment = {
  production: false,
  apiBaseUrl: '/api',
  wsBaseUrl: "ws://localhost:8080",
  vapidPublicKey: 'BJ6GO7eYzYx0FhmPPyw0D0aU-E1KC6tEt8_XZvD0zZDlgshBEGBBPnhb2hTRRjK9B2segWYfsjLufSLte_0INUU',
  oidc: {
    authority: 'http://localhost:8180/realms/oauthrealm',
    redirectUrl: window.location.origin,
    postLogoutRedirectUri: window.location.origin,
    clientId: 'fleetfinder-frontend',
    responseType: 'code',
    scope: 'openid profile email',
    useRefreshToken: true,
    silentRenew: true,
    logLevel: LogLevel.Debug,
    loadUserInfo: false,
    autoUserInfo: false,
  }
}
