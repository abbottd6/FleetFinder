import {LogLevel} from "angular-auth-oidc-client";

export const environment = {
  production: false,
  webAppBaseUrl: 'http://localhost:4200',
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
    logLevel: LogLevel.None,
    loadUserInfo: false,
    autoUserInfo: false,
  }
}
